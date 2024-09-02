package mikita.web.controller;

import lombok.AllArgsConstructor;
import mikita.miscellaneous.stringgen.StringGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/string-generator")
public class StringGenerationController {

    private Map<String, StringGenerator> generators;

    @GetMapping("/generate")
    public ResponseEntity<?> generateString(@RequestParam int length,
                                            @RequestParam String generator,
                                            @RequestParam(defaultValue = "true") boolean zeroStartAllowed) {
        StringGenerator stringGenerator = generators.get(generator);
        if (stringGenerator == null) {
            return ResponseEntity.badRequest().body("Generator not found: " + generator);
        }

        String generatedString;
        try {
            generatedString = stringGenerator.generate(length, zeroStartAllowed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Negative length: " + length);
        }

        GeneratedString responseBody = new GeneratedString(generatedString, length, zeroStartAllowed, Instant.now());
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/generators")
    public ResponseEntity<List<String>> generators() {
        return ResponseEntity.ok(new ArrayList<>(generators.keySet()));
    }

    public record GeneratedString(
            String generatedString,
            int length,
            boolean zeroStartAllowed,
            Instant timestamp
    ) {
    }

}
