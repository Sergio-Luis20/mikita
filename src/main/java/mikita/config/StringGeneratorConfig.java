package mikita.config;

import mikita.miscellaneous.stringgen.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class StringGeneratorConfig {

    @Bean
    public AlphabeticGenerator alphabeticGeneratorUpper() {
        return new AlphabeticGenerator(LetterCase.UPPER);
    }

    @Bean
    public AlphabeticGenerator alphabeticGeneratorLower() {
        return new AlphabeticGenerator(LetterCase.LOWER);
    }

    @Bean
    @Primary
    public AlphabeticGenerator alphabeticGenerator() {
        return new AlphabeticGenerator(LetterCase.BOTH);
    }

    @Bean
    public AlphanumericGenerator alphanumericGeneratorUpper() {
        return new AlphanumericGenerator(LetterCase.UPPER);
    }

    @Bean
    public AlphanumericGenerator alphanumericGeneratorLower() {
        return new AlphanumericGenerator(LetterCase.LOWER);
    }

    @Bean
    @Primary
    public AlphanumericGenerator alphanumericGenerator() {
        return new AlphanumericGenerator(LetterCase.BOTH);
    }

    @Bean
    public BinaryGenerator binaryGenerator() {
        return new BinaryGenerator();
    }

    @Bean
    public OctalGenerator octalGenerator() {
        return new OctalGenerator();
    }

    @Bean
    public DecimalGenerator decimalGenerator() {
        return new DecimalGenerator();
    }

    @Bean
    public HexadecimalGenerator hexadecimalGeneratorUpper() {
        return new HexadecimalGenerator(LetterCase.UPPER);
    }

    @Bean
    @Primary
    public HexadecimalGenerator hexadecimalGeneratorLower() {
        return new HexadecimalGenerator(LetterCase.LOWER);
    }

    @Bean
    public HexadecimalGenerator hexadecimalGenerator() {
        return new HexadecimalGenerator(LetterCase.BOTH);
    }

    @Bean
    @Primary
    public Base64Generator base64GeneratorSecureUrl() {
        return new Base64Generator(true);
    }

    @Bean
    public Base64Generator base64Generator() {
        return new Base64Generator(false);
    }

}
