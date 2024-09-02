package mikita.io.cli;

import mikita.command.AbstractCommand;
import mikita.command.CommandEntry;
import mikita.command.CommandOutput;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExitCommand extends AbstractCommand {

    private final ApplicationContext context;

    public ExitCommand(CommandOutput output, ApplicationContext context) {
        super(output);
        this.context = context;
    }

    @Override
    protected Map<String, String> helpMap() {
        Map<String, String> helpMap = new HashMap<>();
        helpMap.put("", "Finaliza a aplicação");
        return helpMap;
    }

    @CommandEntry
    public void exit() {
        Thread.ofPlatform().daemon().start(() -> SpringApplication.exit(context, () -> 0));
    }

    @Override
    public String getName() {
        return "exit";
    }

}
