package mikita.config;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import mikita.util.MikitaPrettyPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public PrettyPrinter prettyPrinter() {
        return new MikitaPrettyPrinter();
    }

    @Configuration
    public static class ObjectMapperConfiguration {

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private PrettyPrinter prettyPrinter;

        @PostConstruct
        public void configureObjectMapper() {
            objectMapper.setDefaultPrettyPrinter(prettyPrinter);
        }

    }

}
