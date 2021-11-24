package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class SpreadsheetNotFound extends RuntimeException {
    public SpreadsheetNotFound(String message) {
        super(message);
    }

    public SpreadsheetNotFound(Throwable cause) {
        super(cause);
    }

    public SpreadsheetNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}

