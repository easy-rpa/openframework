package eu.ibagroup.easyrpa.openframework.email.exception;

public class EmailMessagingException extends RuntimeException {
    private static final long serialVersionUID = -2922140237742210407L;

    public EmailMessagingException(String message) {
        super(message);
    }

    public EmailMessagingException(Throwable cause) {
        super(cause);
    }

    public EmailMessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
