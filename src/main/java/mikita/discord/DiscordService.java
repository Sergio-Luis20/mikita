package mikita.discord;

import lombok.Getter;
import mikita.BatchList;
import mikita.exception.JDAInitializationException;
import mikita.message.Messageable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Service
public class DiscordService implements AutoCloseable, Messageable {

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-z0-9-]{1,32}");
    public static final int PAGE_SIZE = 5;

    private final JDA jda;
    private final long selfId;
    private final long mainGuildId;
    private boolean closed;
    private DiscordPosition position;
    private Messageable log;

    public DiscordService(String token, long mainGuildId, Messageable log) throws JDAInitializationException {
        jda = JDABuilder.createDefault(token)
                .setEnabledIntents(Arrays.asList(GatewayIntent.values()))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.EMOJI)
                .enableCache(CacheFlag.ONLINE_STATUS)
                .build();

        this.mainGuildId = mainGuildId;
        this.log = log;

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new JDAInitializationException("JDA could not get ready", e);
        }

        selfId = jda.getSelfUser().getIdLong();
        position = new DiscordPosition(this);
    }

    public void registerEvents(EventListener[] listeners) {
        jda.addEventListener(Arrays.stream(listeners).filter(Objects::nonNull).toArray());
    }

    public void updateSlashCommands(boolean forceUpdate, SlashCommandData... slashs) {
        Map<String, SlashCommandData> commandMap = Arrays.stream(slashs).filter(Objects::nonNull)
                .collect(Collectors.toConcurrentMap(CommandData::getName, slash -> slash));
        if (forceUpdate) {
            for (Guild guild : getGuilds()) {
                Collection<SlashCommandData> commands = commandMap.values();
                guild.updateCommands().addCommands(commands).queue(null, throwable -> log.sendErrorMessage(
                        "Não foi possível atualizar os " + commands.size() + " comandos a seguir: "
                                + commands, throwable));
            }
            return;
        }
        for (Guild guild : getGuilds()) {
            guild.retrieveCommands().queue(commands -> {
                Set<SlashCommandData> toBeAdded = new HashSet<>();
                Set<String> names = commands.stream()
                        .filter(cmd -> cmd.getApplicationIdLong() == selfId)
                        .map(Command::getName)
                        .collect(Collectors.toSet());
                for (Entry<String, SlashCommandData> entry : commandMap.entrySet()) {
                    if (!names.contains(entry.getKey())) {
                        toBeAdded.add(entry.getValue());
                    }
                }
                if (!toBeAdded.isEmpty()) {
                    guild.updateCommands().addCommands(toBeAdded.toArray(SlashCommandData[]::new))
                            .queue(null, throwable -> log.sendErrorMessage(
                                    "Não foi possível atualizar os " + toBeAdded.size()
                                            + " comandos a seguir: " + toBeAdded, throwable));
                }
            }, throwable -> log.sendErrorMessage("Não foi possível obter os comandos do servidor "
                    + guild.getName() + " (" + guild.getIdLong() + ")", throwable));
        }
    }

    public boolean reply(long messageId, String response) {
        Objects.requireNonNull(response, "response");
        if (!position.isInTextChannel()) {
            return false;
        }
        TextChannel channel = position.getCurrentTextChannel();
        channel.retrieveMessageById(messageId).queue(message -> message.reply(response).queue(),
                throwable -> log.sendErrorMessage("Não foi possível obter a mensagem pelo id " + messageId, throwable));
        return true;
    }

    public boolean sendFile(InputStream file, String fileName) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(fileName, "fileName");
        if (!position.isInTextChannel()) {
            return false;
        }
        if (fileName.isBlank()) {
            return false;
        }
        FileUpload upload = FileUpload.fromData(file, fileName);
        position.getCurrentTextChannel().sendFiles(upload).queue(success -> {
                    try {
                        upload.close();
                    } catch (IOException e) {
                        log.sendErrorMessage("Não foi possível fechar um FileUpload. " +
                                "Nome do arquivo: " + fileName, e, true);
                    }
                },
                throwable -> log.sendErrorMessage("Não foi possível enviar o arquivo " + fileName, throwable));
        return true;
    }

    public String formatMessage(Message message) {
        long messageId = message.getIdLong();

        String content = "(" + messageId + ") " + getMessageAuthorName(message) + ": " + message.getContentDisplay();
        String[] urlAttachs = message.getAttachments().stream().map(Attachment::getUrl).toArray(String[]::new);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(content);
        if(urlAttachs.length != 0) {
            stringBuilder.append("\n");
            for(int i = 0; i < urlAttachs.length; i++) {
                stringBuilder.append("(Anexo-" + (i + 1) + ": " + urlAttachs[i] + ")"
                        + (i == urlAttachs.length - 1 ? "\n" : ""));
            }
        }

        return stringBuilder.toString();
    }

    public List<Guild> getGuilds() {
        return jda.getGuilds();
    }

    public BatchList<Guild> listGuilds() {
        return getGuilds().stream().collect(BatchList.collector(PAGE_SIZE));
    }

    public Guild getMainGuild() {
        return getGuild(mainGuildId);
    }

    public Guild getGuild(long id) {
        return jda.getGuildById(id);
    }

    public User getUser(long id) {
        return jda.getUserById(id);
    }

    public User getSelfUser() {
        return jda.getSelfUser();
    }

    public boolean isOnline(Member member) {
        return switch (member.getOnlineStatus()) {
            case ONLINE, IDLE, DO_NOT_DISTURB -> true;
            default -> false;
        };
    }

    @Override
    public void sendMessage(String message) {
        TextChannel channel = position.getCurrentTextChannel();
        if (channel == null) {
            return;
        }
        channel.sendMessage(message).queue(null, fail -> log.sendErrorMessage(fail.getMessage(), fail));
    }

    @Override
    public void sendWarnMessage(String warnMessage) {
        sendMessage("Warn: " + warnMessage);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        sendMessage("Error: " + errorMessage);
    }

    @Override
    public void sendErrorMessage(String errorMessage, Throwable throwable) {
        sendMessage("Error: " + errorMessage + " (" + throwable + ")");
    }

    public static String memberName(Member member) {
        if (member == null) {
            return "null";
        }
        String nickName = member.getNickname();
        if(nickName != null) {
            return nickName;
        }
        return member.getEffectiveName();
    }

    public static String getMessageAuthorName(Message message) {
        Member member = message.getMember();
        return member == null ? message.getAuthor().getEffectiveName() : memberName(member);
    }

    public static String getInteractionAuthorName(Interaction interaction) {
        Member member = interaction.getMember();
        return member == null ? interaction.getUser().getEffectiveName() : memberName(member);
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            return;
        }
        jda.shutdown();
        if (!jda.awaitShutdown(Duration.ofSeconds(5))) {
            jda.shutdownNow();
            jda.awaitShutdown();
        }
        closed = true;
    }

}
