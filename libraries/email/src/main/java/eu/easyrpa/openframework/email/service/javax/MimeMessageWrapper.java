package eu.easyrpa.openframework.email.service.javax;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.EmailSender;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.message.EmailAddress;
import eu.easyrpa.openframework.email.message.EmailAttachment;
import eu.easyrpa.openframework.email.message.EmailBodyPart;
import eu.easyrpa.openframework.email.message.tnef.TNEFMailMessageConverter;
import net.freeutils.tnef.TNEFInputStream;
import net.freeutils.tnef.TNEFUtils;
import net.freeutils.tnef.mime.ContactConverter;
import net.freeutils.tnef.mime.ReadReceiptConverter;
import net.freeutils.tnef.mime.TNEFMime;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Special implementation of {@link EmailMessage} that wraps related {@link MimeMessage} and provides read-only
 * access to its data and parameters.
 */
public class MimeMessageWrapper extends EmailMessage {

    @JsonIgnore
    private MimeMessage mimeMessage;

    public MimeMessageWrapper(MimeMessage message) {
        try {
            Folder folder = message.getFolder();
            if (folder instanceof UIDFolder) {
                id = String.valueOf(((UIDFolder) folder).getUID(message));
                parentFolder = folder.getFullName();
            }
            this.mimeMessage = new MimeMessage(message);

            List<EmailAttachment> attachments = MimeMessageConverter.extractAttachments(this.mimeMessage);
            if (attachments.size() == 1 && TNEFUtils.isTNEFMimeType(attachments.get(0).getMimeType())) {
                TNEFMime.setConverters(new ContactConverter(), new ReadReceiptConverter(), new TNEFMailMessageConverter());
                this.mimeMessage = TNEFMime.convert(message.getSession(),
                        new TNEFInputStream(attachments.get(0).getInputStream()));
            }
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public Date getDate() {
        if (date == null) {
            try {
                date = mimeMessage.getReceivedDate();
                if (date == null) {
                    date = mimeMessage.getSentDate();
                }
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return date;
    }

    @Override
    public Map<String, String> getHeaders() {
        if (headers == null) {
            try {
                headers = new HashMap<>();
                Enumeration<Header> xHeaders = mimeMessage.getAllHeaders();
                while (xHeaders.hasMoreElements()) {
                    Header header = xHeaders.nextElement();
                    headers.put(header.getName(), header.getValue());
                }
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        try {
            Enumeration<Header> existingHeaders = mimeMessage.getAllHeaders();
            while (existingHeaders.hasMoreElements()) {
                mimeMessage.removeHeader(existingHeaders.nextElement().getName());
            }
            for (Map.Entry<String, String> header : headers.entrySet()) {
                mimeMessage.setHeader(header.getKey(), header.getValue());
            }
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage header(String key, String value) {
        try {
            mimeMessage.setHeader(key, value);
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public EmailAddress getSender() {
        if (sender == null) {
            try {
                sender = convertToEmailAddress(mimeMessage.getSender());
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return sender;
    }

    @Override
    public void setSender(EmailAddress sender) {
        try {
            mimeMessage.setSender(convertToInternetAddress(sender));
            this.sender = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public void setSender(String senderAddress) {
        try {
            mimeMessage.setSender(parseInternetAddress(senderAddress));
            this.sender = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public String getSenderName() {
        return getSender().getPersonal();
    }

    @Override
    public void setSenderName(String senderName) {
        setSender(new EmailAddress(getSender().getAddress(), senderName));
    }

    @Override
    public EmailAddress getFrom() {
        if (from == null) {
            try {
                List<EmailAddress> fromAddress = convertToEmailAddresses(mimeMessage.getFrom());
                from = fromAddress.size() > 0 ? fromAddress.get(0) : null;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return from;
    }

    @Override
    public void setFrom(EmailAddress from) {
        try {
            mimeMessage.setFrom(convertToInternetAddress(from));
            this.from = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public void setFrom(String fromAddress) {
        try {
            mimeMessage.setFrom(parseInternetAddress(fromAddress));
            this.from = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public List<EmailAddress> getRecipients() {
        if (recipients == null) {
            try {
                recipients = convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.TO));
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return recipients;
    }

    @Override
    public void setRecipients(List<EmailAddress> recipientsList) {
        try {
            mimeMessage.setRecipients(Message.RecipientType.TO, recipientsList.stream().map(emailAddress -> {
                try {
                    return convertToInternetAddress(emailAddress);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.recipients = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage recipients(String... recipientsSequence) {
        try {
            mimeMessage.setRecipients(Message.RecipientType.TO, Arrays.stream(recipientsSequence).map(address -> {
                try {
                    return parseInternetAddress(address);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.recipients = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public List<EmailAddress> getCcRecipients() {
        if (ccRecipients == null) {
            try {
                ccRecipients = convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.CC));
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return ccRecipients;
    }

    @Override
    public void setCcRecipients(List<EmailAddress> recipientsList) {
        try {
            mimeMessage.setRecipients(Message.RecipientType.CC, recipientsList.stream().map(emailAddress -> {
                try {
                    return convertToInternetAddress(emailAddress);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.ccRecipients = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage ccRecipients(String... recipientsSequence) {
        try {
            mimeMessage.setRecipients(Message.RecipientType.CC, Arrays.stream(recipientsSequence).map(address -> {
                try {
                    return parseInternetAddress(address);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.ccRecipients = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public List<EmailAddress> getBccRecipients() {
        if (bccRecipients == null) {
            try {
                bccRecipients = convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.BCC));
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return bccRecipients;
    }

    @Override
    public void setBccRecipients(List<EmailAddress> recipientsList) {
        try {
            mimeMessage.setRecipients(Message.RecipientType.BCC, recipientsList.stream().map(emailAddress -> {
                try {
                    return convertToInternetAddress(emailAddress);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.bccRecipients = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage bccRecipients(String... recipientsSequence) {
        try {
            mimeMessage.setRecipients(Message.RecipientType.BCC, Arrays.stream(recipientsSequence).map(address -> {
                try {
                    return parseInternetAddress(address);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.bccRecipients = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public List<EmailAddress> getReplyTo() {
        if (replyTo == null) {
            try {
                replyTo = convertToEmailAddresses(mimeMessage.getReplyTo());
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return replyTo;
    }

    @Override
    public void setReplyTo(List<EmailAddress> recipientsList) {
        try {
            mimeMessage.setReplyTo(recipientsList.stream().map(emailAddress -> {
                try {
                    return convertToInternetAddress(emailAddress);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.replyTo = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage replyTo(String... recipientsSequence) {
        try {
            mimeMessage.setReplyTo(Arrays.stream(recipientsSequence).map(address -> {
                try {
                    return parseInternetAddress(address);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));
            this.replyTo = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public String getSubject() {
        if (subject == null) {
            try {
                subject = mimeMessage.getSubject();
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        try {
            mimeMessage.setSubject(subject, getCharset());
            this.subject = null;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public String getCharset() {
        if (charset == null) {
            try {
                charset = MimeMessageConverter.extractCharset(mimeMessage);
                if (charset == null) {
                    charset = StandardCharsets.UTF_8.name();
                }
            } catch (MessagingException | IOException e) {
                throw new EmailMessagingException(e);
            }
        }
        return charset;
    }

    @Override
    public List<EmailBodyPart> getBodyParts() {
        if (bodyParts == null) {
            try {
                bodyParts = MimeMessageConverter.extractBodyParts(mimeMessage);
            } catch (MessagingException | IOException e) {
                throw new EmailMessagingException(e);
            }
        }
        return bodyParts;
    }

    @Override
    public void setBodyParts(List<EmailBodyPart> bodyParts) {
        try {
            String charset = getCharset();
            for (EmailBodyPart bodyPart : bodyParts) {
                MimeMessageConverter.updateBodyPart(mimeMessage, bodyPart, charset);
            }
            this.bodyParts = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage text(String text) {
        try {
            EmailBodyPart bodyPart = new EmailBodyPart(text, EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN);
            MimeMessageConverter.updateBodyPart(mimeMessage, bodyPart, getCharset());
            this.bodyParts = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public EmailMessage html(String html) {
        try {
            EmailBodyPart bodyPart = new EmailBodyPart(html, EmailBodyPart.CONTENT_TYPE_TEXT_HTML);
            MimeMessageConverter.updateBodyPart(mimeMessage, bodyPart, getCharset());
            this.bodyParts = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public List<EmailAttachment> getAttachments() {
        if (attachments == null) {
            try {
                attachments = MimeMessageConverter.extractAttachments(mimeMessage);
            } catch (MessagingException | IOException e) {
                throw new EmailMessagingException(e);
            }
        }
        return attachments;
    }

    @Override
    public void setAttachments(List<EmailAttachment> attachments) {
        try {
            MimeMessageConverter.removeAttachments(mimeMessage);
            for (EmailAttachment attachment : attachments) {
                MimeMessageConverter.addAttachment(mimeMessage, attachment);
            }
            this.attachments = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage attach(File file) {
        try {
            MimeMessageConverter.addAttachment(mimeMessage, new EmailAttachment(file.toPath()));
            attachments = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public EmailMessage attach(Path filePath) {
        try {
            MimeMessageConverter.addAttachment(mimeMessage, new EmailAttachment(filePath));
            attachments = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public EmailMessage attach(String fileName, InputStream fileContent, String mimeType) {
        try {
            MimeMessageConverter.addAttachment(mimeMessage, new EmailAttachment(fileName, fileContent, mimeType));
            attachments = null;
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
        return this;
    }

    @Override
    public boolean isRead() {
        if (isRead == null) {
            try {
                isRead = mimeMessage.getFlags().contains(Flags.Flag.SEEN);
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
        return super.isRead();
    }

    @Override
    public boolean isUnread() {
        return !isRead();
    }

    @Override
    public void setRead(boolean read) {
        if (isRead() != read) {
            try {
                mimeMessage.setFlag(Flags.Flag.SEEN, read);
                isRead = read;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        }
    }

    @Override
    public void send() {
        throw new UnsupportedOperationException("Retrieved from mailbox message cannot be resend.");
    }

    @Override
    public void send(EmailSender emailSender) {
        throw new UnsupportedOperationException("Retrieved from mailbox message cannot be resend.");
    }

    MimeMessage getMimeMessage() {
        return mimeMessage;
    }

    private List<EmailAddress> convertToEmailAddresses(Address[] addresses) {
        List<EmailAddress> emailAddresses = new ArrayList<>();
        if (addresses != null) {
            for (Address address : addresses) {
                EmailAddress emailAddress = convertToEmailAddress(address);
                if (emailAddress != null) {
                    emailAddresses.add(emailAddress);
                }
            }
        }
        return emailAddresses;
    }

    private EmailAddress convertToEmailAddress(Address address) {
        return address != null ? new EmailAddress(address.toString()) : null;
    }

    private InternetAddress convertToInternetAddress(EmailAddress address) throws MessagingException {
        try {
            if (address != null) {
                return new InternetAddress(address.getAddress(), address.getPersonal(), getCharset());
            }
            return null;
        } catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Failed to parse embedded personal name to correct encoding", ex);
        }
    }

    private InternetAddress parseInternetAddress(String address) throws MessagingException {
        InternetAddress[] parsed = InternetAddress.parse(address);
        if (parsed.length != 1) {
            throw new IllegalArgumentException(String.format("Illegal email address '%s'.", address));
        }
        InternetAddress raw = parsed[0];
        try {
            return new InternetAddress(raw.getAddress(), raw.getPersonal(), getCharset());
        } catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Failed to parse embedded personal name to correct encoding", ex);
        }
    }
}

