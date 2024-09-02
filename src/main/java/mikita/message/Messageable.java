package mikita.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public interface Messageable {

    void sendMessage(String message);

    void sendWarnMessage(String warnMessage);

    void sendErrorMessage(String errorMessage);

    void sendErrorMessage(String errorMessage, Throwable throwable);

    default void sendErrorMessage(String errorMessage, Throwable throwable, boolean printStackTrace) {
        sendErrorMessage(errorMessage, throwable);

        if (printStackTrace) {
            try (StringWriter writer = new StringWriter(); PrintWriter printWriter = new PrintWriter(writer)) {
                throwable.printStackTrace(printWriter);
                printWriter.flush();
                sendErrorMessage(writer.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to print stack trace", e);
            }
        }
    }

}
