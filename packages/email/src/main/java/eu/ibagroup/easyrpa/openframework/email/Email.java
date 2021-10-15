package eu.ibagroup.easyrpa.openframework.email;

import eu.ibagroup.easyrpa.openframework.core.model.RPASecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesProvider;
import eu.ibagroup.easyrpa.openframework.email.core.templates.FreeMarkerTemplate;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.javax.SMTPEmailSender;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.*;

public class Email {

    private static final String DEFAULT_EMAIL_TYPE_NAME = "email";
    private static final String DEFAULT_EMAIL_SERVICE_PROTOCOL = "smtp";
    private static final String DEFAULT_EMAIL_SUBJECT = "Robot Email";

    private static final String SERVICE_CFG_NAME_TPL = "%s.service";
    private static final String SERVICE_PROTOCOL_CFG_NAME_TPL = "%s.service.protocol";
    private static final String SERVICE_CREDENTIALS_CFG_NAME_TPL = "%s.service.credentials";
    private static final String SENDER_CFG_NAME_TPL = "%s.sender";
    private static final String SENDER_NAME_CFG_NAME_TPL = "%s.sender.name";
    private static final String RECIPIENTS_CFG_NAME_TPL = "%s.recipients";
    private static final String CC_RECIPIENTS_CFG_NAME_TPL = "%s.cc.recipients";
    private static final String BCC_RECIPIENTS_CFG_NAME_TPL = "%s.bcc.recipients";
    private static final String SUBJECT_CFG_NAME_TPL = "%s.subject";
    private static final String BODY_TEMPLATE_CFG_NAME_TPL = "%s.body.tpl";
    private static final String CHARSET_CFG_NAME_TPL = "%s.charset";

    private RPAServicesProvider rpaServices;

    private String typeName = DEFAULT_EMAIL_TYPE_NAME;

    private String emailService;

    private String emailServiceProtocol;

    private RPASecretCredentials credentials;

    private String subject;

    private EmailAddress sender;

    private String senderName;

    private List<EmailAddress> recipients;

    private List<EmailAddress> ccRecipients;

    private List<EmailAddress> bccRecipients;

    private String body;

    private String charset;

    private Map<String, Object> bodyProperties = new HashMap<>();

    private List<EmailAttachment> attachments = new ArrayList<>();

    private HashMap<String, Object> root;

    public Email() {
    }

    public Email(String typeName) {
        this.typeName = typeName;
    }

    public Email(RPAServicesProvider rpaServices) {
        this.rpaServices = rpaServices;
    }

    public Email(String typeName, RPAServicesProvider rpaServices) {
        this.typeName = typeName;
        this.rpaServices = rpaServices;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Email> T create() {
        try {
            return (T) MethodHandles.lookup().lookupClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new EmailMessagingException("Email class instantiation has failed.");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Email> T create(RPAServicesProvider rpaServices) {
        try {
            return (T) MethodHandles.lookup().lookupClass().getDeclaredConstructor(RPAServicesProvider.class).newInstance(rpaServices);
        } catch (Exception e) {
            throw new EmailMessagingException("Email class instantiation has failed.");
        }
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getEmailService() {
        if (emailService == null) {
            emailService = getParam(SERVICE_CFG_NAME_TPL);
        }

        if (emailService == null) {
            throw new EmailMessagingException("Email service (host and port) is null. Setup service using set method or configuration.");
        }
        return emailService;
    }

    public void setEmailService(String emailServiceHostAndPort) {
        this.emailService = emailServiceHostAndPort;
    }

    public Email service(String emailServiceHostAndPort) {
        setEmailService(emailServiceHostAndPort);
        return this;
    }

    public String getEmailServiceProtocol() {
        if (emailServiceProtocol == null) {
            emailServiceProtocol = getParam(SERVICE_PROTOCOL_CFG_NAME_TPL);
        }
        return emailServiceProtocol != null ? emailServiceProtocol : DEFAULT_EMAIL_SERVICE_PROTOCOL;
    }

    public void setEmailServiceProtocol(String emailServiceProtocol) {
        this.emailServiceProtocol = emailServiceProtocol;
    }

    public Email serviceProtocol(String emailServiceProtocol) {
        setEmailServiceProtocol(emailServiceProtocol);
        return this;
    }

    public RPASecretCredentials getCredentials() {
        if (credentials == null) {
            String secretAlias = getParam(SERVICE_CREDENTIALS_CFG_NAME_TPL);
            if (secretAlias != null) {
                credentials = rpaServices.getCredentials(secretAlias);
            }
        }
        return credentials;
    }

    public void setCredentials(RPASecretCredentials credentials) {
        this.credentials = credentials;
    }

    public Email credentials(String username, String password) {
        setCredentials(new RPASecretCredentials(username, password));
        return this;
    }

    public String getSubject() {
        if (subject == null) {
            subject = getParam(SUBJECT_CFG_NAME_TPL);
        }
        return subject != null ? subject : DEFAULT_EMAIL_SUBJECT;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Email subject(String subject) {
        setSubject(subject);
        return this;
    }

    public EmailAddress getSender() {
        if (sender == null) {
            String senderAddress = getParam(SENDER_CFG_NAME_TPL);
            sender = senderAddress != null ? EmailAddress.of(senderAddress) : null;
        }
        return sender;
    }

    public void setSender(String senderAddress) {
        this.sender = senderAddress != null ? EmailAddress.of(senderAddress) : null;
    }

    public Email sender(String senderAddress) {
        setSender(senderAddress);
        return this;
    }

    public String getSenderName() {
        if (senderName == null) {
            senderName = getParam(SENDER_NAME_CFG_NAME_TPL);
        }
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Email senderName(String senderName) {
        setSenderName(senderName);
        return this;
    }

    public List<EmailAddress> getRecipients() {
        if (recipients == null) {
            String recipientsStr = getParam(RECIPIENTS_CFG_NAME_TPL);
            if (recipientsStr != null) {
                recipients = new ArrayList<>();
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        recipients.add(EmailAddress.of(recipient));
                    }
                }
                return recipients;
            }
        } else {
            return recipients;
        }
        throw new EmailMessagingException("Recipients is null. Setup recipients using set method or configuration.");
    }

    public void setRecipients(List<String> recipientsList) {
        recipients = new ArrayList<EmailAddress>();
        for (String recipient : recipientsList) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                recipients.add(EmailAddress.of(recipient));
            }
        }
    }

    public Email recipients(String... recipientsSequence) {
        setRecipients(Arrays.asList(recipientsSequence));
        return this;
    }

    public List<EmailAddress> getCcRecipients() {

        if (ccRecipients == null) {
            String recipientsStr = getParam(CC_RECIPIENTS_CFG_NAME_TPL);
            if (StringUtils.isNotEmpty(recipientsStr)) {
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        ccRecipients.add(EmailAddress.of(recipient));
                    }
                }
            }
        }
        return ccRecipients != null ? ccRecipients : new ArrayList<>();
    }

    public void setCcRecipients(List<String> recipientsList) {
        ccRecipients = new ArrayList<EmailAddress>();
        for (String recipient : recipientsList) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                ccRecipients.add(EmailAddress.of(recipient));
            }
        }
    }

    public Email ccRecipients(String... recipientsSequence) {
        setCcRecipients(Arrays.asList(recipientsSequence));
        return this;
    }

    public List<EmailAddress> getBccRecipients() {

        if (bccRecipients == null) {
            String recipientsStr = getParam(BCC_RECIPIENTS_CFG_NAME_TPL);
            if (StringUtils.isNotEmpty(recipientsStr)) {
                for (String recipient : recipientsStr.split(";")) {
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        bccRecipients.add(EmailAddress.of(recipient));
                    }
                }
            }
        }
        return bccRecipients != null ? bccRecipients : new ArrayList<>();
    }

    public void setBccRecipients(List<String> recipientsList) {
        bccRecipients = new ArrayList<>();
        for (String recipient : recipientsList) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                bccRecipients.add(EmailAddress.of(recipient));
            }
        }
    }

    public Email bccRecipients(String... recipientsSequence) {
        setBccRecipients(Arrays.asList(recipientsSequence));
        return this;
    }

    public String getBody() {
        if (body == null) {
            String tpl = getParam(BODY_TEMPLATE_CFG_NAME_TPL);
            body = getFtlFileContent(tpl);
        }
        return body;
    }

    public void setBody(String body) {
        this.body = getFtlFileContent(body);
    }

    public Email body(String body) {
        setBody(body);
        return this;
    }

    public String getCharset() {

        if (charset == null) {
            charset = getParam(CHARSET_CFG_NAME_TPL);
        }
        return charset != null ? charset : "";
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Email charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Map<String, Object> getBodyProperties() {
        return bodyProperties;
    }

    public void setBodyProperties(Map<String, Object> bodyProperties) {
        this.bodyProperties = bodyProperties;
    }

    public Email property(String key, Object value) {
        bodyProperties.put(key, value);
        return this;
    }

    public HashMap<String, Object> getRoot() {
        return root;
    }

    public void setRoot(HashMap<String, Object> root) {
        this.root = root;
    }

    public Email root(HashMap<String, Object> root) {
        this.root = root;
        return this;
    }

    public void addAttachment(Path filePath) throws IOException {
        attachments.add(new EmailAttachment(filePath));
    }

    public void addAttachment(String fileName, InputStream fileContent) {
        attachments.add(new EmailAttachment(fileName, fileContent));
    }

    public void addAttachment(String fileName, byte[] fileContent) {
        attachments.add(new EmailAttachment(fileName, fileContent));
    }

    public void addAttachment(String fileName, InputStream fileContent, String mimeType) {
        attachments.add(new EmailAttachment(fileName, fileContent, mimeType));
    }

    public void addAttachment(String fileName, byte[] fileContent, String mimeType) {
        attachments.add(new EmailAttachment(fileName, fileContent, mimeType));
    }

    public void send() {
        send(null);
    }

    public void send(String to) {
        try {
            beforeSend();

            List<EmailAddress> recipients = new ArrayList<>();
            if (to != null) {
                recipients.add(EmailAddress.of(to));
            } else {
                recipients = getRecipients();
            }

            EmailAddress from = getSender();
            RPASecretCredentials credentials = getCredentials();
            if (from == null && credentials != null) {
                from = EmailAddress.of(credentials.getUser());
            }

            if (from == null) {
                throw new EmailMessagingException("Email sender or credentials should be defined. Setup them using set method or configuration.");
            }

            String senderName = getSenderName();
            if (senderName != null) {
                from = EmailAddress.of(from.getAddress(), senderName);
            }

            EmailMessage.EmailMessageBuilder emailBuilder = EmailMessage.EmailMessageBuilder.newMessage(from, recipients);
            emailBuilder.addCc(getCcRecipients());
            emailBuilder.addBcc(getBccRecipients());
            emailBuilder.subject(getSubject());

            String body = getBody();
            if (body == null) {
                throw new EmailMessagingException("Email body is not defined.");
            }

            FreeMarkerTemplate bodyTpl = new FreeMarkerTemplate(body, root);

            bodyTpl.put(getBodyProperties());
            emailBuilder.html(bodyTpl.compile());

            emailBuilder.charset(getCharset());

            emailBuilder.addAttachments(attachments);

            EmailMessage message = emailBuilder.build();

            String emailService = getEmailService();
            if (emailService == null) {
                throw new EmailMessagingException("Email service address is not defined.");
            }
            SMTPEmailSender.SupportedProtocol protocol = SMTPEmailSender.SupportedProtocol.forValue(getEmailServiceProtocol());
            String[] hostAndPort = emailService.split(":");
            String host = hostAndPort[0];
            String port = hostAndPort.length > 1 ? hostAndPort[1] : "";
            String user = credentials != null ? credentials.getUser() : "";
            String pass = credentials != null ? credentials.getPassword() : "";

            SMTPEmailSender service = new SMTPEmailSender(protocol, host, port, user, pass);
            service.sendEmail(message);

        } catch (EmailMessagingException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    protected void beforeSend() {
        // do some preparations here for subclasses
    }

    protected RPAServicesProvider getRpaServices() {
        return rpaServices;
    }

    private String getFtlFileContent(String ftlFilePath) {
        if (ftlFilePath != null && ftlFilePath.endsWith(".ftl")) {
            try {
                return IOUtils.toString(getClass().getResourceAsStream(ftlFilePath), "UTF-8");
            } catch (IOException e) {
                throw new EmailMessagingException("Template file not found", e);
            }
        } else
            return ftlFilePath != null ? ftlFilePath : "";
    }

    private String getParam(String template) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getParam(String.format(template, typeName));
        } catch (Exception e) {
            //do nothing
        }

        if (result == null && !DEFAULT_EMAIL_TYPE_NAME.equals(typeName)) {
            try {
                result = rpaServices.getParam(String.format(template, DEFAULT_EMAIL_TYPE_NAME));
            } catch (Exception e) {
                //do nothing
            }
        }
        return result;
    }
}
