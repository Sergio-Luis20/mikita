package mikita.miscellaneous.stringgen;

import org.springframework.stereotype.Component;

@Component
public class DecimalGenerator extends AbstractStringGenerator {

    public DecimalGenerator() {
        super("Decimal", NUMERIC, true);
    }

}
