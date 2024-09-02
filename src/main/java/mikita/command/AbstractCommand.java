package mikita.command;

import mikita.command.MethodCommandScanner.Endpoint;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractCommand implements MikitaCommand {

    protected final CommandOutput output;
    private final MethodCommandScanner scanner;

    public AbstractCommand(CommandOutput output) {
        this.output = Objects.requireNonNull(output, "output");

        scanner = new MethodCommandScanner(getClass());
        scanner.scan();
    }

    @Override
    public final void onCommand(String[] args) {
        Endpoint result = scanner.find(args);
        if (result == null) {
            output.sendErrorMessage("Comando " + getName() + " não compatível com os argumentos passados");
            return;
        }
        try {
            result.method().invoke(this, result.args());
        } catch (IllegalAccessException e) {
            output.sendErrorMessage("Não foi possível processar o comando " + getName()
                    + ". O método responsável não é acessível.", e);
        } catch (InvocationTargetException e) {
            output.sendErrorMessage("Ocorreu um erro durante o processamento do comando "
                    + getName(), e);
        }
    }

    @CommandEntry("help")
    public void help() {
        String name = getName();
        output.sendMessage("Lista de opções para o comando \"" + name + "\":");
        helpMap().forEach((subCommand, description) -> {
            String sub = subCommand == null || subCommand.isEmpty() ? "" : " " + subCommand;
            String formatted = name + sub + " -> " + description;
            output.sendMessage(formatted);
        });
    }

    protected abstract Map<String, String> helpMap();

}
