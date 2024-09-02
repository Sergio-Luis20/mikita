package mikita.chatbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArrayDeterministicChatbot implements Chatbot {

    private static final String RESOURCE_LOCATION = "/chatbot/array-answers.txt";

    private final List<String> answers = new ArrayList<>();

    public ArrayDeterministicChatbot() {
        try (InputStream stream = getClass().getResourceAsStream(RESOURCE_LOCATION)) {
            if (stream == null) {
                throw new NullPointerException("Resource \"" + RESOURCE_LOCATION + "\" does not exist");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                for (String line; (line = reader.readLine()) != null;) {
                    answers.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load resource \"" + RESOURCE_LOCATION + "\"", e);
        }
    }

    @Override
    public ChatbotOutput predict(ChatbotInput input) {
        int index = ThreadLocalRandom.current().nextInt(answers.size());
        ChatbotMessage message = new ChatbotMessage(answers.get(index));
        return new ChatbotOutput(LocalDateTime.now(), message);
    }

}
