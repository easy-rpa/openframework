package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class SpreadsheetRequestFailed extends RuntimeException {
    public SpreadsheetRequestFailed(String message) {
        super(message);
    }

    public SpreadsheetRequestFailed(Throwable cause) {
        super(cause);
    }

    public SpreadsheetRequestFailed(String message, Throwable cause) {
        super(message, cause);
    }
}

