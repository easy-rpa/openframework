package eu.easyrpa.openframework.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.email.constants.EmailConfigParam;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.search.SearchQuery;
import eu.easyrpa.openframework.email.service.EmailServiceFactory;
import eu.easyrpa.openframework.email.service.EmailServiceSecret;
import eu.easyrpa.openframework.email.service.InboundEmailProtocol;
import eu.easyrpa.openframework.email.service.InboundEmailService;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is an email client service that provides functionality for working with mailbox folders and email
 * messages in them.
 * <p>
 * The main purpose of this class is waiting and reading of inbound email messages within RPA process. It's adapted
 * to make this work is much simpler and convenient as possible. This is achieved by using of
 * {@link RPAServicesAccessor} to get all necessary configuration parameters and thus moving out the definition of
 * them from RPA process code. In conjunction with using of {@link Inject} annotation the using of this class
 * looks easy and clear:
 * <pre>
 * {@code @Inject}
 *  private EmailClient emailClient;
 *
 *  public void execute() {
 *      ...
 *     {@code List<EmailMessage> messages = emailClient.fetchMessages();}
 *      ...
 *  }
 * </pre>
 *
 * @see EmailMessage
 */
public class EmailClient {

    private static final String DEFAULT_INBOX_FOLDER_NAME = "INBOX";
    private static final Duration DEFAULT_CHECK_TIMEOUT = Duration.ofHours(1);
    private static final Duration DEFAULT_CHECK_INTERVAL = Duration.ofMinutes(1);
    private static final InboundEmailProtocol DEFAULT_INBOUND_EMAIL_PROTOCOL = InboundEmailProtocol.IMAP;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Inbound email server URL.
     */
    private String server;

    /**
     * Protocol used by inbound email server to work with mailbox.
     */
    private InboundEmailProtocol protocol;

    /**
     * Secret information necessary to perform authentication to specific mailbox on the server.
     */
    private String secret;

    /**
     * Specific instance of inbound email service that depends on protocol used by inbound email server.
     */
    private InboundEmailService service;

    /**
     * Default folder with inbound messages of the mailbox. If this folder is not specified the "INBOX" folder is
     * used as default.
     */
    private String defaultFolder;

    /**
     * Default constructor of this EmailClient.
     * <p>
     * This constructor should be used in case of manual providing of parameters for connection with inbound email
     * server or if its necessary to work with more than one email server at the same time. E.g.:
     * <pre>
     *  EmailClient client1 = new EmailClient().server("imap1.mail.com").protocol("imaps")
     *          .secret("{ \"user\": \"user1@mail.com\", \"password": \"passphrase\" }");
     *
     *  EmailClient client2 = new EmailClient().server("imap2.mail.com").protocol("imaps")
     *          .secret("{ \"user\": \"user2@mail.com\", \"password": \"passphrase\" }");
     *   ...
     *  });
     * </pre>
     */
    public EmailClient() {
    }

    /**
     * Constructs EmailClient with provided <code>RPAServicesAccessor</code>.
     * <p>
     * This constructor is used in case of injecting of this EmailClient using {@link Inject} annotation. This is
     * preferable way of working with this class. E.g.:
     * <pre>
     * {@code @Inject}
     *  private EmailClient emailClient;
     *
     *  public void execute() {
     *      ...
     *     {@code List<EmailMessage> messages = emailClient.fetchMessages();}
     *      ...
     *  }
     * </pre>
     *
     * @param rpaServices instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     */
    @Inject
    public EmailClient(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    /**
     * Gets inbound email server URL.
     * <p>
     * If this server URL is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b><code>inbound.email.server</code></b>.
     *
     * @return inbound email server URL string.
     */
    public String getServer() {
        if (server == null) {
            server = getConfigParam(EmailConfigParam.INBOUND_EMAIL_SERVER);
        }
        return server;
    }

    /**
     * Sets explicitly the value of inbound email server URL.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param emailServerHostAndPort inbound email server URL with host name and port to set.
     */
    public void setServer(String emailServerHostAndPort) {
        this.server = emailServerHostAndPort;
        this.service = null;
    }

    /**
     * Sets explicitly the value of inbound email server URL.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param emailServerHostAndPort inbound email server URL with host name and port to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailClient server(String emailServerHostAndPort) {
        setServer(emailServerHostAndPort);
        return this;
    }

    /**
     * Gets protocol that is necessary to use for working with inbound email server.
     * <p>
     * If this protocol is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b><code>"inbound.email.protocol"</code></b>.
     * <p>
     * If it's not specified in configurations parameters either then <b><code>"imap"</code></b> protocol will be
     * used as default.
     *
     * @return {@link InboundEmailProtocol} representing necessary to use protocol.
     */
    public InboundEmailProtocol getProtocol() {
        if (protocol == null) {
            String protocolStr = getConfigParam(EmailConfigParam.INBOUND_EMAIL_PROTOCOL);
            protocol = protocolStr != null ? InboundEmailProtocol.valueOf(protocolStr.toUpperCase()) : DEFAULT_INBOUND_EMAIL_PROTOCOL;
        }
        return protocol;
    }

    /**
     * Sets explicitly the value of protocol that is necessary to use for working with inbound email server.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param protocol {@link InboundEmailProtocol} that is necessary to use.
     */
    public void setProtocol(InboundEmailProtocol protocol) {
        this.protocol = protocol;
        this.service = null;
    }

    /**
     * Sets explicitly the value of protocol that is necessary to use for working with inbound email server.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param protocol {@link InboundEmailProtocol} that is necessary to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailClient protocol(InboundEmailProtocol protocol) {
        setProtocol(protocol);
        return this;
    }

    /**
     * Sets explicitly the value of protocol that is necessary to use for working with inbound email server.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param protocol string with name of protocol that is necessary to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailClient protocol(String protocol) {
        setProtocol(InboundEmailProtocol.valueOf(protocol.toUpperCase()));
        return this;
    }

    /**
     * Gets JSON string with secret information necessary to perform authentication to specific mailbox on
     * the server.
     * <p>
     * The JSON string format depends on the protocol used by this client. But in most cases it looks like
     * the following:
     * <pre>
     * { "user": "email@dress", "password": "passphrase" }
     * </pre>
     * <p>
     * If this secret string is not specified explicitly then it will be looked up in secret vault of the
     * RPA platform. The secret vault alias that is necessary to lookup is expected to be specified in configuration
     * parameters under the key <b><code>"inbound.email.secret"</code></b>.
     *
     * @return JSON string with secret information.
     */
    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(EmailConfigParam.INBOUND_EMAIL_SECRET);
            if (secretAlias != null) {
                secret = rpaServices.getSecret(secretAlias, String.class);
            }
        }
        return secret;
    }

    /**
     * Sets explicitly the value of secret information necessary to perform authentication to specific mailbox on
     * the server.
     * <p>
     * The secret information should be in JSON format. Specific format depends on the protocol used by this client.
     * But in most cases it should look like the following:
     * <pre>
     * { "user": "email@dress", "password": "passphrase" }
     * </pre>
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param secret JSON string with secret information to set.
     */
    public void setSecret(String secret) {
        this.secret = secret;
        this.service = null;
    }

    /**
     * Sets explicitly the value of secret information necessary to perform authentication to specific mailbox on
     * the server.
     * <p>
     * The secret information should be in JSON format. Specific format depends on the protocol used by this client.
     * But in most cases it should look like the following:
     * <pre>
     * { "user": "email@dress", "password": "passphrase" }
     * </pre>
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param secret JSON string with secret information to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailClient secret(String secret) {
        setSecret(secret);
        return this;
    }

    /**
     * Sets explicitly credentials necessary to perform authentication to specific mailbox on the server.
     * <p>
     * These parameters can be changed at any time of working with this class. It will switch to work with new values
     * before calling of next service method.
     *
     * @param userName email address of the necessary mailbox.
     * @param password pass phrase to access the necessary mailbox.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailClient secret(String userName, String password) {
        try {
            setSecret(new ObjectMapper().writeValueAsString(new EmailServiceSecret(userName, password)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Gets the name of default mailbox folder with inbound messages.
     * <p>
     * If the default folder name is not specified explicitly then it will be looked up in configurations parameters
     * of the RPA platform under the key <b><code>"mailbox.default.folder"</code></b>.
     * <p>
     * If it's not specified in configurations parameters either then <b><code>"INBOX"</code></b> folder will be used.
     *
     * @return name of default mailbox folder with inbound messages.
     */
    public String getDefaultFolder() {
        if (defaultFolder == null) {
            String folderName = getConfigParam(EmailConfigParam.MAILBOX_DEFAULT_FOLDER);
            defaultFolder = folderName != null ? folderName : DEFAULT_INBOX_FOLDER_NAME;
        }
        return defaultFolder;
    }

    /**
     * Sets explicitly the name of default mailbox folder with inbound messages.
     *
     * @param defaultFolder the name of mailbox folder with inbound messages to set as default.
     */
    public void setDefaultFolder(String defaultFolder) {
        this.defaultFolder = defaultFolder;
    }

    /**
     * Sets explicitly the name of default mailbox folder with inbound messages.
     *
     * @param defaultFolder the name of mailbox folder with inbound messages to set as default.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailClient defaultFolder(String defaultFolder) {
        setDefaultFolder(defaultFolder);
        return this;
    }

    /**
     * Gets amount of email messages in the default mailbox folder.
     * <p>
     * The name of default mailbox folder is taken using {@link #getDefaultFolder()}.
     *
     * @return amount of email messages in the default mailbox folder.
     * @throws EmailMessagingException in case of some errors.
     */
    public int getMessageCount() {
        initService();
        return getMessageCount(getDefaultFolder());
    }

    /**
     * Gets amount of email messages in the mailbox folder with given name.
     *
     * @param folderName the name of mailbox folder where is necessary to count messages.
     * @return amount of email messages in the mailbox folder with given name.
     * @throws EmailMessagingException in case of some errors.
     */
    public int getMessageCount(String folderName) {
        initService();
        return this.service.getMessageCount(folderName);
    }

    /**
     * Searches email message in the mailbox with given message ID.
     * <p>
     * It views all mailbox folders to find the necessary message.
     *
     * @param messageId the unique identifier of email message that is necessary to find.
     * @return {@link EmailMessage} object representing found email message or <code>null</code> if nothing is found.
     * @throws EmailMessagingException in case of some errors.
     */
    public EmailMessage getMessage(String messageId) {
        initService();
        return this.service.getMessage(messageId);
    }

    /**
     * Gets all email messages contained in the default mailbox folder.
     * <p>
     * The name of default mailbox folder is taken using {@link #getDefaultFolder()}.
     *
     * @return list of {@link EmailMessage} objects representing existing email messages.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<EmailMessage> getMessages() {
        initService();
        return this.service.fetchMessages(getDefaultFolder(), null);
    }

    /**
     * Gets all email messages contained in the mailbox folder with given name.
     *
     * @param folderName the name of mailbox folder which is necessary to get all messages from.
     * @return list of {@link EmailMessage} objects representing existing email messages.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<EmailMessage> getMessages(String folderName) {
        initService();
        return this.service.fetchMessages(folderName, null);
    }

    /**
     * Gets email messages contained in the default mailbox folder and satisfy to specific condition.
     * <p>
     * The name of default mailbox folder is taken using {@link #getDefaultFolder()}.
     *
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @return list of {@link EmailMessage} objects representing satisfying email messages.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<EmailMessage> searchMessages(SearchQuery searchQuery) {
        initService();
        return this.service.fetchMessages(getDefaultFolder(), searchQuery);
    }

    /**
     * Gets email messages contained in the mailbox folder with given name and satisfy to specific condition.
     *
     * @param folderName  the name of mailbox folder where is necessary to collect email messages.
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @return list of {@link EmailMessage} objects representing satisfying email messages.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<EmailMessage> searchMessages(String folderName, SearchQuery searchQuery) {
        initService();
        return this.service.fetchMessages(folderName, searchQuery);
    }

    /**
     * Gets all email messages contained in all mailbox folders.
     *
     * @return list of {@link EmailMessage} objects representing existing email messages.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<EmailMessage> getAllMessages() {
        initService();
        return this.service.fetchMessages(null, null);
    }

    /**
     * Gets all email messages contained in all mailbox folders and satisfy to specific condition.
     *
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @return list of {@link EmailMessage} objects representing satisfying email messages.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<EmailMessage> searchAllMessages(SearchQuery searchQuery) {
        initService();
        return this.service.fetchMessages(null, searchQuery);
    }

    /**
     * Waits appearing of email messages in the default mailbox folder that satisfy to specific condition.
     * <p>
     * The waiting of messages is performed asynchronously using {@link CompletableFuture}. It allows to save a time
     * in cases when waiting of messages is expected can take much time and something different can be done in parallel.
     * <p>
     * When this method is called it does the following:
     * <ol>
     *     <li>
     *         Searches satisfying messages in the default mailbox folder. If at least one message that satisfy
     *         to given condition is found the invocation of this method ends and the message is returned in results.
     *         Otherwise continue with step 2.
     *     </li>
     *     <li>
     *         Each <b><code>1 minute</code></b> is checked newly come messages. If at least one satisfying message
     *         is found the waiting ends and satisfying messages are returned in results. Otherwise continue
     *         with step 3.
     *     </li>
     *     <li>
     *         Checks whether the waiting is continue no longer than <b><code>1 hour</code></b> starting from the
     *         moment of this method call. If timeout is reached than the waiting ends and empty result is returned.
     *         Otherwise continue with steps 2-3.
     *     </li>
     * </ol>
     *
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     * @throws EmailMessagingException in case of some errors.
     */
    public CompletableFuture<List<EmailMessage>> waitMessages(SearchQuery searchQuery) {
        initService();
        return this.service.waitMessages(getDefaultFolder(), searchQuery, DEFAULT_CHECK_TIMEOUT, DEFAULT_CHECK_INTERVAL);
    }

    /**
     * Waits appearing of email messages in the default mailbox folder that satisfy to specific condition.
     * <p>
     * The waiting of messages is performed asynchronously using {@link CompletableFuture}. It allows to save a time
     * in cases when waiting of messages is expected can take much time and something different can be done in parallel.
     * <p>
     * When this method is called it does the following:
     * <ol>
     *     <li>
     *         Searches satisfying messages in the default mailbox folder. If at least one message that satisfy
     *         to given condition is found the invocation of this method ends and the message is returned in results.
     *         Otherwise continue with step 2.
     *     </li>
     *     <li>
     *         Each <b><code>1 minute</code></b> is checked newly come messages. If at least one satisfying message
     *         is found the waiting ends and satisfying messages are returned in results. Otherwise continue
     *         with step 3.
     *     </li>
     *     <li>
     *         Checks whether the waiting is continue no longer than <b><code>timeout</code></b> starting from the
     *         moment of this method call. If timeout is reached than the waiting ends and empty result is returned.
     *         Otherwise continue with steps 2-3.
     *     </li>
     * </ol>
     *
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @param timeout     the maximum time of waiting necessary messages.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     * @throws EmailMessagingException in case of some errors.
     */
    public CompletableFuture<List<EmailMessage>> waitMessages(SearchQuery searchQuery, Duration timeout) {
        initService();
        return this.service.waitMessages(getDefaultFolder(), searchQuery, timeout, DEFAULT_CHECK_INTERVAL);
    }

    /**
     * Waits appearing of email messages in the default mailbox folder that satisfy to specific condition.
     * <p>
     * The waiting of messages is performed asynchronously using {@link CompletableFuture}. It allows to save a time
     * in cases when waiting of messages is expected can take much time and something different can be done in parallel.
     * <p>
     * When this method is called it does the following:
     * <ol>
     *     <li>
     *         Searches satisfying messages in the default mailbox folder. If at least one message that satisfy
     *         to given condition is found the invocation of this method ends and the message is returned in results.
     *         Otherwise continue with step 2.
     *     </li>
     *     <li>
     *         Each <b><code>checkInterval</code></b> is checked newly come messages. If at least one satisfying message
     *         is found the waiting ends and satisfying messages are returned in results. Otherwise continue
     *         with step 3.
     *     </li>
     *     <li>
     *         Checks whether the waiting is continue no longer than <b><code>timeout</code></b> starting from the
     *         moment of this method call. If timeout is reached than the waiting ends and empty result is returned.
     *         Otherwise continue with steps 2-3.
     *     </li>
     * </ol>
     *
     * @param searchQuery   the {@link SearchQuery} object representing specific condition.
     * @param timeout       the maximum time of waiting necessary messages.
     * @param checkInterval amount of time that defines period of checking newly come messages.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     * @throws EmailMessagingException in case of some errors.
     */
    public CompletableFuture<List<EmailMessage>> waitMessages(SearchQuery searchQuery, Duration timeout, Duration checkInterval) {
        initService();
        return this.service.waitMessages(getDefaultFolder(), searchQuery, timeout, checkInterval);
    }

    /**
     * Waits appearing of email messages in the mailbox folder with given name that satisfy to specific condition.
     * <p>
     * The waiting of messages is performed asynchronously using {@link CompletableFuture}. It allows to save a time
     * in cases when waiting of messages is expected can take much time and something different can be done in parallel.
     * <p>
     * When this method is called it does the following:
     * <ol>
     *     <li>
     *         Searches satisfying messages in the given mailbox folder. If at least one message that satisfy
     *         to given condition is found the invocation of this method ends and the message is returned in results.
     *         Otherwise continue with step 2.
     *     </li>
     *     <li>
     *         Each <b><code>1 minute</code></b> is checked newly come messages. If at least one satisfying message
     *         is found the waiting ends and satisfying messages are returned in results. Otherwise continue
     *         with step 3.
     *     </li>
     *     <li>
     *         Checks whether the waiting is continue no longer than <b><code>1 hour</code></b> starting from the
     *         moment of this method call. If timeout is reached than the waiting ends and empty result is returned.
     *         Otherwise continue with steps 2-3.
     *     </li>
     * </ol>
     *
     * @param folderName  the name of mailbox folder where is necessary to check messages.
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     * @throws EmailMessagingException in case of some errors.
     */
    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, SearchQuery searchQuery) {
        initService();
        return this.service.waitMessages(folderName, searchQuery, DEFAULT_CHECK_TIMEOUT, DEFAULT_CHECK_INTERVAL);
    }

    /**
     * Waits appearing of email messages in the mailbox folder with given name that satisfy to specific condition.
     * <p>
     * The waiting of messages is performed asynchronously using {@link CompletableFuture}. It allows to save a time
     * in cases when waiting of messages is expected can take much time and something different can be done in parallel.
     * <p>
     * When this method is called it does the following:
     * <ol>
     *     <li>
     *         Searches satisfying messages in the given mailbox folder. If at least one message that satisfy
     *         to given condition is found the invocation of this method ends and the message is returned in results.
     *         Otherwise continue with step 2.
     *     </li>
     *     <li>
     *         Each <b><code>1 minute</code></b> is checked newly come messages. If at least one satisfying message
     *         is found the waiting ends and satisfying messages are returned in results. Otherwise continue
     *         with step 3.
     *     </li>
     *     <li>
     *         Checks whether the waiting is continue no longer than <b><code>timeout</code></b> starting from the
     *         moment of this method call. If timeout is reached than the waiting ends and empty result is returned.
     *         Otherwise continue with steps 2-3.
     *     </li>
     * </ol>
     *
     * @param folderName  the name of mailbox folder where is necessary to check messages.
     * @param searchQuery the {@link SearchQuery} object representing specific condition.
     * @param timeout     the maximum time of waiting necessary messages.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     * @throws EmailMessagingException in case of some errors.
     */
    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, SearchQuery searchQuery, Duration timeout) {
        initService();
        return this.service.waitMessages(folderName, searchQuery, timeout, DEFAULT_CHECK_INTERVAL);
    }

    /**
     * Waits appearing of email messages in the mailbox folder with given name that satisfy to specific condition.
     * <p>
     * The waiting of messages is performed asynchronously using {@link CompletableFuture}. It allows to save a time
     * in cases when waiting of messages is expected can take much time and something different can be done in parallel.
     * <p>
     * When this method is called it does the following:
     * <ol>
     *     <li>
     *         Searches satisfying messages in the given mailbox folder. If at least one message that satisfy
     *         to given condition is found the invocation of this method ends and the message is returned in results.
     *         Otherwise continue with step 2.
     *     </li>
     *     <li>
     *         Each <b><code>1 minute</code></b> is checked newly come messages. If at least one satisfying message
     *         is found the waiting ends and satisfying messages are returned in results. Otherwise continue
     *         with step 3.
     *     </li>
     *     <li>
     *         Checks whether the waiting is continue no longer than <b><code>timeout</code></b> starting from the
     *         moment of this method call. If timeout is reached than the waiting ends and empty result is returned.
     *         Otherwise continue with steps 2-3.
     *     </li>
     * </ol>
     *
     * @param folderName    the name of mailbox folder where is necessary to check messages.
     * @param searchQuery   the {@link SearchQuery} object representing specific condition.
     * @param timeout       the maximum time of waiting necessary messages.
     * @param checkInterval amount of time that defines period of checking newly come messages.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     * @throws EmailMessagingException in case of some errors.
     */
    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, SearchQuery searchQuery, Duration timeout, Duration checkInterval) {
        initService();
        return this.service.waitMessages(folderName, searchQuery, timeout, checkInterval);
    }

    /**
     * Makes a copy of given email message in the specified folder.
     *
     * @param message      the source email message that should be copied.
     * @param targetFolder the name of mailbox folder where the email message should be copied.
     * @return {@link EmailMessage} object representing copied message.
     * @throws EmailMessagingException in case of some errors.
     */
    public EmailMessage copyMessage(EmailMessage message, String targetFolder) {
        initService();
        return this.service.copyMessage(message, targetFolder);
    }

    /**
     * Moves given email message to the specified folder.
     * <p>
     * The moving is performed by copying of the email message to the specified folder and removing of
     * the source message.
     *
     * @param message      the source email message that should be moved.
     * @param targetFolder the name of mailbox folder where the email message should be moved.
     * @return {@link EmailMessage} object representing moved message. The source message will be deleted.
     * @throws EmailMessagingException in case of some errors.
     */
    public EmailMessage moveMessage(EmailMessage message, String targetFolder) {
        initService();
        return this.service.moveMessage(message, targetFolder);
    }

    /**
     * Updates parameters of given email message in the mailbox.
     * <p>
     * Currently it updates only one parameter: {@link EmailMessage#isRead} flag.
     *
     * @param message the email message whose parameters should be updated in the mailbox.
     * @throws EmailMessagingException in case of some errors.
     */
    public void updateMessage(EmailMessage message) {
        initService();
        this.service.updateMessage(message);
    }

    /**
     * Updates parameters of given email messages in the mailbox.
     * <p>
     * Currently it updates only one parameter: {@link EmailMessage#isRead} flag.
     *
     * @param messages the list of email messages whose parameters should be updated in the mailbox.
     * @throws EmailMessagingException in case of some errors.
     */
    public void updateMessages(List<EmailMessage> messages) {
        initService();
        this.service.updateMessages(messages);
    }

    /**
     * Deletes given email message from the mailbox.
     * <p>
     * The message is deleted permanently without Trash.
     *
     * @param message the email message to delete.
     * @throws EmailMessagingException in case of some errors.
     */
    public void deleteMessage(EmailMessage message) {
        initService();
        this.service.deleteMessage(message);
    }

    /**
     * Deletes given email messages from the mailbox.
     * <p>
     * Messages are deleted permanently without Trash.
     *
     * @param messages the list of email messages to delete.
     * @throws EmailMessagingException in case of some errors.
     */
    public void deleteMessages(List<EmailMessage> messages) {
        initService();
        this.service.deleteMessages(messages);
    }

    /**
     * Gets the list of folder names that are present in the mailbox.
     *
     * @return list of folder names that are present in the mailbox.
     * @throws EmailMessagingException in case of some errors.
     */
    public List<String> listFolders() {
        initService();
        return this.service.listFolders();
    }

    /**
     * Creates a new folder with given name in the mailbox.
     *
     * @param folderName the name of folder to create.
     * @return <code>true</code> if the folder has been created successfully and <code>false</code> otherwise.
     * @throws EmailMessagingException in case of some errors.
     */
    public boolean createFolder(String folderName) {
        initService();
        return this.service.createFolder(folderName);
    }

    /**
     * Renames given mailbox folder.
     *
     * @param folderName    the name of source mailbox folder.
     * @param newFolderName a new name of the folder.
     * @return <code>true</code> if the folder has been renamed successfully and <code>false</code> otherwise.
     * @throws EmailMessagingException in case of some errors.
     */
    public boolean renameFolder(String folderName, String newFolderName) {
        initService();
        return this.service.renameFolder(folderName, newFolderName);
    }

    /**
     * Deletes given mailbox folder with all messages and sub-folders contained in it.
     *
     * @param folderName the name of mailbox folder to delete.
     * @return <code>true</code> if the folder has been deleted successfully and <code>false</code> otherwise.
     * @throws EmailMessagingException in case of some errors.
     */
    public boolean deleteFolder(String folderName) {
        initService();
        return this.service.deleteFolder(folderName);
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found or {@link RPAServicesAccessor} is not defined for this email client.
     */
    protected String getConfigParam(String key) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getConfigParam(key);
        } catch (Exception e) {
            //do nothing
        }

        return result;
    }

    /**
     * Creates instance of inbound email service depended on protocol is going to be used by this email client
     * and supported by target inbound email server.
     * <p>
     * {@link EmailServiceFactory} is responsible for creation of specific inbound email service.
     */
    private void initService() {
        if (this.service == null) {
            this.service = EmailServiceFactory.getInstance().getInboundService(
                    rpaServices, getServer(), getProtocol(), getSecret()
            );
        }
    }
}
