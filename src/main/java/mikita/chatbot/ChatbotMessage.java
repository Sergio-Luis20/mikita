package mikita.chatbot;

import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatbotMessage {

    @NonNull
    private String text;
    private List<String> attachments;

}
