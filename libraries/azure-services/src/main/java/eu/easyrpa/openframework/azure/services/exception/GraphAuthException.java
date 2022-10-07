package eu.easyrpa.openframework.azure.services.exception;

public class GraphAuthException extends RuntimeException {
    public GraphAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphAuthException(Throwable cause) {
        super(cause);
    }
}
