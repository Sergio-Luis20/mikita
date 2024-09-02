package mikita.command;

import mikita.io.LineObservable;
import mikita.io.LineObserver;
import mikita.message.Messageable;
import mikita.message.StdMessageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ConsoleProcessor implements LineObserver, AutoCloseable {

    private LineObservable console;
    private CommandContainer container;
    private Messageable log;

    public ConsoleProcessor(StdMessageable log, CommandContainer container) {
        this.container = container;
        this.log = log;

        console = new LineObservable(System.in, false);
        console.addObserver(this);
        console.start();
    }

    @Override
    public void newLine(String line) {
        String[] split = line.split(" ");
        switch(split.length) {
            case 0 -> log.sendWarnMessage("Comando não reconhecido");
            case 1 -> processCommand(split[0], new String[0]);
            default -> {
                String commandName = split[0];
                String[] args = Arrays.copyOfRange(split, 1, split.length);
                processCommand(commandName, args);
            }
        }
    }

    private void processCommand(String commandName, String[] args) {
        MikitaCommand command = container.getCommandByName(commandName);
        if(command == null) {
            log.sendWarnMessage("Comando não encontrado: " + commandName);
            return;
        }
        command.onCommand(args);
    }

    @Override
    public void onClose() {
        log.sendMessage("Linha de comando de console finalizada.");
    }

    @Override
    public void close() throws Exception {
        console.close();
    }

}
