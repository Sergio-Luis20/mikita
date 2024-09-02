package mikita.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import mikita.chatbot.ChatbotInput;
import mikita.chatbot.ChatbotMessage;
import mikita.chatbot.ChatbotOutput;
import mikita.chatbot.ChatbotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private ChatbotService chatbotService;
    private Map<String, String> names;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
        this.names = new HashMap<>();
    }

    @GetMapping
    public ModelAndView index() {
        return okModel("intro");
    }

    @GetMapping("/conversation")
    public ModelAndView conversation(HttpServletRequest request, @RequestParam String name) {
        names.put(request.getRemoteAddr(), name);
        return okModel("conversation");
    }

    @GetMapping("/predict")
    public ResponseEntity<ChatbotOutput> predict(HttpServletRequest request, @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime timestamp, @RequestParam String message) {
        String name = names.get(request.getRemoteAddr());
        if (name == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ChatbotInput input = new ChatbotInput(name, timestamp, new ChatbotMessage(message));
        return ResponseEntity.ok(chatbotService.predict(input));
    }

    @GetMapping("/name")
    public ResponseEntity<String> getName(HttpServletRequest request) {
        String name = names.get(request.getRemoteAddr());
        return name == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(name);
    }

    private ModelAndView okModel(String modelName) {
        ModelAndView modelAndView = new ModelAndView("chatbot/" + modelName);
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }

}
