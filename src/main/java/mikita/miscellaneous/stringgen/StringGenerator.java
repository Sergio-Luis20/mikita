package mikita.miscellaneous.stringgen;

public interface StringGenerator {

    boolean isNumeric();

    String generate(int length, boolean zeroStartAllowed);

    default String generate(int length) {
        return generate(length, true);
    }

}
