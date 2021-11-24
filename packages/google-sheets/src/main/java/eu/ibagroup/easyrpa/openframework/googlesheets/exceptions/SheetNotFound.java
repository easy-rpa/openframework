package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class SheetNotFound extends RuntimeException {
    public SheetNotFound(String message) {
        super(message);
    }

    public SheetNotFound(Throwable cause) {
        super(cause);
    }

    public SheetNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}

