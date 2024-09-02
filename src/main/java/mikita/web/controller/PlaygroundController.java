package mikita.web.controller;

import mikita.exception.TextTooLongException;
import mikita.miscellaneous.MessageOfTheDay;
import mikita.util.ResponseMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping("/playground")
public class PlaygroundController {

    @GetMapping("/motd")
    public ResponseEntity<?> motd(@RequestParam String text, @RequestParam(required = false) String color) {
        MessageOfTheDay motd = new MessageOfTheDay(text);
        try {
            if (color != null) {
                if (color.startsWith("0x")) {
                    color = color.substring(2);
                } else if (color.startsWith("#")) {
                    color = color.substring(1);
                }
                int argb = Integer.parseInt(color, 16);
                if (argb >> 24 == 0) {
                    argb |= 0xff << 24;
                }
                motd.setColor(new Color(argb));
            }

            byte[] image = motd.getImageAsBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(image.length);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Not a 4 byte hex integer color"));
        } catch (TextTooLongException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
