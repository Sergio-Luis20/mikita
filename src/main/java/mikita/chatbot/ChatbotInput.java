package mikita.chatbot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatbotInput {

    private String senderName;
    private LocalDateTime timestamp;
    private ChatbotMessage message;

}
