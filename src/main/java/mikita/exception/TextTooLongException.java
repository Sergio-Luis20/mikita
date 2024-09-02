package mikita.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.StandardException;

@Getter
@Setter
@StandardException
public class TextTooLongException extends Exception {

    private String text;

}
