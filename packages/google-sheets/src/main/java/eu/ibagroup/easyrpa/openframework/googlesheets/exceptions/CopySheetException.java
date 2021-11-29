package eu.ibagroup.easyrpa.openframework.googlesheets.exceptions;

public class CopySheetException extends RuntimeException {
    public CopySheetException(String message) {
        super(message);
    }

    public CopySheetException(Throwable cause) {
        super(cause);
    }

    public CopySheetException(String message, Throwable cause) {
        super(message, cause);
    }
}

