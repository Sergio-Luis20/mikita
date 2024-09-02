package mikita.discord.command.slash;

import mikita.discord.DiscordService;
import mikita.exception.TextTooLongException;
import mikita.message.Messageable;
import mikita.miscellaneous.MessageOfTheDay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class MotdSlash extends AbstractMikitaDiscordSlash {

    public MotdSlash(DiscordService discordService) {
        super(discordService, "motd", "Envia uma mensagem no formato Message Of The Day (MOTD).");
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event, InteractionHook hook) {
        Messageable log = discordService.getLog();
        String text;
        try {
            text = event.getOption("texto").getAsString();
        } catch (NullPointerException e) {
            hook.sendMessage("Erro interno.").queue(null, throwable ->
                    log.sendErrorMessage("Falha ao enviar mensagem de erro ao usuário", throwable));
            log.sendErrorMessage("Opção \"texto\" de um MOTD nula.", e, true);
            return;
        }
        MessageOfTheDay motd = new MessageOfTheDay(text);
        OptionMapping color = event.getOption("cor");
        if (color != null) {
            String hex = color.getAsString();
            if (hex.startsWith("0x")) {
                hex = hex.substring(2);
            } else if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            try {
                int hexColor = Integer.parseInt(hex, 16);
                if (hexColor >> 24 == 0) {
                    hexColor |= 0xff << 24;
                }
                motd.setColor(new Color(hexColor));
            } catch (NumberFormatException e) {
                hook.sendMessage("Cor inválida: " + color.getAsString()).queue(null,
                        throwable -> log.sendErrorMessage("Não foi possível " +
                                "enviar mensagem de cor inválida via hook na execução de um slash command MOTD.", throwable));
                return;
            }
        } else {
            motd.setColor(Color.WHITE);
        }
        byte[] image;
        try {
            image = motd.getImageAsBytes();
        } catch (TextTooLongException e) {
            hook.sendMessage("Texto muito longo.").queue(null, throwable -> log
                    .sendErrorMessage("Não foi possível enviar mensagem de erro de " +
                            "texto de MOTD muito longo ao usuário.", throwable));
            return;
        }
        FileUpload upload = FileUpload.fromData(image, "mikita-motd-" + Instant.now() + ".png");
        hook.sendFiles(upload).queue(success -> {
            try {
                upload.close();
            } catch (IOException e) {
                log.sendErrorMessage("Não foi possível fechar um FileUpload. " +
                        "Nome do arquivo: " + upload.getName(), e, true);
            }
        }, throwable -> log.sendErrorMessage("Não foi possível enviar o arquivo: " + upload.getName(), throwable));
    }

    @Override
    public List<Option> options() {
        Option text = new Option(OptionType.STRING, "texto", "O texto que será exibito no MOTD.");

        Choice[] choices = new Choice[] {
            new Choice("preto", "000000"),
            new Choice("azul escuro", "0000aa"),
            new Choice("verde escuro", "00aa00"),
            new Choice("ciano minecraft", "00aaaa"),
            new Choice("vermelho escuro", "aa0000"),
            new Choice("roxo", "aa00aa"),
            new Choice("laranja", "ffaa00"),
            new Choice("cinza", "aaaaaa"),
            new Choice("cinza escuro", "555555"),
            new Choice("azul eclesiástico", "5555ff"),
            new Choice("índigo", "0a3876"),
            new Choice("verde lima", "55ff55"),
            new Choice("azul claro", "55ffff"),
            new Choice("vermelho claro", "ff5555"),
            new Choice("rosa", "ff55ff"),
            new Choice("amarelo claro", "ffff55"),
            new Choice("verde claro", "55ff55"),
            new Choice("vermelho", "ff0000"),
            new Choice("verde", "00ff00"),
            new Choice("azul", "0000ff"),
            new Choice("amarelo", "ffff00"),
            new Choice("ciano", "00ffff"),
            new Choice("lilás", "ff00ff"),
            new Choice("magenta", "ff0080"),
            new Choice("branco", "ffffff")
        };

        Option color = new Option(OptionType.STRING, "cor", "Cor em hexadecimal de até " +
                "4 bytes, precedido ou não por 0x ou #, ou uma cor predefinida.", false, choices);

        return List.of(text, color);
    }

}
