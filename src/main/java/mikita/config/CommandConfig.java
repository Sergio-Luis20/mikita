package mikita.config;

import jakarta.annotation.PostConstruct;
import mikita.command.CommandContainer;
import mikita.command.MikitaCommand;
import mikita.exception.MikitaCommandException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Configuration
public class CommandConfig {

    @Autowired
    private CommandContainer container;

    @Autowired
    private List<MikitaCommand> commands;

    @PostConstruct
    public void addCommands() {
        try {
            Method method = CommandContainer.class.getDeclaredMethod("addCommands", Collection.class);
            method.setAccessible(true);
            method.invoke(container, commands);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MikitaCommandException("Could not add commands to container", e);
        }
    }

}
