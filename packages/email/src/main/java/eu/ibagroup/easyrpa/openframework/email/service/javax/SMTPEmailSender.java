package eu.ibagroup.easyrpa.openframework.email.service.javax;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.EmailSender;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class SMTPEmailSender implements EmailSender {
    private final SMTPEmailSender.SupportedProtocol protocol;

    private final String host;

    private final String port;

    private final String user;

    private final String password;

    private Session session;

    private MessageConverter<Message> messageConverter;

    public SMTPEmailSender(SMTPEmailSender.SupportedProtocol protocol, String host, String port, String user, String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.initStore();
    }

    private void initStore() {
        Properties props = this.protocol.configure(this);
        this.session = Session.getDefaultInstance(props, (Authenticator) null);
        this.messageConverter = new JavaxMimeMessageConverter(this.session);
    }

    public void sendEmail(EmailMessage message) throws EmailMessagingException {
        Message nativeMessage = (Message) this.messageConverter.createNativeMessage(message);
        Transport transport = null;

        try {
            transport = this.session.getTransport(this.protocol.getProtocolName());
            transport.connect(this.user, this.password);
            transport.sendMessage(nativeMessage, nativeMessage.getAllRecipients());
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                }
            }
        }
    }

    public void sendEmail(String from, String to, String subject, String body) throws EmailMessagingException {
        EmailMessage message = EmailMessage.EmailMessageBuilder.newMessage(EmailAddress.of(from), new EmailAddress[] { EmailAddress.of(to) }).text(body).subject(subject).build();
        this.sendEmail(message);
    }

    public void sendEmail(String from, List<String> to, String subject, String body) throws EmailMessagingException {
        List<EmailAddress> toEmailAddresses = to.stream().map(EmailAddress::of).collect(Collectors.toList());
        EmailMessage message = EmailMessage.EmailMessageBuilder.newMessage(EmailAddress.of(from), toEmailAddresses).text(body).subject(subject).build();
        this.sendEmail(message);
    }

    public MessageConverter<Message> getMessageConverter() {
        return this.messageConverter;
    }

    public void setMessageConverter(MessageConverter<Message> messageConverter) {
        this.messageConverter = messageConverter;
    }

    public String getHost() {
        return this.host;
    }

    public String getPort() {
        return this.port;
    }

    public enum SupportedProtocol {
        SMTP("smtp") {
            public Properties configure(SMTPEmailSender client) {
                return super.configure(client);
            }
        }, SMTP_OVER_TSL("smtp") {
            public Properties configure(SMTPEmailSender client) {
                Properties props = super.configure(client);
                props.put("mail.smtp.starttls.enable", "true");
                return props;
            }
        }, SMTP_OVER_SSL("smtp") {
            public Properties configure(SMTPEmailSender client) {
                Properties props = super.configure(client);
                props.put("mail.smtp.socketFactory.port", client.getPort());
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                return props;
            }
        };

        private final String protocolName;

        SupportedProtocol(String protocolName) {
            this.protocolName = protocolName;
        }

        public static SMTPEmailSender.SupportedProtocol forValue(String value) {
            if ("smtp".equals(value)) {
                return SMTP;
            } else if ("smtp_over_tsl".equals(value)) {
                return SMTP_OVER_TSL;
            } else if ("smtp_over_ssl".equals(value)) {
                return SMTP_OVER_SSL;
            } else {
                throw new IllegalArgumentException("No 'SupportedProtocol' defined for value '" + value + "'");
            }
        }

        public Properties configure(SMTPEmailSender client) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", client.getHost());
            props.put("mail.smtp.port", client.getPort());
            return props;
        }

        public String getProtocolName() {
            return this.protocolName;
        }
    }
}
