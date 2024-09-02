package mikita.miscellaneous.stringgen;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AlphabeticGenerator extends AbstractStringGenerator implements CaseSensitive {

    private final LetterCase letterCase;

    public AlphabeticGenerator(LetterCase letterCase) {
        super("Alphabetic " + switch (letterCase) {
            case UPPER -> "(upper case)";
            case LOWER -> "(lower case)";
            case BOTH -> "(standard)";
        }, switch (letterCase) {
            case UPPER -> UPPERS;
            case LOWER -> LOWERS;
            case BOTH -> UPPERS + LOWERS;
        });
        this.letterCase = letterCase;
    }

}
