package mikita.miscellaneous.stringgen;

import org.springframework.stereotype.Component;

@Component
public class OctalGenerator extends AbstractStringGenerator {

    public static final String OCTAL = "01234567";

    public OctalGenerator() {
        super("Octal", OCTAL, true);
    }

}
