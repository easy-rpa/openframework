package eu.ibagroup.easyrpa.openframework.googledrive.exceptions;

public class GoogleInstanceCreationException extends RuntimeException{
    public GoogleInstanceCreationException(String message) {
        super(message);
    }

    public GoogleInstanceCreationException(Throwable cause) {
        super(cause);
    }

    public GoogleInstanceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
