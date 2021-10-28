package eu.ibagroup.easyrpa.openframework.googledrive.exceptions;

public class GoogleDriveInstanceCreationException extends RuntimeException{
    public GoogleDriveInstanceCreationException(String message) {
        super(message);
    }

    public GoogleDriveInstanceCreationException(Throwable cause) {
        super(cause);
    }

    public GoogleDriveInstanceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
