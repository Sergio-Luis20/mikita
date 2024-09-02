package mikita.miscellaneous.stringgen;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class AbstractStringGenerator implements StringGenerator, Serializable {

    public static final String UPPERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMERIC = "0123456789";

    private final String name, characters;
    private final boolean numeric;

    public AbstractStringGenerator(String name, String characters) {
        this(name, characters, false);
    }

    public AbstractStringGenerator(String name, String characters, boolean numeric) {
        this.name = Objects.requireNonNull(name);
        this.characters = Objects.requireNonNull(characters);
        this.numeric = numeric;

        if (name.isBlank()) {
            throw new IllegalArgumentException("blank name");
        }
        if (characters.isBlank()) {
            throw new IllegalArgumentException("blank characters");
        }
    }

    @Override
    public String generate(int length, boolean zeroStartAllowed) {
        if (length < 0) {
            throw new IllegalArgumentException("negative length: " + length);
        } else if (length == 0) {
            return "";
        } else {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int charAmount = characters.length();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < length; i++) {
                if (i == 0 && !zeroStartAllowed && numeric) {
                    char c;
                    do {
                        c = characters.charAt(random.nextInt(charAmount));
                    } while (c == '0');
                    result.append(c);
                } else {
                    result.append(characters.charAt(random.nextInt(charAmount)));
                }
            }
            return result.toString();
        }
    }

}
