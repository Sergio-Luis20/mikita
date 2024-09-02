package mikita.miscellaneous.stringgen;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AlphanumericGenerator extends AbstractStringGenerator implements CaseSensitive {

    private final LetterCase letterCase;

    public AlphanumericGenerator(LetterCase letterCase) {
        super("Alphanumeric " + switch (letterCase) {
            case UPPER -> "(upper case)";
            case LOWER -> "(lower case)";
            case BOTH -> "(standard)";
        }, NUMERIC + switch (letterCase) {
            case UPPER -> UPPERS;
            case LOWER -> LOWERS;
            case BOTH -> UPPERS + LOWERS;
        });
        this.letterCase = letterCase;
    }

}
