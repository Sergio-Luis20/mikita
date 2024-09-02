package mikita.command;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandContainer {

    private final Map<String, MikitaCommand> commands;

    public CommandContainer() {
        commands = new HashMap<>();
    }

    public Map<String, MikitaCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public MikitaCommand getCommandByName(String commandName) {
        return commands.get(commandName);
    }

    public boolean isCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    private void addCommands(Collection<MikitaCommand> commands) {
        for (MikitaCommand command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

}
