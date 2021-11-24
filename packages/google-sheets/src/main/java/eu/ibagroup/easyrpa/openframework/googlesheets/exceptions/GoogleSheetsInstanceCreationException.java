package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class GoogleSheetsInstanceCreationException extends RuntimeException {
    public GoogleSheetsInstanceCreationException(String message) {
        super(message);
    }

    public GoogleSheetsInstanceCreationException(Throwable cause) {
        super(cause);
    }

    public GoogleSheetsInstanceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

