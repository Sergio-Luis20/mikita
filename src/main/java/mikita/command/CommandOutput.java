package mikita.command;

import mikita.message.Messageable;
import mikita.message.RecipientGroup;
import org.springframework.stereotype.Component;

@Component
public class CommandOutput extends RecipientGroup {

    public CommandOutput(Messageable... messageables) {
        super(messageables);
    }

}
