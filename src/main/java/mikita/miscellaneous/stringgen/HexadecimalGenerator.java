package mikita.miscellaneous.stringgen;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class HexadecimalGenerator extends AbstractStringGenerator implements CaseSensitive {

    public static final String HEX_UPPER = "ABCDEF";
    public static final String HEX_LOWER = "abcdef";

    private final LetterCase letterCase;

    public HexadecimalGenerator(LetterCase letterCase) {
        super("Hexadecimal " + switch (letterCase) {
            case UPPER -> "(upper case)";
            case LOWER -> "(lower case)";
            case BOTH -> "(standard)";
        }, switch (letterCase) {
            case UPPER -> NUMERIC + HEX_UPPER;
            case LOWER -> NUMERIC + HEX_LOWER;
            case BOTH -> NUMERIC + HEX_UPPER + HEX_LOWER;
        }, true);
        this.letterCase = letterCase;
    }

}
