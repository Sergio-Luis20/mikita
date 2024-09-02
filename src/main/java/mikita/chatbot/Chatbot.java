package mikita.chatbot;

public interface Chatbot {

    String NAME = "Mikita";

    ChatbotOutput predict(ChatbotInput input);

}
