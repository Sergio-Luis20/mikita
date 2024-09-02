package mikita.message;

import lombok.Getter;

import java.io.*;
import java.util.Objects;

public class StreamMessageable implements Messageable, Closeable {

    private final BufferedWriter writer;

    @Getter
    private boolean closed;

    public StreamMessageable(OutputStream stream) {
        writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(stream, "stream")));
    }

    @Override
    public void sendMessage(String message) {
        try {
            writer.write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendWarnMessage(String warnMessage) {
        sendMessage("Warn: " + warnMessage);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        sendMessage("Error: " + errorMessage);
    }

    @Override
    public void sendErrorMessage(String errorMessage, Throwable throwable) {
        sendMessage("Error: " + errorMessage + " (" + throwable + ")");
    }

    @Override
    public void close() throws IOException {
        writer.close();
        closed = true;
    }

}
