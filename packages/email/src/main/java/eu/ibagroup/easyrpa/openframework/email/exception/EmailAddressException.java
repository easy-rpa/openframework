package eu.ibagroup.easyrpa.openframework.email.exception;

public class EmailAddressException extends EmailMessagingException {
    private static final long serialVersionUID = 6747883869792961676L;

    public EmailAddressException(String message) {
        super(message);
    }

    public EmailAddressException(Throwable cause) {
        super(cause);
    }

    public EmailAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
