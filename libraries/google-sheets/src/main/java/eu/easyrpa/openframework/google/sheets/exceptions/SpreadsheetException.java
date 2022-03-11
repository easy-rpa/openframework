package eu.easyrpa.openframework.google.sheets.exceptions;

/**
 * Google Spreadsheet runtime exception. Thrown in case of some errors or problems during working with
 * Google Spreadsheet.
 */
public class SpreadsheetException extends RuntimeException {

    /**
     * Constructs a new Google Spreadsheet exception with the specified detail message and
     * cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public SpreadsheetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Google Spreadsheet exception with the specified cause.
     *
     * @param cause the cause.
     */
    public SpreadsheetException(Throwable cause) {
        super(cause);
    }
}
