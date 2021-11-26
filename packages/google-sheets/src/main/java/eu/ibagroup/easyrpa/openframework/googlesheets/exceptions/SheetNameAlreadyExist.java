package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class SheetNameAlreadyExist extends RuntimeException {
    public SheetNameAlreadyExist(String message) {
        super(message);
    }

    public SheetNameAlreadyExist(Throwable cause) {
        super(cause);
    }

    public SheetNameAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }
}

