package mikita.exception;

import lombok.experimental.StandardException;

/**
 * Exception thrown to indicate the inability to
 * read a value and/or parsing it to a proper format
 * or object. The context may be anything: IO, document
 * file, resource not found...
 *
 * @author Sergio Luis
 */
@StandardException
public class ReadException extends Exception {
}
