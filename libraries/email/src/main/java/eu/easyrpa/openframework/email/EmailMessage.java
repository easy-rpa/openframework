package eu.easyrpa.openframework.email;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.email.constants.EmailConfigParam;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.message.EmailAddress;
import eu.easyrpa.openframework.email.message.EmailAttachment;
import eu.easyrpa.openframework.email.message.EmailBodyPart;
import org.jsoup.Jsoup;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents specific email message and provides functionality to work with its parameters and content.
 * <p>
 * This class is implemented in a way to make creating of email messages as much simple and convenient as possible
 * that is very important within RPA process. It provides different ways of defining email message parameters:
 * <p>
 * <b>1. In one line of code</b>. This way allows dynamically build email message and avoid the overloading of
 * RPA process code.
 * <pre>
 * new EmailMessage().recipients("tom@example.com").subject("Test email").text("This message was sent by robot.");
 * </pre>
 * <p>
 * <b>2. In configuration parameters</b>. Almost all parameters of email message can be specified in configuration
 * parameters of RPA process. It allows to remove from RPA process code definition of constant parameters.
 * <p>
 * In configuration parameters:
 * <pre>
 *    email.recipients=tom@example.com
 *    email.subject=Test email
 * </pre>
 * Since recipients and subject are already defined in configuration parameters they automatically will be set to any
 * new email message:
 * <pre>
 * EmailMessage msg = new EmailMessage().text("This message was sent by robot.");
 * assert "Test email".equals(msg.getSubject()); //is true.
 * </pre>
 * If necessary to define configuration parameters for specific emails the special parameter <code>typeName</code>
 * can be used. In the following example the <code>typeName</code>="user.email":
 * <pre>
 *    user.email.recipients=tom@example.com
 *    user.email.subject=This is a user email
 * </pre>
 * And in the code:
 * <pre>
 * EmailMessage userMsg = new EmailMessage("user.email").text("This message was sent by robot.");
 * assert "This is a user email".equals(userMsg.getSubject()); //is true
 * //But
 * EmailMessage msg = new EmailMessage().text("This message was sent by robot.");
 * assert "This is a user email".equals(msg.getSubject()); //is false
 * assert "".equals(msg.getSubject()); //is true
 * </pre>
 * <p>
 * <b>3. From JSON</b>. This way is recommended to use when it's necessary to transfer email message somewhere as text,
 * e.g. between steps of the RPA process.
 * <pre>
 * EmailMessage msg = new EmailMessage().subject("Test email").text("This message was sent by robot.");
 * String msgJson = msg.toJson(false);
 * ...
 * EmailMessage restoredMsg = EmailMessage.fromJson(msgJson);
 * assert "Test email".equals(restoredMsg.getSubject()); //is true.
 * </pre>
 */
public class EmailMessage {

    /**
     * Date time format used for serialization/deserialization of {@link #date} field.
     */
    public static final String USED_DATE_TME_FORMAT_PATTERN = "MM/dd/yyyy hh:mm:ss a";

    private static final String DEFAULT_EMAIL_TYPE_NAME = "email";

    /**
     * The name of type of this email message that defines names of configuration parameters containing related to
     * this email message parameters. This value is set via {@link #EmailMessage(String)} or
     * {@link #EmailMessage(String, EmailSender)}
     */
    protected String typeName = DEFAULT_EMAIL_TYPE_NAME;

    /**
     * The unique identifier of this email message within mailbox.
     */
    protected String id;

    /**
     * The received or sent date of this email. The received date if the email message has been received and sent
     * date if the email message has been just sent.
     */
    @JsonFormat(pattern = USED_DATE_TME_FORMAT_PATTERN)
    protected Date date;

    /**
     * The name of mailbox folder where this email message is contained.
     */
    protected String parentFolder;

    /**
     * Map with all header parameters of this email message.
     */
    protected Map<String, String> headers;

    /**
     * Email address of the actual sender of this email message that corresponds to email account on behalf of which
     * this message is sent.
     */
    protected EmailAddress sender;

    /**
     * Display name of the actual sender of this email message.
     */
    @JsonIgnore
    protected String senderName;

    /**
     * Email address displayed in the filed <code>From:</code> of this email message.
     */
    protected EmailAddress from;

    /**
     * Name of channel configured and managed within RPA platform. Channels define recipients of email message
     * and way of sending.
     */
    private String channel;

    /**
     * List of email addresses who are recipients of this email message.
     */
    protected List<EmailAddress> recipients;

    /**
     * List of email addresses who are CC recipients of this email message.
     */
    protected List<EmailAddress> ccRecipients;

    /**
     * List of email addresses who are BCC recipients of this email message.
     */
    protected List<EmailAddress> bccRecipients;

    /**
     * List of email addresses who are supposed recipients of the replying on this email message.
     */
    protected List<EmailAddress> replyTo;

    /**
     * Subject of this email message.
     */
    protected String subject;

    /**
     * Charset of this email message body.
     */
    protected String charset;

    /**
     * Name of template that defines content of this email message and managed within RPA platform where this
     * library is used.
     */
    protected String template;

    /**
     * Cached text representation of this email message body.
     */
    @JsonIgnore
    protected String text;

    /**
     * Cached HTML representation of this email message body.
     */
    @JsonIgnore
    protected String html;

    /**
     * List of parts that constituents the body of this email message.
     */
    protected List<EmailBodyPart> bodyParts;

    /**
     * Map with properties used for substitution of variables within the body of this email message.
     */
    protected Map<String, Object> bodyProperties = new HashMap<>();

    /**
     * List of attachments attached to this email message.
     */
    protected List<EmailAttachment> attachments;

    /**
     * Read/unread flag of this email message.
     */
    protected Boolean isRead;

    /**
     * Link to the email message that this email message forwards.
     */
    @JsonIgnore
    protected EmailMessage forwardedMessage;

    /**
     * Link to the email message that this email message replies on.
     */
    @JsonIgnore
    protected EmailMessage replyOnMessage;

    /**
     * Link to the {@link EmailSender} that can be used for sending of this email message using method {@link #send()}
     * and retrieving of parameters that are not specified explicitly.
     */
    @JsonIgnore
    protected EmailSender emailSender;

    private final Pattern htmlTagExtractor = Pattern.compile("<html([^>]*)>([\\w\\W]*)</html>", Pattern.CASE_INSENSITIVE);
    private final Pattern headTagExtractor = Pattern.compile("<head([^>]*)>([\\w\\W]*)</head>", Pattern.CASE_INSENSITIVE);
    private final Pattern bodyTagExtractor = Pattern.compile("<body([^>]*)>([\\w\\W]*)</body>", Pattern.CASE_INSENSITIVE);

    /**
     * Constructs a new instance of EmailMessage with default type name.
     * <p>
     * The default type name: <b><code>email</code></b>.
     * <p>
     * It means if some parameters of this email message are not specified explicitly the email sender will try to
     * find them in configuration parameters of RPA platform under keys:
     * <br><b><code>email</code>.subject</b>,
     * <br><b><code>email</code>.recipients</b>,
     * <br><b><code>email</code>.cc.recipients</b> etc.
     */
    public EmailMessage() {
    }

    /**
     * Constructs a new instance of EmailMessage with given type name.
     * <p>
     * The email type name defines where the email sender should find email message parameters if they are not
     * specified explicitly. The value of <code>typeName</code> defines keys of configuration parameters specified
     * within RPA platform and related to corresponding parameters of email message. Full keys of related configuration
     * parameters are formed as:
     * <br><b><code>typeName</code>.subject</b>,
     * <br><b><code>typeName</code>.recipients</b>,
     * <br><b><code>typeName</code>.cc.recipients</b> etc.
     * <p>
     * It means if <code>typeName</code> is equal to <code>"summary.email"</code> keys of related configuration
     * parameters will be:
     * <br><b>summary.email.subject</b>,
     * <br><b>summary.email.recipients</b>,
     * <br><b>summary.email.cc.recipients</b> etc.
     *
     * @param typeName the name of email type.
     */
    public EmailMessage(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Constructs a new instance of EmailMessage with default type name and providing of email sender.
     * <p>
     * This constructor is used in case of injecting of this EmailMessage using {@link Inject} annotation.
     * It's a convenient way that allows to avoid direct working with {@link EmailSender}:
     * <pre>
     * {@code @Inject}
     *  private EmailMessage message;
     *
     *  public void execute() {
     *      ...
     *      message.subject(SUBJECT).text(BODY).send();
     *      ...
     *  }
     * </pre>
     * The default type name: <b><code>email</code></b>.
     * <p>
     * It means if some parameters of this email message are not specified explicitly the email sender will try to
     * find them in configuration parameters of RPA platform under keys:
     * <br><b><code>email</code>.subject</b>,
     * <br><b><code>email</code>.recipients</b>,
     * <br><b><code>email</code>.cc.recipients</b> etc.
     * <p>
     *
     * @param emailSender the instance of {@link EmailSender}. It used for sending of this email message using
     *                    method {@link #send()} and retrieving of email message parameters that are not
     *                    specified explicitly.
     */
    @Inject
    public EmailMessage(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Constructs a new instance of EmailMessage with given type name and providing of email sender.
     * <p>
     * This constructor should be used in case of extending EmailMessage by other subclasses. It allows implement
     * injecting of extended email class using {@link Inject} annotation and avoid direct working
     * with {@link EmailSender}:
     * <pre>
     * {@code @Inject}
     *  private SummaryEmailMessage summaryMessage;
     *
     *  public void execute() {
     *      ...
     *      summaryMessage.setResults(someResults).send();
     *      ...
     *  }
     * </pre>
     * <p>
     * The <code>typeName</code> defines where the email sender should find email message parameters if they are not
     * specified explicitly. The value of <code>typeName</code> defines keys of configuration parameters specified
     * within RPA platform and related to corresponding parameters of email message. Full keys of related configuration
     * parameters are formed as:
     * <br><b><code>typeName</code>.subject</b>,
     * <br><b><code>typeName</code>.recipients</b>,
     * <br><b><code>typeName</code>.cc.recipients</b> etc.
     * <p>
     * It means if <code>typeName</code> is equal to <code>"summary.email"</code> keys of related configuration
     * parameters will be:
     * <br><b>summary.email.subject</b>,
     * <br><b>summary.email.recipients</b>,
     * <br><b>summary.email.cc.recipients</b> etc.
     *
     * @param typeName    the name of email type.
     * @param emailSender the instance of {@link EmailSender}. It used for sending of this email message using
     *                    method {@link #send()} and retrieving of email message parameters that are not
     *                    specified explicitly.
     */
    public EmailMessage(String typeName, EmailSender emailSender) {
        this.typeName = typeName;
        this.emailSender = emailSender;
    }

    /**
     * Gets unique identifier of this email message within mailbox.
     * <p>
     * The value of unique identifier is present only for existing email messages of the mailbox.
     *
     * @return unique identifier string of this email message within mailbox or <code>null</code> if this email message
     * is a new and have never been sent.
     * @throws EmailMessagingException in case of some errors.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the name of mailbox folder where this email message is contained.
     * <p>
     * The value of parent folder is present only for existing email messages of the mailbox.
     *
     * @return name of mailbox folder where this email message is contained or <code>null</code> if this email message
     * is a new and have never been sent.
     * @throws EmailMessagingException in case of some errors.
     */
    public String getParentFolder() {
        return this.parentFolder;
    }

    /**
     * Gets the date of this email message. Depends on the email message state it can be received or sent date.
     * <p>
     * This date represents received date if the email message has been received or sent date if the email message
     * has been just sent.
     *
     * @return {@link Date} object representing received or sent date of this email message.
     * @throws EmailMessagingException in case of some errors.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the date of this email message.
     * <p>
     * It provides the same date that is {@link #getDate()} but as {@link LocalDateTime} value.
     *
     * @return {@link LocalDateTime} object representing received or sent date of this email message.
     * @throws EmailMessagingException in case of some errors.
     */
    @JsonIgnore
    public LocalDateTime getDateTime() {
        return getDate() != null ? LocalDateTime.ofInstant(getDate().toInstant(), ZoneId.systemDefault()) : null;
    }


    /**
     * Gets key-value map with all header parameters of this email message.
     *
     * @return key-value map with all header parameters of this email message.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets given key-value map as header parameters of this email message.
     * <p>
     * If some of given header parameters are already present in headers of this email message then they will be
     * overwritten. Other headers of this email message that are not present in the given map will be left without
     * changes.
     *
     * @param headers the key-value map with header parameters to set.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>();
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    /**
     * Sets header parameter for this email message.
     *
     * @param key   the key string of the header parameter to set.
     * @param value the value string of the header parameter to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage header(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    /**
     * Gets email address of the actual sender of this email message.
     * <p>
     * The sender email address automatically is set by email service and corresponds to email account on behalf of
     * which this message is sent. Information about this email account is provided via {@link EmailSender#getSecret()}.
     *
     * @return {@link EmailAddress} object representing the email address of actual sender of this email message.
     */
    public EmailAddress getSender() {
        return sender;
    }

    /**
     * Sets the email address of the actual sender of this email message.
     * <p>
     * <b>This method is used by email service and shouldn't be used explicitly.</b> The sender email address
     * automatically is set by email service and corresponds to email account on behalf of which this message is sent.
     * Information about this email account is provided via {@link EmailSender#getSecret()}.
     *
     * @param senderAddress the email address string to set.
     */
    public void setSender(String senderAddress) {
        this.sender = senderAddress != null ? new EmailAddress(senderAddress, getSenderName()) : null;
    }

    /**
     * Sets the email address of the actual sender of this email message.
     * <p>
     * <b>This method is used for serialize/deserialize of this email message into JSON and shouldn't be used
     * explicitly.</b> Since the sender email address string automatically is set by email server and corresponds
     * to email account on behalf of which this message is sent. Information about this email account is provided
     * via {@link EmailSender#getSecret()}.
     *
     * @param sender the {@link EmailAddress} object with email address and its display name to set.
     */
    @JsonSetter
    public void setSender(EmailAddress sender) {
        this.sender = sender;
        this.senderName = sender != null ? sender.getPersonal() : null;
    }

    /**
     * Gets the display name of the actual sender of this email message.
     * <p>
     * This display name is used ONLY if the value of {@link #from} is not specified.
     * <p>
     * If the value of {@link #from} is not set and the display name is not specified explicitly then it will be looked
     * up in configuration parameters of the RPA platform under the key that depends on the actual
     * {@link #typeName} value:
     * <pre>
     * {@code <typeName>.sender.name}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.sender.name</b>.
     *
     * @return string with display name of the actual sender of this email message.
     */
    public String getSenderName() {
        if (senderName == null) {
            senderName = getConfigParam(EmailConfigParam.SENDER_NAME_TPL);
        }
        return senderName;
    }

    /**
     * Sets explicitly the display name of the actual sender of this email message.
     * <p>
     * The actual sender is set by email service and corresponds to email account on behalf of
     * which this message is sent. Using this method it is possible to specify a display name that should be
     * displayed in the field <code>From:</code> instead of email address string but ONLY if the value of {@link #from}
     * is not specified.
     *
     * @param senderName the string with display name of the sender to set.
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
        if (this.sender != null) {
            this.sender = new EmailAddress(this.sender.getAddress(), this.senderName);
        }
    }

    /**
     * Sets explicitly the display name of the actual sender of this email message.
     * <p>
     * The actual sender is set by email service and corresponds to email account on behalf of
     * which this message is sent. Using this method it is possible to specify a display name that should be
     * displayed in the field <code>From:</code> instead of email address string but ONLY if the value of {@link #from}
     * is not specified.
     *
     * @param senderName the string with display name of the sender to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage senderName(String senderName) {
        setSenderName(senderName);
        return this;
    }

    /**
     * Gets email address displayed in the field <code>From:</code> of this email message.
     * <p>
     * This value can be different from the actual email sender that is returned by {@link #getSender()}.
     * <p>
     * If this email address is not specified explicitly then it will be looked up in configuration parameters of
     * the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.from}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.from</b>.
     * <p>
     * If this email address is not set at all then email address returned by {@link #getSender()} will be displayed
     * in the field <code>From:</code> instead.
     *
     * @return {@link EmailAddress} object representing email address displayed in the field <code>From:</code>
     * of this email message.
     */
    public EmailAddress getFrom() {
        if (from == null) {
            String fromAddress = getConfigParam(EmailConfigParam.FROM_TPL);
            from = fromAddress != null ? new EmailAddress(fromAddress) : null;
        }
        return from;
    }

    /**
     * Sets explicitly the email address displayed in the field <code>From:</code> of this email message.
     *
     * @param fromAddress the email address string to set.
     */
    public void setFrom(String fromAddress) {
        this.from = fromAddress != null ? new EmailAddress(fromAddress) : null;
    }

    /**
     * Sets explicitly the email address displayed in the field <code>From:</code> of this email message.
     *
     * @param from the {@link EmailAddress} object with email address and its display name to set.
     */
    @JsonSetter
    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    /**
     * Sets explicitly the email address displayed in the field <code>From:</code> of this email message.
     *
     * @param fromAddress the email address string to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage from(String fromAddress) {
        setFrom(fromAddress);
        return this;
    }

    /**
     * Gets name of channel that defines recipients of email message and way of sending.
     * <p>
     * The channel is expected to be configured and managed within RPA platform. The name is used as
     * reference to it.
     * <p>
     * If the name of channel is not specified explicitly then it will be looked up in configuration parameters of
     * the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.channel}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.channel</b>.
     * <p>
     * The email will be sent using RPA platform capabilities <b>only when this channel name is specified</b>.
     *
     * @return string with name of channel.
     */
    public String getChannel() {
        if (channel == null) {
            channel = getConfigParam(EmailConfigParam.CHANNEL_TPL);
        }
        return channel;
    }

    /**
     * Sets explicitly name of channel that defines recipients of email message and way of sending.
     * <p>
     * The channel is expected to be configured and managed within RPA platform. The name is used as
     * reference to it.
     *
     * @param channel string with name of channel to set.
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * Sets explicitly name of channel that defines recipients of email message and way of sending.
     * <p>
     * The channel is expected to be configured and managed within RPA platform. The name is used as
     * reference to it.
     *
     * @param channel string with name of channel to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage channel(String channel) {
        setChannel(channel);
        return this;
    }

    /**
     * Gets list of email addresses who are recipients of this email message.
     * <p>
     * This is a list of email addresses displayed in the field <code>To:</code> of this email message.
     * <p>
     * If recipients are not specified explicitly then they will be looked up in configuration parameters of
     * the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.recipients}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.recipients</b>.
     * <p>
     * Email addresses in the value of this configuration parameter can be delimited with "<code>;</code>":
     * <pre>
     * email.recipients = user1@example.com;user2@example.com;user3@example.com
     * </pre>
     *
     * @return the list of {@link EmailAddress} objects representing email addresses who are recipients of this
     * email message.
     */
    public List<EmailAddress> getRecipients() {
        if (recipients == null) {
            String recipientsStr = getConfigParam(EmailConfigParam.RECIPIENTS_TPL);
            if (recipientsStr != null) {
                recipients = new ArrayList<>();
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        recipients.add(new EmailAddress(recipient));
                    }
                }
            }
        }
        return recipients;
    }

    /**
     * Sets explicitly the list of email addresses who are recipients of this email message.
     * <p>
     * These are email addresses displayed in the field <code>To:</code> of this email message.
     *
     * @param recipientsList the list of {@link EmailAddress} objects representing email addresses to set as recipients
     *                       of this email message.
     */
    public void setRecipients(List<EmailAddress> recipientsList) {
        recipients = new ArrayList<>();
        recipients.addAll(recipientsList);
    }

    /**
     * Sets explicitly email addresses who are recipients of this email message.
     * <p>
     * These are email addresses displayed in the field <code>To:</code> of this email message.
     *
     * @param recipientsSequence the sequence of email address strings to set as recipients of this email message.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage recipients(String... recipientsSequence) {
        recipients = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                recipients.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

    /**
     * Excludes given email addresses from the list of recipients of this email message.
     *
     * @param recipientsSequence the sequence of email address strings to exclude.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage excludeFromRecipients(String... recipientsSequence) {
        if (recipients != null) {
            for (String recipient : recipientsSequence) {
                if (recipient != null && !recipient.trim().isEmpty() && recipients.size() > 1) {
                    recipients.remove(new EmailAddress(recipient));
                }
            }
        }
        return this;
    }

    /**
     * Gets list of email addresses who are CC recipients of this email message.
     * <p>
     * This is a list of email addresses displayed in the field <code>CC:</code> of this email message.
     * <p>
     * If CC recipients are not specified explicitly then they will be looked up in configuration parameters of
     * the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.cc.recipients}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.cc.recipients</b>.
     * <p>
     * Email addresses in the value of this configuration parameter can be delimited with "<code>;</code>":
     * <pre>
     * email.cc.recipients = user1@example.com;user2@example.com;user3@example.com
     * </pre>
     *
     * @return the list of {@link EmailAddress} objects representing email addresses who are CC recipients of this
     * email message.
     */
    public List<EmailAddress> getCcRecipients() {
        if (ccRecipients == null) {
            ccRecipients = new ArrayList<>();
            String recipientsStr = getConfigParam(EmailConfigParam.CC_RECIPIENTS_TPL);
            if (recipientsStr != null) {
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        ccRecipients.add(new EmailAddress(recipient));
                    }
                }
            }
        }
        return ccRecipients;
    }

    /**
     * Sets explicitly the list of email addresses who are CC recipients of this email message.
     * <p>
     * These are email addresses displayed in the field <code>CC:</code> of this email message.
     *
     * @param recipientsList the list of {@link EmailAddress} objects representing email addresses to set as
     *                       CC recipients of this email message.
     */
    public void setCcRecipients(List<EmailAddress> recipientsList) {
        ccRecipients = new ArrayList<>();
        ccRecipients.addAll(recipientsList);
    }

    /**
     * Sets explicitly email addresses who are CC recipients of this email message.
     * <p>
     * These are email addresses displayed in the field <code>CC:</code> of this email message.
     *
     * @param recipientsSequence the sequence of email address strings to set as CC recipients of this email message.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage ccRecipients(String... recipientsSequence) {
        ccRecipients = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                ccRecipients.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

    /**
     * Excludes given email addresses from the list of CC recipients of this email message.
     *
     * @param recipientsSequence the sequence of email address strings to exclude.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage excludeFromCcRecipients(String... recipientsSequence) {
        if (ccRecipients != null) {
            for (String recipient : recipientsSequence) {
                if (recipient != null && !recipient.trim().isEmpty()) {
                    ccRecipients.remove(new EmailAddress(recipient));
                }
            }
        }
        return this;
    }

    /**
     * Gets list of email addresses who are BCC recipients of this email message.
     * <p>
     * This is a list of email addresses displayed in the field <code>BCC:</code> of this email message.
     * <p>
     * If BCC recipients are not specified explicitly then they will be looked up in configuration parameters of
     * the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.bcc.recipients}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.bcc.recipients</b>.
     * <p>
     * Email addresses in the value of this configuration parameter can be delimited with "<code>;</code>":
     * <pre>
     * email.bcc.recipients = user1@example.com;user2@example.com;user3@example.com
     * </pre>
     *
     * @return the list of {@link EmailAddress} objects representing email addresses who are BCC recipients of this
     * email message.
     */
    public List<EmailAddress> getBccRecipients() {
        if (bccRecipients == null) {
            bccRecipients = new ArrayList<>();
            String recipientsStr = getConfigParam(EmailConfigParam.BCC_RECIPIENTS_TPL);
            if (recipientsStr != null) {
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        bccRecipients.add(new EmailAddress(recipient));
                    }
                }
            }
        }
        return bccRecipients;
    }

    /**
     * Sets explicitly the list of email addresses who are BCC recipients of this email message.
     * <p>
     * These are email addresses displayed in the field <code>BCC:</code> of this email message.
     *
     * @param recipientsList the list of {@link EmailAddress} objects representing email addresses to set as
     *                       BCC recipients of this email message.
     */
    public void setBccRecipients(List<EmailAddress> recipientsList) {
        bccRecipients = new ArrayList<>();
        bccRecipients.addAll(recipientsList);
    }

    /**
     * Sets explicitly email addresses who are BCC recipients of this email message.
     * <p>
     * These are email addresses displayed in the field <code>BCC:</code> of this email message.
     *
     * @param recipientsSequence the sequence of email address strings to set as BCC recipients of this email message.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage bccRecipients(String... recipientsSequence) {
        bccRecipients = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                bccRecipients.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

    /**
     * Gets list of email addresses who are supposed recipients of the replying on this email message.
     * <p>
     * If "replay to" recipients are not specified explicitly then they will be looked up in configuration parameters
     * of the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.reply.to}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.reply.to</b>.
     * <p>
     * Email addresses in the value of this configuration parameter can be delimited with "<code>;</code>":
     * <pre>
     * email.reply.to = user1@example.com;user2@example.com;user3@example.com
     * </pre>
     *
     * @return the list of {@link EmailAddress} objects representing email addresses who are supposed recipients of
     * the replying on this email message.
     */
    public List<EmailAddress> getReplyTo() {
        if (replyTo == null) {
            replyTo = new ArrayList<>();
            String recipientsStr = getConfigParam(EmailConfigParam.REPLY_TO_TPL);
            if (recipientsStr != null) {
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        replyTo.add(new EmailAddress(recipient));
                    }
                }
            }
        }
        return replyTo;
    }

    /**
     * Sets explicitly the list of email addresses who are supposed recipients of the replying on this email message.
     *
     * @param recipientsList the list of {@link EmailAddress} objects representing email addresses to set.
     */
    public void setReplyTo(List<EmailAddress> recipientsList) {
        replyTo = new ArrayList<>();
        replyTo.addAll(recipientsList);
    }

    /**
     * Sets explicitly email addresses who are supposed recipients of the replying on this email message.
     *
     * @param recipientsSequence the sequence of email address strings to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage replyTo(String... recipientsSequence) {
        replyTo = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                replyTo.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

    /**
     * Gets subject of this email message.
     * <p>
     * If subject is not specified explicitly then it will be looked up in configuration parameters of the RPA platform
     * under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.subject}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.subject</b>.
     *
     * @return string with subject of this email message.
     */
    public String getSubject() {
        if (subject == null) {
            subject = getConfigParam(EmailConfigParam.SUBJECT_TPL);
        }
        return subject != null ? subject : "";
    }

    /**
     * Sets explicitly the subject of this email message.
     *
     * @param subject string with subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets explicitly the subject of this email message.
     *
     * @param subject string with subject to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage subject(String subject) {
        setSubject(subject);
        return this;
    }

    /**
     * Gets charset of this email message body.
     * <p>
     * If charset is not specified explicitly then it will be looked up in configuration parameters of the RPA platform
     * under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.charset}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.charset</b>.
     *
     * @return string with charset of this email message body.
     */
    public String getCharset() {
        if (charset == null) {
            charset = getConfigParam(EmailConfigParam.CHARSET_TPL);
        }
        return charset;
    }

    /**
     * Sets explicitly the charset of this email message body.
     *
     * @param charset string with charset to set.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Sets explicitly the charset of this email message body.
     *
     * @param charset string with charset to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage charset(String charset) {
        setCharset(charset);
        return this;
    }

    /**
     * Gets name of template that defines content for this email message.
     * <p>
     * The template is expected to be created, managed and compiled within RPA platform. The name is used as
     * reference to it.
     * <p>
     * Properties that should be available within template can be specified using {@link #property(String, Object)}
     * method.
     * <p>
     * If the name of template is not specified explicitly then it will be looked up in configuration parameters
     * of the RPA platform under the key that depends on the actual {@link #typeName} value:
     * <pre>
     * {@code <typeName>.tpl}
     * </pre>
     * In case of default <code>typeName</code> the key of configuration parameter is <b>email.tpl</b>.
     *
     * @return string with name of template that defines content for this email message.
     */
    public String getTemplate() {
        if (template == null) {
            template = getConfigParam(EmailConfigParam.TEMPLATE_NAME_TPL);
        }
        return template;
    }

    /**
     * Sets explicitly the name of template that defines content for this email message.
     * <p>
     * The template is expected to be created, managed and compiled within RPA platform. The name is used as
     * reference to it.
     * <p>
     * Properties that should be available within template can be specified using {@link #property(String, Object)}
     * method.
     *
     * @param template string with name of template to set.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Sets explicitly the name of template that defines content for this email message.
     * <p>
     * The template is expected to be created, managed and compiled within RPA platform. The name is used as
     * reference to it.
     * <p>
     * Properties that should be available within template can be specified using {@link #property(String, Object)}
     * method.
     *
     * @param template string with name of template to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage template(String template) {
        setTemplate(template);
        return this;
    }

    /**
     * Gets the list of parts that constituents the body of this email message.
     * <p>
     * The email message body can be in a format of simple text or HTML. For compatibility, it can be presented in
     * both formats at the same time. To achieve it the body persisted as list of body parts where each part has
     * specific format. To get the full body in the text or HTML format methods {@link #getText()} or
     * {@link #getHtml()} should be used respectively.
     *
     * @return the list of {@link EmailBodyPart} objects representing body parts of this email message.
     */
    public List<EmailBodyPart> getBodyParts() {
        if (bodyParts == null) {
            bodyParts = new ArrayList<>();
        }
        return bodyParts;
    }

    /**
     * Sets the list of parts that constituents the body of this email message.
     * <p>
     * <b>This method is used for serialize/deserialize of this email message into JSON and shouldn't be used
     * explicitly.</b> To set a content of the body use following methods: {@link #text(String)},
     * {@link #addText(String)}, {@link #html(String)}, {@link #addHtml(String)}.
     *
     * @param bodyParts the list of {@link EmailBodyPart} objects representing body parts to set.
     */
    public void setBodyParts(List<EmailBodyPart> bodyParts) {
        this.bodyParts = new ArrayList<>();
        if (bodyParts != null) {
            this.bodyParts.addAll(bodyParts);
        }
        this.text = null;
        this.html = null;
    }

    /**
     * Sets given text string as body of this email message.
     * <p>
     * It removes all existing text body parts and adds only one text body part with given text.
     * <p>
     * Instead of actual text string this method accepts path to FreeMarker Template File (*.ftl) in the resources of
     * current RPA process module. In this case the content of the .ftl file will be used for adding of text body part.
     *
     * @param text the string with text to set or path to resource .ftl file with text to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage text(String text) {
        if (text != null) {
            List<EmailBodyPart> parts = getBodyParts();
            List<EmailBodyPart> textParts = parts.stream().filter(EmailBodyPart::isText).collect(Collectors.toList());
            if (!textParts.isEmpty()) {
                parts.removeAll(textParts);
            }
            parts.add(new EmailBodyPart(text, EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN));
            this.text = null;
            this.html = null;
        }
        return this;
    }

    /**
     * Adds given text string as text body part of this email message.
     * <p>
     * Instead of actual text string this method accepts path to Freemarker Template File (*.ftl) in the resources of
     * current RPA process module. In this case the content of the .ftl file will be used for adding of text body part.
     *
     * @param text the string with text to add or path to resource .ftl file with text to add.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage addText(String text) {
        if (text != null) {
            getBodyParts().add(new EmailBodyPart(text, EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN));
            this.text = null;
            this.html = null;
        }
        return this;
    }

    /**
     * Sets given HTML string as body of this email message.
     * <p>
     * It removes all existing HTML body parts and adds only one HTML body part with given HTML.
     * <p>
     * Instead of actual HTML string this method accepts path to Freemarker Template File (*.ftl) in the resources of
     * current RPA process module. In this case the content of the .ftl file will be used for adding of HTML body part.
     *
     * @param html the string with HTML to set or path to resource .ftl file with HTML to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage html(String html) {
        if (html != null) {
            List<EmailBodyPart> parts = getBodyParts();
            List<EmailBodyPart> htmlParts = parts.stream().filter(EmailBodyPart::isHtml).collect(Collectors.toList());
            if (!htmlParts.isEmpty()) {
                parts.removeAll(htmlParts);
            }
            parts.add(new EmailBodyPart(html, EmailBodyPart.CONTENT_TYPE_TEXT_HTML));
            this.text = null;
            this.html = null;
        }
        return this;
    }

    /**
     * Adds given HTML string as HTML body part of this email message.
     * <p>
     * Instead of actual HTML string this method accepts path to Freemarker Template File (*.ftl) in the resources of
     * current RPA process module. In this case the content of the .ftl file will be used for adding of HTML body part.
     *
     * @param html the string with HTML to add or path to resource .ftl file with HTML to add.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage addHtml(String html) {
        if (html != null) {
            getBodyParts().add(new EmailBodyPart(html, EmailBodyPart.CONTENT_TYPE_TEXT_HTML));
            this.text = null;
            this.html = null;
        }
        return this;
    }

    /**
     * Checks whether body parts of this email message has html parts.
     *
     * @return <code>true</code> if at least one of body parts is a html part or <code>false</code> otherwise.
     * @see EmailBodyPart#isHtml()
     */
    public boolean hasHtml() {
        return getBodyParts().stream().anyMatch(part -> part.isHtml() || part.isRtf());
    }

    /**
     * Checks whether body parts of this email message has text parts.
     *
     * @return <code>true</code> if at least one of body parts is a text part or <code>false</code> otherwise.
     * @see EmailBodyPart#isText()
     */
    public boolean hasText() {
        return getBodyParts().stream().anyMatch(part -> part.isText() || part.isRtf());
    }

    /**
     * Gets text representation of this email message body.
     * <p>
     * This method takes all text body parts using {@link #getBodyParts()} and joining them using "<code>\n</code>"
     * delimiter. If text parts are absent it gets html presentation of the body using {@link #getHtml()} and
     * returns this html preliminarily converted into simple text format.
     *
     * @return string with email message body content in simple text format.
     */
    public String getText() {
        if (text == null) {
            if (hasText()) {
                text = getBodyParts().stream()
                        .map(part -> part.isText() || part.isRtf() ? part.getContent(bodyProperties) : null)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("\n")).trim();
            } else if (hasHtml()) {
                text = Jsoup.parse(getHtml()).text();
            }
        }
        return text;
    }

    /**
     * Gets HTML representation of this email message body.
     * <p>
     * This method takes all HTML body parts using {@link #getBodyParts()} and merges them into single HTML. If HTML
     * body parts are absent then instead of them text parts are taken.
     *
     * @return string with email message body content in HTML format.
     */
    public String getHtml() {
        if (html == null) {
            List<String> parts = new ArrayList<>();
            if (hasHtml()) {
                parts = getBodyParts().stream()
                        .map(part -> part.isHtml() || part.isRtf() ? part.getContent(bodyProperties) : null)
                        .filter(Objects::nonNull).collect(Collectors.toList());
            } else if (hasText()) {
                parts = getBodyParts().stream()
                        .map(part -> part.isText() ? part.getContent(bodyProperties) : null)
                        .filter(Objects::nonNull).collect(Collectors.toList());
            }

            List<String> htmlAttrs = new ArrayList<>();
            List<String> headAttrs = new ArrayList<>();
            List<String> heads = new ArrayList<>();
            List<String> bodyAttrs = new ArrayList<>();
            List<String> bodies = new ArrayList<>();

            for (String part : parts) {
                Matcher m = htmlTagExtractor.matcher(part);
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        htmlAttrs.add(m.group(1));
                    }
                }
                m = headTagExtractor.matcher(part);
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        headAttrs.add(m.group(1));
                    }
                    if (!m.group(2).isEmpty()) {
                        heads.add(m.group(2));
                    }
                }
                m = bodyTagExtractor.matcher(part);
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        bodyAttrs.add(m.group(1));
                    }
                    if (!m.group(2).isEmpty()) {
                        bodies.add(m.group(2));
                    }
                }
            }

            if (heads.size() > 0) {
                html = String.format("<html%s><head%s>%s</head><body%s>%s</body></html>",
                        String.join(" ", htmlAttrs),
                        String.join(" ", headAttrs),
                        String.join("\n", heads),
                        String.join(" ", bodyAttrs),
                        String.join("\n", bodies)
                );
            } else {
                html = String.format("<html%s><body%s>%s</body></html>",
                        String.join(" ", htmlAttrs),
                        String.join(" ", bodyAttrs),
                        String.join("\n", bodies)
                );
            }
        }
        return html;
    }

    /**
     * Gets properties used for substitution of variables within the body of this email message.
     * <p>
     * The text or HTML content of the body part can be a FreeMarker template where some variables are used. These
     * properties contains actual values of FreeMarker template variables. When the email message is ready to send the
     * template is compiled and property values are substituted instead of variables based on their names that are
     * correspond to property keys.
     *
     * @return the map where the key is a name of variable in the body template and the value is an object that should
     * be substituted instead of corresponding variable.
     */
    public Map<String, Object> getBodyProperties() {
        return bodyProperties;
    }

    /**
     * Sets the map with properties used for substitution of variables within the body of this email message.
     * <p>
     * This method fully overwrites existing properties map.
     *
     * @param bodyProperties map with properties to set.
     */
    public void setBodyProperties(Map<String, Object> bodyProperties) {
        this.bodyProperties = bodyProperties;
    }

    /**
     * Puts a new property into the properties map  used for substitution of variables within the body of this
     * email message.
     * <p>
     * The text or HTML content of the body part can be a FreeMarker template where some variables are used. This
     * method allows to specify values that should be substituted instead of these variables during template
     * compilation.
     *
     * @param key   the string that identifies a name of variable in the body template.
     * @param value the object that should be substituted instead of corresponding variable.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage property(String key, Object value) {
        bodyProperties.put(key, value);
        return this;
    }

    /**
     * Gets attachments attached to this email message.
     *
     * @return the list of {@link EmailAttachment} representing attachments attached to this email message.
     */
    public List<EmailAttachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    /**
     * Sets attachments for this email message.
     *
     * @param attachments the list of {@link EmailAttachment} representing attachments to set.
     */
    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = new ArrayList<>();
        this.attachments.addAll(attachments);
    }

    /**
     * Attaches given file to this email message.
     *
     * @param file the {@link File} object representing file to attach.
     * @return this object to allow joining of methods calls into chain.
     * @throws IOException in case of errors during reading of given file.
     */
    public EmailMessage attach(File file) throws IOException {
        getAttachments().add(new EmailAttachment(file.toPath()));
        return this;
    }

    /**
     * Attaches given file to this email message.
     *
     * @param filePath the {@link Path} to the file that need to be attached.
     * @return this object to allow joining of methods calls into chain.
     * @throws IOException in case of errors during reading of given file.
     */
    public EmailMessage attach(Path filePath) throws IOException {
        getAttachments().add(new EmailAttachment(filePath));
        return this;
    }

    /**
     * Attaches given file content as attachment to this email message.
     *
     * @param fileName    the string with file name that should be used for attached file.
     * @param fileContent the {@link InputStream} that provides the content of file to attach.
     * @param mimeType    the string with content type of file to attach.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage attach(String fileName, InputStream fileContent, String mimeType) {
        getAttachments().add(new EmailAttachment(fileName, fileContent, mimeType));
        return this;
    }

    /**
     * Checks whether this email message has attached files.
     *
     * @return <code>true</code> if this email message has attached files or <code>false</code> otherwise.
     */
    public boolean hasAttachments() {
        List<EmailAttachment> attachments = getAttachments();
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * Gets the value of read/unread flag of this email message.
     *
     * @return <code>true</code> is this email message is READ or <code>false</code> otherwise.
     */
    public boolean isRead() {
        return isRead != null && isRead;
    }

    /**
     * Gets the value of read/unread flag of this email message.
     *
     * @return <code>true</code> is this email message is UNREAD or <code>false</code> otherwise.
     */
    @JsonIgnore
    public boolean isUnread() {
        return isRead != null && !isRead;
    }

    /**
     * Sets read/unread flag for this email message.
     * <p>
     * The changing of this flag actually doesn't change anything in the mailbox. To apply these change in the mailbox
     * use the method {@link EmailClient#updateMessage(EmailMessage)}.
     *
     * @param read the value of read/unread flag to set. It should be <code>true</code> if it's necessary to mark this
     *             email message as read and <code>false</code> as unread.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Marks this email message as read.
     * <p>
     * This method actually doesn't change anything in the mailbox. To apply these change in the mailbox use
     * the method {@link EmailClient#updateMessage(EmailMessage)}.
     *
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage markRead() {
        setRead(true);
        return this;
    }

    /**
     * Marks this email message as unread.
     * <p>
     * This method actually doesn't change anything in the mailbox. To apply these change in the mailbox use
     * the method {@link EmailClient#updateMessage(EmailMessage)}.
     *
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailMessage markUnread() {
        setRead(false);
        return this;
    }


    /**
     * Gets the email message that this email message forwards.
     *
     * @return the email message that this email message forwards or <code>null</code> if this message is not a
     * forwarding of some email message.
     */
    public EmailMessage getForwardedMessage() {
        return forwardedMessage;
    }

    /**
     * Gets the email message that this email message replies on.
     *
     * @return the email message that this email message replies on or <code>null</code> if this message is not a
     * replying on some email message.
     */
    public EmailMessage getReplyOnMessage() {
        return replyOnMessage;
    }

    /**
     * Generates a new email message that is a forwarding of this email message.
     * <p>
     * Before sending the generated email message should be set with list of recipients and additional content.
     * Here is an example of using this method:
     * <pre>
     * {@code @Inject}
     *  private EmailSender emailSender;
     *
     *  public void execute() {
     *      EmailMessage message = ...;
     *      ...
     *      EmailMessage fwdMessage = message.forwardMessage(true)
     *                                  .recipients("user@example.com")
     *                                  .html("This email has been forwarded by robot.");
     *      emailSender.send(fwdMessage);
     *      ...
     *  }
     * </pre>
     *
     * @param withAttachments whether files attached to this email message should be attached to the generated message.
     * @return generated email message that is a forwarding of this email message.
     */
    public EmailMessage forwardMessage(boolean withAttachments) {
        EmailMessage msg = new EmailMessage();
        msg.setSubject("Fwd: " + getSubject());
        msg.setCharset(getCharset());
        msg.forwardedMessage = this;
        if (withAttachments) {
            msg.setAttachments(getAttachments());
        }
        return msg;
    }

    /**
     * Generates a new email message that is a replying on this email message.
     * <p>
     * Before sending the generated email message should be set with additional content and optionally with corrected
     * list of recipients. By default the list of recipients of the generated message includes only the email address
     * returned by {@link #getFrom()} or by {@link #getSender()} of this email message.
     * <p>
     * Here is an example of using this method:
     * <pre>
     * {@code @Inject}
     *  private EmailSender emailSender;
     *
     *  public void execute() {
     *      EmailMessage message = ...;
     *      ...
     *      emailSender.send(message.replyMessage(true).html("Robot replied to this email."));
     *      ...
     *  }
     * </pre>
     *
     * @param withAttachments whether files attached to this email message should be attached to the generated message.
     * @return generated email message that is a replying on this email message.
     */
    public EmailMessage replyMessage(boolean withAttachments) {
        EmailMessage msg = new EmailMessage();
        List<EmailAddress> recipients = getReplyTo();
        if (recipients == null || recipients.isEmpty()) {
            recipients = new ArrayList<>();
            recipients.add(getFrom() != null ? getFrom() : getSender());
        }
        msg.setRecipients(recipients);
        msg.setSubject("Re: " + getSubject());
        msg.setCharset(getCharset());
        msg.replyOnMessage = this;
        if (withAttachments) {
            msg.setAttachments(getAttachments());
        }
        return msg;
    }

    /**
     * Generates a new email message that is a replying to all recipients of this email message.
     * <p>
     * Before sending the generated email message should be set with additional content and optionally with corrected
     * list of recipients. By default the list of recipients of the generated message includes all TO and CC recipients
     * of this email message excluding the current sender who is going to send the generated message.
     * <p>
     * Here is an example of using this method:
     * <pre>
     * {@code @Inject}
     *  private EmailSender emailSender;
     *
     *  public void execute() {
     *      EmailMessage message = ...;
     *      ...
     *      emailSender.send(message.replyAllMessage(true).html("Robot replied to all participants of this email."));
     *      ...
     *  }
     * </pre>
     *
     * @param withAttachments whether files attached to this email message should be attached to the generated message.
     * @return generated email message that is a replying to all recipients of this email message.
     */
    public EmailMessage replyAllMessage(boolean withAttachments) {
        EmailMessage msg = replyMessage(withAttachments);
        List<EmailAddress> recipients = msg.getRecipients();
        EmailAddress currentSender = msg.getSender();
        recipients.addAll(getRecipients().stream()
                .filter(r -> !r.equals(currentSender) && !recipients.contains(r))
                .collect(Collectors.toList()));
        msg.setCcRecipients(getCcRecipients().stream()
                .filter(r -> !r.equals(currentSender) && !recipients.contains(r))
                .collect(Collectors.toList()));
        return msg;
    }

    /**
     * Sends this email message using provided email sender.
     * <p>
     * Does nothing if this email message has not been constructed using {@link #EmailMessage(EmailSender)}
     * or {@link #EmailMessage(String, EmailSender)}. In such cases is necessary to use {@link #send(EmailSender)}
     * instead.
     *
     * @throws EmailMessagingException in case of some errors.
     */
    public void send() {
        if (emailSender != null) {
            emailSender.sendMessage(this);
        }
    }

    /**
     * Sends this email message using given email sender.
     *
     * @param emailSender the {@link EmailSender} that should be used to send this email message.
     * @throws EmailMessagingException in case of some errors.
     */
    public void send(EmailSender emailSender) {
        if (emailSender != null) {
            this.emailSender = emailSender;
            this.emailSender.sendMessage(this);
        }
    }

    /**
     * Returns a string representation of this email message.
     *
     * @return string representation of this email message.
     */
    @Override
    public String toString() {
        return "EmailMessage{" +
                "id='" + id + '\'' +
                ", senderName='" + senderName + '\'' +
                ", subject='" + subject + '\'' +
                ", bodyParts=" + bodyParts +
                '}';
    }

    /**
     * Serializes this email message into JSON.
     * <p>
     * This method is recommended to use when it's necessary to transfer email message somewhere as text,
     * e.g. between steps of the RPA process.
     *
     * @param isPrettyPrint whether the output JSON should have a pretty look or should be a one line string.
     * @return JSON string with serialize email message.
     * @throws JsonProcessingException in case of some errors during JSON processing.
     */
    public String toJson(boolean isPrettyPrint) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return isPrettyPrint ? om.writerWithDefaultPrettyPrinter().writeValueAsString(this) : om.writeValueAsString(this);
    }

    /**
     * Deserializes the email message from JSON.
     * <p>
     * This method is recommended to use when it's necessary to restore email message after transferring from somewhere
     * as text, e.g. between steps of the RPA process.
     *
     * @param json the JSON string that needs to deserialize into email message.
     * @return the email message deserialized from JSON.
     * @throws JsonProcessingException in case of some errors during JSON parsing.
     */
    public static EmailMessage fromJson(String json) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, EmailMessage.class);
    }

    /**
     * Performs some preparation steps before sending of this email message.
     * <p>
     * This method is intended to be overridden by subclasses if its necessary to specify values of variables used
     * within FreeMarker template of the body. Also here can be done some other untypical actions like setting of
     * charsets, subject etc.
     */
    protected void beforeSend() {
        // do some preparations here for subclasses
    }

    /**
     * Internal implementation of before send method.
     * <p>
     * This method is used to perform some preparation steps where access to RPA platform services is necessary.
     *
     * @param rpaServices instance of RPA services accessor that allows to access RPA platform services like secret vault or
     *                    templates management
     */
    /*package*/ void beforeSend(RPAServicesAccessor rpaServices) {
        if (rpaServices != null && !hasHtml() && !hasText() && getTemplate() != null) {
            try {
                byte[] content = rpaServices.evaluateTemplate(getTemplate(), getBodyProperties());
                String charset = getCharset();
                charset = charset == null || charset.trim().isEmpty() ? StandardCharsets.UTF_8.name() : charset;
                html(new String(content, charset));
            } catch (Exception e) {
                throw new EmailMessagingException(String.format(
                        "Compiling of email message content based on template '%s' has failed.",
                        getTemplate()
                ), e);
            }
        }
        beforeSend();
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the key that depends on the actual
     * value of {@link #typeName}.
     *
     * @param template the key template that depends on value of {@link #typeName}.
     * @return string value of corresponding configuration parameter or <code>null</code> if parameter is
     * not found or {@link EmailSender} is not provided for this email message.
     */
    protected String getConfigParam(String template) {
        String result;

        if (emailSender == null) {
            return null;
        }

        result = emailSender.getConfigParam(String.format(template, typeName));

        if (result == null && !DEFAULT_EMAIL_TYPE_NAME.equals(typeName)) {
            result = emailSender.getConfigParam(String.format(template, DEFAULT_EMAIL_TYPE_NAME));
        }

        return result;
    }
}
