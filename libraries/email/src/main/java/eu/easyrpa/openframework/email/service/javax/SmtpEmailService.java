package eu.easyrpa.openframework.email.service.javax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.service.EmailServiceSecret;
import eu.easyrpa.openframework.email.service.MessageConverter;
import eu.easyrpa.openframework.email.service.OutboundEmailProtocol;
import eu.easyrpa.openframework.email.service.OutboundEmailService;

import javax.mail.*;
import java.util.Properties;

/**
 * Implementation of outbound email service that is working based on SMTP protocol.
 */
public class SmtpEmailService implements OutboundEmailService {

    private final String host;

    private final String port;

    private final OutboundEmailProtocol protocol;

    private final String user;

    private final String password;

    private final Session session;

    private MessageConverter<Message> messageConverter;

    public SmtpEmailService(String server, OutboundEmailProtocol protocol, String secret) {

        if (server.contains(":")) {
            String[] parts = server.split(":");
            this.host = parts[0];
            this.port = parts[1];
        } else {
            this.host = server;
            this.port = protocol.getDefaultPort();
        }

        this.protocol = protocol;

        try {
            EmailServiceSecret credentials = new ObjectMapper().readValue(secret, EmailServiceSecret.class);
            user = credentials.getUser();
            password = credentials.getPassword();
        } catch (JsonProcessingException e) {
            throw new EmailMessagingException(e);
        }

        this.session = Session.getInstance(getConfigurationFor(protocol), null);
        this.messageConverter = new MimeMessageConverter(this.session);
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public OutboundEmailProtocol getProtocol() {
        return protocol;
    }

    public Session getSession() {
        return session;
    }

    public MessageConverter<Message> getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter<Message> messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public void send(EmailMessage message) throws EmailMessagingException {
        if (message.getSender() == null) {
            message.setSender(this.user);
            message.excludeFromRecipients(this.user);
            message.excludeFromCcRecipients(this.user);
        }

        Message nativeMessage = this.messageConverter.convertToNativeMessage(message);

        try (Transport transport = this.session.getTransport(protocol.getProtocolName())) {
            Address[] recipients = nativeMessage.getAllRecipients();
            if (recipients == null || recipients.length == 0) {
                throw new EmailMessagingException("No email recipients specified.");
            }
            transport.connect(this.user, this.password);
            transport.sendMessage(nativeMessage, recipients);
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private Properties getConfigurationFor(OutboundEmailProtocol protocol) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", protocol.getProtocolName());
        props.put(String.format("mail.%s.host", protocol.getProtocolName()), host);
        props.put(String.format("mail.%s.port", protocol.getProtocolName()), port);

        if (protocol == OutboundEmailProtocol.SMTP_OVER_TLS) {
            props.put(String.format("mail.%s.auth", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.starttls.enable", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.starttls.required", protocol.getProtocolName()), "true");

        } else if (protocol == OutboundEmailProtocol.SMTPS) {
            props.put(String.format("mail.%s.auth", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.ssl.enable", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.socketFactory.class", protocol.getProtocolName()), "javax.net.ssl.SSLSocketFactory");
            props.put(String.format("mail.%s.socketFactory.port", protocol.getProtocolName()), port);
            props.put(String.format("mail.%s.socketFactory.fallback", protocol.getProtocolName()), "false");
        }
        return props;
    }
}
