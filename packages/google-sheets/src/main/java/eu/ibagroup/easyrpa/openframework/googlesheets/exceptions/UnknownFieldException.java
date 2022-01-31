package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class UnknownFieldException extends RuntimeException {
    public UnknownFieldException(String message) {
        super(message);
    }

    public UnknownFieldException(Throwable cause) {
        super(cause);
    }

    public UnknownFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}

