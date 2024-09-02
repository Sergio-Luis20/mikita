package mikita.discord.command.slash;

import mikita.chatbot.ChatbotInput;
import mikita.chatbot.ChatbotMessage;
import mikita.chatbot.ChatbotOutput;
import mikita.chatbot.ChatbotService;
import mikita.discord.DiscordService;
import mikita.message.Messageable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class DoTheLSlash extends AbstractMikitaDiscordSlash {

    private ChatbotService chatbotService;

    public DoTheLSlash(DiscordService discordService, ChatbotService chatbotService) {
        super(discordService, "fazol", "Pergunte algo à Mikita que possa ser respondido com \"sim\" ou \"não\".");
        this.chatbotService = chatbotService;
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event, InteractionHook hook) {
        Messageable log = discordService.getLog();
        String ask;
        try {
            ask = event.getOption("pergunta").getAsString();
        } catch (NullPointerException e) {
            hook.sendMessage("Erro interno.").queue(null, throwable ->
                    log.sendErrorMessage("Falha ao enviar mensagem de erro ao usuário", throwable));
            log.sendErrorMessage("Opção \"pergunta\" de um comando FazOL nula.", e, true);
            return;
        }

        OffsetDateTime created = event.getTimeCreated();
        ChatbotMessage message = new ChatbotMessage(ask);
        ChatbotInput input = new ChatbotInput(DiscordService.getInteractionAuthorName(event),
                created.toLocalDateTime(), message);
        ChatbotOutput output = chatbotService.predict(input);
        hook.sendMessage(output.getMessage().getText()).queue(null, throwable -> log.sendErrorMessage(
                "Não foi possível responder à requisição de comando FazOL.", throwable));
    }

    @Override
    public List<Option> options() {
        Option text = new Option(OptionType.STRING, "pergunta", "Sua pergunta.");
        return List.of(text);
    }
}
