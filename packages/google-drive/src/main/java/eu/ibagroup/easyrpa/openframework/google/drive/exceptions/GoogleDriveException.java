package eu.ibagroup.easyrpa.openframework.google.drive.exceptions;

/**
 * Google Drive runtime exception. Thrown in case of some errors or problems during working with Google Drive service.
 */
public class GoogleDriveException extends RuntimeException {

    /**
     * Constructs a new Google Drive exception with the specified detail message and
     * cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public GoogleDriveException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Google Drive exception with the specified cause.
     *
     * @param cause the cause.
     */
    public GoogleDriveException(Throwable cause) {
        super(cause);
    }
}
