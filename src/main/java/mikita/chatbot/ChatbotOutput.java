package mikita.chatbot;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class ChatbotOutput {

    private String senderName;
    private LocalDateTime timestamp;
    private ChatbotMessage message;

    public ChatbotOutput(LocalDateTime timestamp, ChatbotMessage message) {
        this(Chatbot.NAME, timestamp, message);
    }

    public ChatbotOutput(String senderName, LocalDateTime timestamp, ChatbotMessage message) {
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.message = message;
    }

}
