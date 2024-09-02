package mikita.io;

import mikita.message.Messageable;

import java.util.Objects;

public class LinePrinter implements LineObserver {

    private final Messageable messageable;

    public LinePrinter(Messageable messageable) {
        this.messageable = Objects.requireNonNull(messageable, "messageable");
    }

    @Override
    public void newLine(String line) {
        messageable.sendMessage(line);
    }

}
