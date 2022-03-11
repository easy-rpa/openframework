package eu.easyrpa.openframework.google.services.exceptions;

import eu.easyrpa.openframework.google.services.GoogleAuth;

import java.util.List;

/**
 * An GoogleAuthException is thrown when an application tries to proceed through an authorization flow.
 *
 * @see GoogleAuth#authorize(List)
 */
public class GoogleAuthException extends RuntimeException {

    public GoogleAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleAuthException(Throwable cause) {
        super(cause);
    }
}
