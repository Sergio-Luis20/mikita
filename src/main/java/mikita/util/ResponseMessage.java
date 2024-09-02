package mikita.util;

/**
 * Utility record for wrapping a String into a json format
 * for jackson. If the message is null, the String "null"
 * will be used.
 * As the class name suggests, this was made to be used in
 * Mikita responses.
 *
 * @author Sergio Luis
 * @param message the message
 */
public record ResponseMessage(String message) {

    public ResponseMessage(String message) {
        this.message = message == null ? "null" : message;
    }

    public ResponseMessage(Object message) {
        this(String.valueOf(message));
    }

}
