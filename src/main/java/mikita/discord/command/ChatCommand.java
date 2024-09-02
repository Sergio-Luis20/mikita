package mikita.discord.command;

import jakarta.annotation.PostConstruct;
import mikita.command.AbstractCommand;
import mikita.command.CommandContainer;
import mikita.command.CommandEntry;
import mikita.command.CommandOutput;
import mikita.discord.DiscordPosition;
import mikita.discord.DiscordService;
import mikita.exception.DiscordEntityNotFoundException;
import mikita.message.Printer;
import mikita.miscellaneous.MessageOfTheDay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChatCommand extends AbstractCommand {

    public static final int MESSAGE_LOAD_AMOUNT = 100;

    private ApplicationContext context;
    private DiscordService service;
    private CommandContainer container;
    private DiscordPosition position;

    public ChatCommand(CommandOutput output, DiscordService service, ApplicationContext context) {
        super(output);
        this.context = context;
        this.service = service;
        this.position = service.getPosition();
    }

    @PostConstruct
    public void setupCommandContainer() {
        container = context.getBean(CommandContainer.class);
    }

    @Override
    public Map<String, String> helpMap() {
        Map<String, String> helpMap = new HashMap<>();
        helpMap.put("<mensagem>", "Envia uma mensagem no chat atual");
        helpMap.put("enter <id>", "Entra no chat do id inserido");
        helpMap.put("leave", "Sai do chat atual");
        helpMap.put("list", "Lista a primeira página dos chats");
        helpMap.put("list <página>", "Lista a página n dos chats");
        helpMap.put("load", "Obtém as últimas " + MESSAGE_LOAD_AMOUNT + " mensagens do chat");
        helpMap.put("reply <id> <mensagem>", "Responde a mensagem do id fornecido com a mensagem fornecida");
        helpMap.put("listen <true|false>", "Liga ou desliga o recebimento de mensagens no canal atual enquanto estiver nele");
        helpMap.put("motd <mensagem>", "Envia uma mensagem em formato de MOTD (Message Of The Day)");
        return helpMap;
    }

    @CommandEntry
    public void text(String message) {
        if (container.isCommand(message.split(" ")[0])) {
            return;
        }
        if (!position.isInGuild() || !position.isInTextChannel()) {
            output.sendErrorMessage("Você não está em nenhum canal de texto");
        } else {
            service.sendMessage(message);
        }
    }

    @CommandEntry("load")
    public void load() {
        if (!position.isInTextChannel()) {
            output.sendErrorMessage("Você não entrou em nenhum chat");
            return;
        }
        TextChannel channel = position.getCurrentTextChannel();
        output.sendMessage("Obtendo as últimas " + MESSAGE_LOAD_AMOUNT + " mensagens...");
        channel.getHistory().retrievePast(MESSAGE_LOAD_AMOUNT).queue(messages -> {
            for (int i = messages.size() - 1; i >= 0; i--) {
                output.sendMessage(service.formatMessage(messages.get(i)));
            }
        }, throwable -> output.sendErrorMessage("Não foi possível obter as mensagens", throwable));
    }

    @CommandEntry("listen")
    public void listen(boolean listen) {
        if (!position.isInTextChannel()) {
            output.sendErrorMessage("Você não entrou em nenhum chat");
            return;
        }
        Guild guild = position.getCurrentGuild();
        TextChannel channel = position.getCurrentTextChannel();
        position.setListeningMessages(listen);
        output.sendMessage("Recebimento de mensagens %s para o canal %s (%s) no servidor %s (%s)"
                .formatted(listen ? "ativado" : "desativado", channel.getName(), channel.getIdLong(), guild.getName(), guild.getIdLong()));
    }

    @CommandEntry("reply")
    public void reply(long id, String message) {
        if (container.isCommand(message.split(" ")[0])) {
            return;
        }
        if (!position.isInTextChannel()) {
            output.sendWarnMessage("Você não está em nenhum canal de texto");
            return;
        }
        if (!service.reply(id, message)) {
            output.sendErrorMessage("Não foi possível responder a mensagem");
        }
    }

    @CommandEntry("motd")
    public void motd(String message) {
        if (!position.isInTextChannel()) {
            output.sendWarnMessage("Você não está em nenhum canal de texto");
            return;
        }
        MessageOfTheDay motd = new MessageOfTheDay(message);
        motd.setColor(Color.WHITE);
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(motd.getImageAsBytes());
        } catch (Exception e) {
            output.sendErrorMessage("Texto muito grande", e);
            return;
        }
        if (!service.sendFile(bais, "mikita-motd-" + Instant.now() + ".png")) {
            output.sendErrorMessage("Não foi possível enviar o MOTD");
        } else {
            output.sendMessage("MOTD enviado");
        }
    }

    @CommandEntry("enter")
    public void enter(long id) {
        try {
            if (position.enterTextChannel(id)) {
                TextChannel channel = position.getCurrentTextChannel();
                output.sendMessage("Chat " + channel.getName() + " (" + channel.getIdLong() + ") aberto");
            } else {
                output.sendWarnMessage("Você ainda não entrou em nenhum servidor");
            }
        } catch (DiscordEntityNotFoundException e) {
            output.sendErrorMessage("Canal de texto não encontrado para o id: " + id, e);
        }
    }

    @CommandEntry("leave")
    public void leave() {
        position.leaveTextChannel();
        output.sendMessage("Você saiu do canal de texto");
    }

    @CommandEntry("list")
    public void list() {
        list(1);
    }

    @CommandEntry("list")
    public void list(int page) {
        if (!position.isInGuild()) {
            output.sendWarnMessage("Você ainda não entrou em nenhum servidor");
            return;
        }
        Printer.listPageable(output, page, position.getCurrentGuild().getTextChannels(),
                "canais de texto", channel -> "Nome: " + channel.getName() + ". Id: "
                        + channel.getIdLong());
    }

    @Override
    public String getName() {
        return "chat";
    }

}
