package mikita.config;

import mikita.web.UserAgentInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfig {

    @Value("${spring.application.name}")
    private String name;

    @Value("@{spring.application.version}")
    private String version;

    @Bean
    public RestTemplate restTemplate() {
        String userAgent = name + "/" + version;
        return new RestTemplateBuilder()
                .additionalInterceptors(new UserAgentInterceptor(userAgent))
                .build();
    }

}
