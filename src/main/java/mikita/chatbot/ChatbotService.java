package mikita.chatbot;

import mikita.exception.ImplementationException;
import mikita.util.Implementations;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ChatbotService {

    private final Chatbot chatbot;

    public ChatbotService(Implementations implementations) {
        try {
            this.chatbot = implementations.getCurrentImplementationInstance(Chatbot.class);
        } catch (ImplementationException e) {
            throw new RuntimeException("Could not initialize ChatbotService", e);
        }
    }

    public ChatbotOutput predict(ChatbotInput input) {
        return chatbot.predict(Objects.requireNonNull(input, "input"));
    }

}
