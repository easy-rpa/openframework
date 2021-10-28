package eu.ibagroup.easyrpa.openframework.googledrive.exceptions;

public class HttpTransportCreationException extends RuntimeException{
    public HttpTransportCreationException(String message) {
        super(message);
    }

    public HttpTransportCreationException(Throwable cause) {
        super(cause);
    }

    public HttpTransportCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

