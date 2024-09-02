package mikita.discord.event;

import mikita.discord.DiscordMessageReceivedOutput;
import mikita.discord.DiscordPosition;
import mikita.discord.DiscordService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiving extends ListenerAdapter {

    private DiscordMessageReceivedOutput output;
    private DiscordPosition position;
    private DiscordService service;

    public MessageReceiving(DiscordMessageReceivedOutput output, DiscordService service) {
        this.output = output;
        this.service = service;
        this.position = service.getPosition();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Member member = event.getMember();

        if(!position.isInTextChannel()
                || position.getCurrentTextChannel().getIdLong() != event.getChannel().getIdLong()
                || !position.isListeningMessages()
                || member != null && member.getIdLong() == service.getSelfId()) {
            return;
        }

        output.sendMessage(service.formatMessage(event.getMessage()));
    }

}
