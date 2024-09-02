package mikita.config;

import jakarta.annotation.PostConstruct;
import mikita.command.CommandContainer;
import mikita.command.ConsoleProcessor;
import mikita.exception.ConsoleShutdownHookException;
import mikita.io.LineObservable;
import mikita.message.StdMessageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Configuration
public class ConsoleConfig {

    @Bean
    public ConsoleProcessor consoleProcessor(StdMessageable log, CommandContainer container) {
        return new ConsoleProcessor(log, container);
    }

    @Configuration
    public static class ConsoleProcessorConfiguration {

        @Autowired
        private ConsoleProcessor processor;

        @PostConstruct
        public void configureConsoleProcessor() {
            Thread shutdownHook = new Thread(() -> {
                try {
                    Field consoleField = ConsoleProcessor.class.getDeclaredField("console");
                    consoleField.setAccessible(true);
                    LineObservable console = (LineObservable) consoleField.get(processor);
                    Method closing = LineObservable.class.getDeclaredMethod("setClosing");
                    closing.setAccessible(true);
                    closing.invoke(console);
                } catch (Exception e) {
                    throw new ConsoleShutdownHookException("Could not set closing flag " +
                            "to console processor line observable", e);
                }
            });
            shutdownHook.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }

    }

}
