package eu.ibagroup.easyrpa.openframework.email;

import eu.ibagroup.easyrpa.openframework.email.message.*;
import eu.ibagroup.easyrpa.openframework.email.message.templates.FreeMarkerTemplate;
import eu.ibagroup.easyrpa.openframework.email.service.EmailConfigParam;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EmailMessage {

    private static final String DEFAULT_EMAIL_TYPE_NAME = "email";

    private String typeName = DEFAULT_EMAIL_TYPE_NAME;

    private String id;

    private ZonedDateTime date;

    private String parentFolder;

    private String subject;

    private EmailAddress sender;

    private String senderName;

    private EmailAddress from;

    private List<EmailAddress> recipients;

    private List<EmailAddress> ccRecipients;

    private List<EmailAddress> bccRecipients;

    private List<EmailAddress> replyTo;

    private String charset;

    private List<EmailBodyPart> bodyParts = new ArrayList<>();

    private Map<String, Object> bodyProperties = new HashMap<>();

    private List<EmailAttachment> attachments = new ArrayList<>();

    private boolean isRead;

    private EmailMessage previousMessage;

    @Inject
    private EmailSender emailSender;

    public EmailMessage() {
    }

    public EmailMessage(String id, String parentFolder, ZonedDateTime date) {
        this.id = id;
        this.parentFolder = parentFolder;
        this.date = date;
    }

    public EmailMessage(String typeName) {
        this.typeName = typeName;
    }

    public EmailMessage(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public EmailMessage(String typeName, EmailSender emailSender) {
        this.typeName = typeName;
        this.emailSender = emailSender;
    }

    public String getId() {
        return this.id;
    }

    public String getParentFolder() {
        return this.parentFolder;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getSubject() {
        if (subject == null) {
            subject = getConfigParam(EmailConfigParam.SUBJECT_TPL);
        }
        return subject != null ? subject : "";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailMessage subject(String subject) {
        setSubject(subject);
        return this;
    }

    public EmailAddress getSender() {
        if (sender == null) {
            String senderAddress = getConfigParam(EmailConfigParam.SENDER_TPL);
            sender = senderAddress != null ? new EmailAddress(senderAddress, getSenderName()) : null;
        }
        return sender;
    }

    public void setSender(String senderAddress) {
        this.sender = senderAddress != null ? new EmailAddress(senderAddress, getSenderName()) : null;
    }

    public void setSender(EmailAddress sender) {
        this.sender = sender;
        this.senderName = sender != null ? sender.getPersonal() : null;
    }

    public EmailMessage sender(String senderAddress) {
        setSender(senderAddress);
        return this;
    }

    public String getSenderName() {
        if (senderName == null) {
            senderName = getConfigParam(EmailConfigParam.SENDER_NAME_TPL);
        }
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
        if (this.sender != null) {
            this.sender = new EmailAddress(this.sender.getAddress(), this.senderName);
        }
    }

    public EmailMessage senderName(String senderName) {
        setSenderName(senderName);
        return this;
    }

    public EmailAddress getFrom() {
        if (from == null) {
            String fromAddress = getConfigParam(EmailConfigParam.FROM_TPL);
            from = fromAddress != null ? new EmailAddress(fromAddress) : null;
        }
        return from;
    }

    public void setFrom(String fromAddress) {
        this.from = fromAddress != null ? new EmailAddress(fromAddress) : null;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public EmailMessage from(String fromAddress) {
        setFrom(fromAddress);
        return this;
    }

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

    public void setRecipients(List<EmailAddress> recipientsList) {
        recipients = new ArrayList<>();
        recipients.addAll(recipientsList);
    }

    public EmailMessage recipients(String... recipientsSequence) {
        recipients = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                recipients.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

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

    public void setCcRecipients(List<EmailAddress> recipientsList) {
        ccRecipients = new ArrayList<>();
        ccRecipients.addAll(recipientsList);
    }

    public EmailMessage ccRecipients(String... recipientsSequence) {
        ccRecipients = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                ccRecipients.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

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

    public void setBccRecipients(List<EmailAddress> recipientsList) {
        bccRecipients = new ArrayList<>();
        bccRecipients.addAll(recipientsList);
    }

    public EmailMessage bccRecipients(String... recipientsSequence) {
        bccRecipients = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                bccRecipients.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

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

    public void setReplyTo(List<EmailAddress> recipientsList) {
        replyTo = new ArrayList<>();
        replyTo.addAll(recipientsList);
    }

    public EmailMessage replyTo(String... recipientsSequence) {
        replyTo = new ArrayList<>();
        for (String recipient : recipientsSequence) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                replyTo.add(new EmailAddress(recipient));
            }
        }
        return this;
    }

    public String getCharset() {
        if (charset == null) {
            charset = getConfigParam(EmailConfigParam.CHARSET_TPL);
        }
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public EmailMessage charset(String charset) {
        this.charset = charset;
        return this;
    }

    public List<EmailBodyPart> getBodyParts() {
        if (bodyParts == null) {
            String tpl = getConfigParam(EmailConfigParam.BODY_TEMPLATE_TPL);
            if (tpl != null) {
                bodyParts.add(tpl.endsWith(".ftl") ? new EmailBodyTemplate(tpl) : new EmailBodyText(tpl));
            }
        }
        return bodyParts;
    }

    public void setBodyParts(List<EmailBodyPart> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public EmailMessage body(String body) {
        if (body != null) {
            bodyParts.add(body.endsWith(".ftl") ? new EmailBodyTemplate(body) : new EmailBodyText(body));
        }
        return this;
    }

    public String getBody() {
        return this.bodyParts.stream().map(part -> {
            if (part instanceof EmailBodyText) {
                return ((EmailBodyText) part).getContent();
            }
            if (part instanceof EmailBodyTemplate) {
                try {
                    return new FreeMarkerTemplate(((EmailBodyTemplate) part).getTemplateText(), bodyProperties).compile();
                } catch (Exception e) {
                    return String.format("[ftl_compile_error \"%s\"]", e.getMessage());
                }
            }
            if (part instanceof EmailAttachment) {
                return String.format("[attachment \"%s\"]", ((EmailAttachment) part).getFileName());
            }
            return "";
        }).collect(Collectors.joining("\n"));
    }

    public Map<String, Object> getBodyProperties() {
        return bodyProperties;
    }

    public void setBodyProperties(Map<String, Object> bodyProperties) {
        this.bodyProperties = bodyProperties;
    }

    public EmailMessage property(String key, Object value) {
        bodyProperties.put(key, value);
        return this;
    }

    public EmailMessage inline(File file) throws IOException {
        bodyParts.add(new EmailAttachment(file.toPath()));
        return this;
    }

    public EmailMessage inline(Path filePath) throws IOException {
        bodyParts.add(new EmailAttachment(filePath));
        return this;
    }

    public EmailMessage inline(InputStream fileContent, String mimeType) {
        bodyParts.add(new EmailAttachment(UUID.randomUUID().toString(), fileContent, mimeType));
        return this;
    }

    public EmailMessage inline(byte[] fileContent, String mimeType) {
        bodyParts.add(new EmailAttachment(UUID.randomUUID().toString(), fileContent, mimeType));
        return this;
    }

    public EmailMessage inline(String fileName, InputStream fileContent, String mimeType) {
        bodyParts.add(new EmailAttachment(fileName, fileContent, mimeType));
        return this;
    }

    public EmailMessage inline(String fileName, byte[] fileContent, String mimeType) {
        bodyParts.add(new EmailAttachment(fileName, fileContent, mimeType));
        return this;
    }

    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
    }

    public EmailMessage attach(File file) throws IOException {
        attachments.add(new EmailAttachment(file.toPath()));
        return this;
    }

    public EmailMessage attach(Path filePath) throws IOException {
        attachments.add(new EmailAttachment(filePath));
        return this;
    }

    public EmailMessage attach(String fileName, InputStream fileContent, String mimeType) {
        attachments.add(new EmailAttachment(fileName, fileContent, mimeType));
        return this;
    }

    public EmailMessage attach(String fileName, byte[] fileContent, String mimeType) {
        attachments.add(new EmailAttachment(fileName, fileContent, mimeType));
        return this;
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isUnread() {
        return !isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public EmailMessage markRead() {
        setRead(true);
        return this;
    }

    public EmailMessage markUnread() {
        setRead(false);
        return this;
    }

    public void setPreviousMessage(EmailMessage previousMessage) {
        this.previousMessage = previousMessage;
    }

    public EmailMessage getPreviousMessage() {
        return previousMessage;
    }

    public EmailMessage forwardMessage() {
        EmailMessage msg = new EmailMessage();
        msg.subject = "Fwd: " + subject;
        msg.charset = charset;
        msg.previousMessage = this;
        return msg;
    }

    public EmailMessage replyMessage() {
        EmailMessage msg = new EmailMessage();
        msg.subject = "Re: " + getSubject();
        msg.charset = charset;
        msg.recipients = new ArrayList<>();
        msg.recipients.add(getSender());
        msg.previousMessage = this;
        return msg;
    }

    public EmailMessage replyAllMessage() {
        EmailMessage msg = replyMessage();
        msg.setCcRecipients(getCcRecipients());
        return msg;
    }

    public void send() {
        if (emailSender != null) {
            emailSender.sendMessage(this);
        }
    }

    public void send(EmailSender emailSender) {
        if (emailSender != null) {
            this.emailSender = emailSender;
            this.emailSender.sendMessage(this);
        }
    }

    protected void beforeSend() {
        // do some preparations here for subclasses
    }

    private String getConfigParam(String template) {
        String result;

        if (emailSender == null) {
            return null;
        }

        result = emailSender.getConfigParam(String.format(template, typeName));

        if (result == null && !DEFAULT_EMAIL_TYPE_NAME.equals(typeName)) {
            emailSender.getConfigParam(String.format(template, DEFAULT_EMAIL_TYPE_NAME));
        }

        return result;
    }
}
