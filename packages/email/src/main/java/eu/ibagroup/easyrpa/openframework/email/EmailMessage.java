package eu.ibagroup.easyrpa.openframework.email;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailBodyPart;
import eu.ibagroup.easyrpa.openframework.email.service.EmailConfigParam;
import eu.ibagroup.easyrpa.openframework.email.utils.EmailUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EmailMessage {

    public static final String USED_DATE_TME_FORMAT_PATTERN = "MM/dd/yyyy hh:mm:ss a";

    private static final String DEFAULT_EMAIL_TYPE_NAME = "email";

    protected String typeName = DEFAULT_EMAIL_TYPE_NAME;

    protected String id;

    @JsonFormat(pattern = USED_DATE_TME_FORMAT_PATTERN)
    protected Date date;

    protected String parentFolder;

    protected Map<String, String> headers;

    protected EmailAddress sender;

    @JsonIgnore
    protected String senderName;

    protected EmailAddress from;

    protected List<EmailAddress> recipients;

    protected List<EmailAddress> ccRecipients;

    protected List<EmailAddress> bccRecipients;

    protected List<EmailAddress> replyTo;

    protected String subject;

    protected String charset;

    @JsonIgnore
    protected String text;

    @JsonIgnore
    protected String html;

    protected List<EmailBodyPart> bodyParts;

    protected Map<String, Object> bodyProperties = new HashMap<>();

    protected List<EmailAttachment> attachments;

    protected Boolean isRead;

    @JsonIgnore
    protected EmailMessage forwardedMessage;

    @JsonIgnore
    protected EmailMessage replyOnMessage;

    @JsonIgnore
    protected EmailSender emailSender;

    public EmailMessage() {
    }

    public EmailMessage(String typeName) {
        this.typeName = typeName;
    }

    @Inject
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

    public Date getDate() {
        return date;
    }

    @JsonIgnore
    public ZonedDateTime getDateTime() {
        return getDate() != null ? ZonedDateTime.ofInstant(getDate().toInstant(), ZoneId.systemDefault()) : null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>();
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    public EmailMessage header(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
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

    @JsonSetter
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

    @JsonSetter
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
        setCharset(charset);
        return this;
    }

    public List<EmailBodyPart> getBodyParts() {
        if (bodyParts == null) {
            bodyParts = new ArrayList<>();
            String tpl = getConfigParam(EmailConfigParam.BODY_TEMPLATE_TPL);
            if (tpl != null) {
                bodyParts.add(new EmailBodyPart(tpl, EmailBodyPart.CONTENT_TYPE_TEXT_HTML));
            }
            this.text = null;
            this.html = null;
        }
        return bodyParts;
    }

    public void setBodyParts(List<EmailBodyPart> bodyParts) {
        this.bodyParts = new ArrayList<>();
        if (bodyParts != null) {
            this.bodyParts.addAll(bodyParts);
        }
        this.text = null;
        this.html = null;
    }

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

    public EmailMessage addText(String text) {
        if (text != null) {
            getBodyParts().add(new EmailBodyPart(text, EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN));
            this.text = null;
            this.html = null;
        }
        return this;
    }

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

    public EmailMessage addHtml(String html) {
        if (html != null) {
            getBodyParts().add(new EmailBodyPart(html, EmailBodyPart.CONTENT_TYPE_TEXT_HTML));
            this.text = null;
            this.html = null;
        }
        return this;
    }

    public boolean hasHtml() {
        return getBodyParts().stream().anyMatch(EmailBodyPart::isHtml);
    }

    public boolean hasText() {
        return getBodyParts().stream().anyMatch(EmailBodyPart::isText);
    }

    public String getText() {
        if (text == null) {
            if (hasText()) {
                text = getBodyParts().stream()
                        .map(part -> part.isText() ? part.getContent(bodyProperties) : null)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("\n")).trim();
            } else if (hasHtml()) {
                text = EmailUtils.htmlToText(getHtml());
            }
        }
        return text;
    }

    public String getHtml() {
        if (html == null) {
            List<String> parts = new ArrayList<>();
            if (hasHtml()) {
                parts = getBodyParts().stream()
                        .map(part -> part.isHtml() ? part.getContent(bodyProperties) : null)
                        .filter(Objects::nonNull).collect(Collectors.toList());
            } else if (hasText()) {
                parts = getBodyParts().stream()
                        .map(part -> part.isText() ? part.getContent(bodyProperties) : null)
                        .filter(Objects::nonNull).collect(Collectors.toList());
            }

            String head = parts.stream().map(p -> {
                if (p.contains("<head>")) {
                    return StringUtils.substringBetween(p, "<head>", "</head>");
                }
                if (p.contains("<HEAD>")) {
                    return StringUtils.substringBetween(p, "<HEAD>", "</HEAD>");
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.joining("\n"));
            String body = parts.stream().map(p -> {
                if (p.contains("<body>")) {
                    return StringUtils.substringBetween(p, "<body>", "</body>");
                }
                if (p.contains("<BODY>")) {
                    return StringUtils.substringBetween(p, "<BODY>", "</BODY>");
                }
                return p.startsWith("<") ? p : String.format("<div>%s</div>", p);
            }).collect(Collectors.joining("\n"));

            if (head.trim().length() > 0) {
                html = String.format("<html><head>\n%s\n</head><body>\n%s\n</body></html>", head, body);
            } else if (body.trim().length() > 0) {
                html = String.format("<html><body>\n%s\n</body></html>", body);
            } else {
                html = "";
            }
        }
        return html;
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

    public List<EmailAttachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }

    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = new ArrayList<>();
        this.attachments.addAll(attachments);
    }

    public EmailMessage attach(File file) throws IOException {
        getAttachments().add(new EmailAttachment(file.toPath()));
        return this;
    }

    public EmailMessage attach(Path filePath) throws IOException {
        getAttachments().add(new EmailAttachment(filePath));
        return this;
    }

    public EmailMessage attach(String fileName, InputStream fileContent, String mimeType) {
        getAttachments().add(new EmailAttachment(fileName, fileContent, mimeType));
        return this;
    }

    public boolean hasAttachments() {
        List<EmailAttachment> attachments = getAttachments();
        return attachments != null && !attachments.isEmpty();
    }

    public boolean isRead() {
        return isRead != null && isRead;
    }

    @JsonIgnore
    public boolean isUnread() {
        return isRead != null && !isRead;
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

    public EmailMessage getForwardedMessage() {
        return forwardedMessage;
    }

    public EmailMessage getReplyOnMessage() {
        return replyOnMessage;
    }

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

    @Override
    public String toString() {
        return "EmailMessage{" +
                "id='" + id + '\'' +
                ", senderName='" + senderName + '\'' +
                ", subject='" + subject + '\'' +
                ", bodyParts=" + bodyParts +
                '}';
    }

    public String toJson(boolean isPrettyPrint) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return isPrettyPrint ? om.writerWithDefaultPrettyPrinter().writeValueAsString(this) : om.writeValueAsString(this);
    }

    public static EmailMessage fromJson(String json) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, EmailMessage.class);
    }

    protected void beforeSend() {
        // do some preparations here for subclasses
    }

    protected String getConfigParam(String template) {
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
