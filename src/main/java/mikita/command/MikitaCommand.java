package mikita.command;

public interface MikitaCommand {

    String getName();

    void onCommand(String[] args);

}
