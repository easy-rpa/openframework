package eu.easyrpa.openframework.email.exception;

import eu.easyrpa.openframework.email.EmailClient;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * Is intended to use to interrupt the fetching of email messages by some reason.
 * <p>
 * In email client functions where <code>isSatisfy</code> lambda expression is used this exception can be thrown
 * for immediate interrupting of the function execution.
 * <p>
 * Here is an example of using this exception:
 * <pre>
 * {@code @Inject}
 *  private EmailClient emailClient;
 *
 *  public void execute() {
 *    ...
 *    log.info("Fetch the first message from default inbox folder that contain attachments.");
 *   {@code List<EmailMessage>} messages = emailClient.fetchMessages(msg -> {
 *        if (msg.hasAttachments()) {
 *           //By throwing this exception it stops further checking of
 *           //emails and return this message as single result
 *           throw new BreakEmailFetchException(true);
 *        }
 *        return false;
 *    });
 *    ...
 * }
 * </pre>
 *
 * @see EmailClient#fetchMessages(Predicate)
 * @see EmailClient#fetchMessages(String, Predicate)
 * @see EmailClient#fetchAllMessages(Predicate)
 * @see EmailClient#waitMessages(Predicate)
 * @see EmailClient#waitMessages(Predicate, Duration)
 * @see EmailClient#waitMessages(Predicate, Duration, Duration)
 * @see EmailClient#waitMessages(String, Predicate)
 * @see EmailClient#waitMessages(String, Predicate, Duration)
 * @see EmailClient#waitMessages(String, Predicate, Duration, Duration)
 */
public class BreakEmailFetchException extends RuntimeException {

    /**
     * Indicates whether is necessary to put the currently checking email message into results.
     */
    private boolean includeIntoResult;

    /**
     * Constructs a new BreakEmailFetchException.
     *
     * @param includeIntoResult the flag that indicates whether is necessary to put the currently checking
     *                          email message into results. If the value is <code>true</code> then the currently
     *                          checking email message should be included into results.
     */
    public BreakEmailFetchException(boolean includeIntoResult) {
        this.includeIntoResult = includeIntoResult;
    }

    /**
     * Checks whether is necessary to put the currently checking email message into results.
     *
     * @return <code>true</code> if the currently checking email message should be included into results and
     * <code>false</code> otherwise.
     */
    public boolean isIncludeIntoResult() {
        return includeIntoResult;
    }
}
