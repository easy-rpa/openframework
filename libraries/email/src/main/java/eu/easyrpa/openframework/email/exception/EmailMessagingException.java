package eu.easyrpa.openframework.email.exception;

/**
 * Runtime exception that is thrown in case of some errors or problems during working with mailbox or email messages.
 */
public class EmailMessagingException extends RuntimeException {
    private static final long serialVersionUID = -2922140237742210407L;

    /**
     * Constructs a new EmailMessagingException with given detail message.
     *
     * @param message the detail message.
     */
    public EmailMessagingException(String message) {
        super(message);
    }

    /**
     * Constructs a new EmailMessagingException with given cause.
     *
     * @param cause the cause.
     */
    public EmailMessagingException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new EmailMessagingException with given detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public EmailMessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
