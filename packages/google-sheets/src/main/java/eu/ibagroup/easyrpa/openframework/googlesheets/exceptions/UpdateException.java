package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class UpdateException extends RuntimeException {
    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

