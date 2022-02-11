package eu.ibagroup.easyrpa.openframework.google.services.exceptions;

public class GoogleAuthException extends RuntimeException {


    public GoogleAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleAuthException(Throwable cause) {
        super(cause);
    }
}
