package mikita.discord;

import mikita.message.Messageable;
import mikita.message.RecipientGroup;
import org.springframework.stereotype.Component;

@Component
public class DiscordMessageReceivedOutput extends RecipientGroup {

    public DiscordMessageReceivedOutput(Messageable... recipients) {
        addAll(recipients);
    }

}
