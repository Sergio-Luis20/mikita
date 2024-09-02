package mikita.message;

import org.springframework.stereotype.Component;

@Component
public class GlobalOutput extends RecipientGroup {

    public GlobalOutput(Messageable... messageables) {
        super(messageables);
    }

}
