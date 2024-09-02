package mikita.miscellaneous.stringgen;

import org.springframework.stereotype.Component;

@Component
public class Base64Generator extends AbstractStringGenerator {

    public static final String STANDARD = "+/";
    public static final String SECURE_URL = "-_";

    public Base64Generator(boolean secureUrl) {
        super("Base 64 " + (secureUrl ? "(secure url)" : "(standard)"),
                NUMERIC + UPPERS + LOWERS + (secureUrl ? SECURE_URL : STANDARD));
    }

}
