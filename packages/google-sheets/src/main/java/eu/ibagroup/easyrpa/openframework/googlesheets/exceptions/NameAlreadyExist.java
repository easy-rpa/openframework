package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class NameAlreadyExist extends RuntimeException {
    public NameAlreadyExist(String message) {
        super(message);
    }

    public NameAlreadyExist(Throwable cause) {
        super(cause);
    }

    public NameAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }
}

