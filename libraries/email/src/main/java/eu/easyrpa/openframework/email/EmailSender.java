package eu.easyrpa.openframework.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.email.constants.EmailConfigParam;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.service.EmailServiceFactory;
import eu.easyrpa.openframework.email.service.EmailServiceSecret;
import eu.easyrpa.openframework.email.service.OutboundEmailProtocol;
import eu.easyrpa.openframework.email.service.OutboundEmailService;

import javax.inject.Inject;

/**
 * This is an email service that provides functionality for sending of messages.
 * <p>
 * Mainly this class does only one thing. The sending of email messages. But to do it it's necessary to provide set
 * of configuration parameters that makes the code of RPA process overloaded and complex to understand. Using of
 * {@link RPAServicesAccessor} this class gets all necessary configuration parameters and thus moving out the
 * definition of them from RPA process code. In conjunction with using of {@link Inject} annotation the sending of
 * email messages becomes easy and clear:
 * <pre>
 * {@code @Inject}
 *  private EmailSender emailSender;
 *
 *  public void execute() {
 *      ...
 *     emailSender.send(new EmailMessage().subject("Test email").text("This message was sent by robot."));
 *      ...
 *  }
 * </pre>
 *
 * @see EmailMessage
 */
public class EmailSender {

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Outbound email server URL.
     */
    private String server;

    /**
     * Protocol used by outbound email server to work with mailbox.
     */
    private OutboundEmailProtocol protocol;

    /**
     * Secret information necessary to perform authentication to specific mailbox on the server.
     */
    private String secret;

    /**
     * Specific instance of outbound email service that depends on protocol used by outbound email server.
     */
    private OutboundEmailService service;

    /**
     * Default constructor of this EmailSender.
     * <p>
     * This constructor should be used in case of manual providing of parameters for connection with outbound email
     * server or if its necessary to work with more than one email server at the same time. E.g.:
     * <pre>
     *  EmailSender sender1 = new EmailSender().server("smtp1.mail.com").protocol("smtp_over_tsl")
     *          .secret("{ \"user\": \"user1@mail.com\", \"password": \"passphrase\" }");
     *
     *  EmailSender sender2 = new EmailSender().server("smtp2.mail.com").protocol("smtp_over_tsl")
     *          .secret("{ \"user\": \"user2@mail.com\", \"password": \"passphrase\" }");
     *   ...
     *  });
     * </pre>
     */
    public EmailSender() {
    }

    /**
     * Constructs EmailSender with provided <code>RPAServicesAccessor</code>.
     * <p>
     * This constructor is used in case of injecting of this EmailSender using {@link Inject} annotation. This is
     * preferable way of working with this class. E.g.:
     * <pre>
     * {@code @Inject}
     *  private EmailSender emailSender;
     *
     *  public void execute() {
     *      ...
     *      new EmailMessage(emailSender).subject(SUBJECT).text(BODY).send();
     *      ...
     *  }
     * </pre>
     *
     * @param rpaServices instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     * @see EmailMessage
     */
    @Inject
    public EmailSender(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    /**
     * Gets outbound email server URL.
     * <p>
     * If this server URL is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b><code>outbound.email.server</code></b>.
     *
     * @return outbound email server URL string.
     */
    public String getServer() {
        if (server == null) {
            server = getConfigParam(EmailConfigParam.OUTBOUND_EMAIL_SERVER);
        }
        return server;
    }

    /**
     * Sets explicitly the value of outbound email server URL.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param emailServerHostAndPort outbound email server URL with host name and port to set.
     */
    public void setServer(String emailServerHostAndPort) {
        this.server = emailServerHostAndPort;
        this.service = null;
    }

    /**
     * Sets explicitly the value of outbound email server URL.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param emailServerHostAndPort outbound email server URL with host name and port to set.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailSender server(String emailServerHostAndPort) {
        setServer(emailServerHostAndPort);
        return this;
    }

    /**
     * Gets protocol that is necessary to use for working with outbound email server.
     * <p>
     * If this protocol is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b><code>"outbound.email.protocol"</code></b>.
     * <p>
     * If it's not specified in configurations parameters either then email will be sending using available RPA services.
     *
     * @return {@link OutboundEmailProtocol} representing necessary to use protocol.
     */
    public OutboundEmailProtocol getProtocol() {
        if (protocol == null) {
            String protocolStr = getConfigParam(EmailConfigParam.OUTBOUND_EMAIL_PROTOCOL);
            if (protocolStr != null) {
                protocol = OutboundEmailProtocol.valueOf(protocolStr.toUpperCase());
            }
        }
        return protocol;
    }

    /**
     * Sets explicitly the value of protocol that is necessary to use for working with outbound email server.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param protocol {@link OutboundEmailProtocol} that is necessary to use.
     */
    public void setProtocol(OutboundEmailProtocol protocol) {
        this.protocol = protocol;
        this.service = null;
    }

    /**
     * Sets explicitly the value of protocol that is necessary to use for working with outbound email server.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param protocol {@link OutboundEmailProtocol} that is necessary to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailSender protocol(OutboundEmailProtocol protocol) {
        setProtocol(protocol);
        return this;
    }

    /**
     * Sets explicitly the value of protocol that is necessary to use for working with outbound email server.
     * <p>
     * This parameter can be changed at any time of working with this class. It will switch to work with a new value
     * before calling of next service method.
     *
     * @param protocol string with name of protocol that is necessary to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public EmailSender protocol(String protocol) {
        setProtocol(OutboundEmailProtocol.valueOf(protocol.toUpperCase()));
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
     * If this secret string is not specified explicitly then at it will be looked up in secret vault of the
     * RPA platform. The secret vault alias that is necessary to lookup is expected to be specified in configuration
     * parameters under the key <b><code>"outbound.email.secret"</code></b>.
     *
     * @return JSON string with secret information.
     */
    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(EmailConfigParam.OUTBOUND_EMAIL_SECRET);
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
    public EmailSender secret(String secret) {
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
    public EmailSender secret(String userName, String password) {
        try {
            setSecret(new ObjectMapper().writeValueAsString(new EmailServiceSecret(userName, password)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Sends given email message.
     *
     * @param message the email message to send.
     * @throws EmailMessagingException in case of some errors.
     */
    public void send(EmailMessage message) {
        message.send(this);
    }

    /**
     * Actually sends given email message.
     *
     * @param message the email message to send.
     * @throws EmailMessagingException in case of some errors.
     */
    /*package*/ void sendMessage(EmailMessage message) {
        initService();
        message.beforeSend(rpaServices);
        this.service.send(message);
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found or {@link RPAServicesAccessor} is not defined for this email sender.
     */
    /*package*/ String getConfigParam(String key) {
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
     * Creates instance of outbound email service depended on protocol is going to be used by this email sender
     * and supported by target outbound email server.
     * <p>
     * {@link EmailServiceFactory} is responsible for creation of specific outbound email service.
     */
    private void initService() {
        if (this.service == null) {
            this.service = EmailServiceFactory.getInstance().getOutboundService(
                    rpaServices, getServer(), getProtocol(), getSecret()
            );
        }
    }
}
