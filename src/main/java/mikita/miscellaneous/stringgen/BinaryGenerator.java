package mikita.miscellaneous.stringgen;

import org.springframework.stereotype.Component;

@Component
public class BinaryGenerator extends AbstractStringGenerator {

    public static final String BINARY = "01";

    public BinaryGenerator() {
        super("Binary", BINARY, true);
    }

}
