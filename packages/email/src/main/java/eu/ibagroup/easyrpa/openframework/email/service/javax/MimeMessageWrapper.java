package eu.ibagroup.easyrpa.openframework.email.service.javax;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailBodyPart;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class MimeMessageWrapper extends EmailMessage {

    private static final String CHARSET_KEY = "charset=";
    private static final String MIME_MULTIPART = "multipart";
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String MIME_TEXT_HTML = "text/html";

    @JsonIgnore
    private MimeMessage mimeMessage;

    public MimeMessageWrapper(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    @Override
    public String getId() {
        if (id == null) {
            id = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    Folder folder = mimeMessage.getFolder();
                    return folder instanceof UIDFolder ? String.valueOf(((UIDFolder) folder).getUID(mimeMessage)) : null;
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return id;
    }

    @Override
    public Date getDate() {
        if (date == null) {
            date = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    Date messageDate = mimeMessage.getReceivedDate();
                    if (messageDate == null) {
                        messageDate = mimeMessage.getSentDate();
                    }
                    return messageDate;
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return date;
    }

    @Override
    public String getParentFolder() {
        if (parentFolder == null) {
            Folder folder = mimeMessage.getFolder();
            if (folder != null) {
                parentFolder = folder.getFullName();
            }
        }
        return parentFolder;
    }

    @Override
    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    Map<String, String> headers = new HashMap<>();
                    Enumeration<Header> xHeaders = mimeMessage.getAllHeaders();
                    while (xHeaders.hasMoreElements()) {
                        Header header = xHeaders.nextElement();
                        headers.put(header.getName(), header.getValue());
                    }
                    return headers;
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        throw new UnsupportedOperationException("Headers cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage header(String key, String value) {
        throw new UnsupportedOperationException("Headers cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailAddress getSender() {
        if (sender == null) {
            super.setSender(openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return convertToEmailAddress(mimeMessage.getSender());
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            }));
        }
        return sender;
    }

    @Override
    public void setSender(EmailAddress sender) {
        throw new UnsupportedOperationException("The field 'sender' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public void setSender(String senderAddress) {
        throw new UnsupportedOperationException("The field 'sender' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public String getSenderName() {
        if (senderName == null) {
            getSender();
        }
        return senderName;
    }

    @Override
    public void setSenderName(String senderName) {
        throw new UnsupportedOperationException("The field 'senderName' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailAddress getFrom() {
        if (from == null) {
            from = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    List<EmailAddress> fromAddress = convertToEmailAddresses(mimeMessage.getFrom());
                    return fromAddress.size() > 0 ? fromAddress.get(0) : null;
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return from;
    }

    @Override
    public void setFrom(EmailAddress from) {
        throw new UnsupportedOperationException("The field 'from' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public void setFrom(String fromAddress) {
        throw new UnsupportedOperationException("The field 'from' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public List<EmailAddress> getRecipients() {
        if (recipients == null) {
            recipients = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.TO));
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return recipients;
    }

    @Override
    public void setRecipients(List<EmailAddress> recipientsList) {
        throw new UnsupportedOperationException("The field 'recipients' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage recipients(String... recipientsSequence) {
        throw new UnsupportedOperationException("The field 'recipients' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public List<EmailAddress> getCcRecipients() {
        if (ccRecipients == null) {
            ccRecipients = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.CC));
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return ccRecipients;
    }

    @Override
    public void setCcRecipients(List<EmailAddress> recipientsList) {
        throw new UnsupportedOperationException("The field 'ccRecipients' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage ccRecipients(String... recipientsSequence) {
        throw new UnsupportedOperationException("The field 'ccRecipients' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public List<EmailAddress> getBccRecipients() {
        if (bccRecipients == null) {
            bccRecipients = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.BCC));
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return bccRecipients;
    }

    @Override
    public void setBccRecipients(List<EmailAddress> recipientsList) {
        throw new UnsupportedOperationException("The field 'bccRecipients' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage bccRecipients(String... recipientsSequence) {
        throw new UnsupportedOperationException("The field 'bccRecipients' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public List<EmailAddress> getReplyTo() {
        if (replyTo == null) {
            replyTo = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return convertToEmailAddresses(mimeMessage.getReplyTo());
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return replyTo;
    }

    @Override
    public void setReplyTo(List<EmailAddress> recipientsList) {
        throw new UnsupportedOperationException("The field 'replyTo' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage replyTo(String... recipientsSequence) {
        throw new UnsupportedOperationException("The field 'replyTo' cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public String getSubject() {
        if (subject == null) {
            subject = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return mimeMessage.getSubject();
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        throw new UnsupportedOperationException("Subject cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public String getCharset() {
        if (charset == null) {
            charset = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    String charset = extractCharset(mimeMessage);
                    return charset != null ? charset : StandardCharsets.UTF_8.name();
                } catch (MessagingException | IOException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        throw new UnsupportedOperationException("Body charset cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public List<EmailBodyPart> getBodyParts() {
        if (bodyParts == null) {
            bodyParts = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return extractBodyParts(mimeMessage);
                } catch (MessagingException | IOException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return bodyParts;
    }

    @Override
    public void setBodyParts(List<EmailBodyPart> bodyParts) {
        throw new UnsupportedOperationException("Body cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage text(String text) {
        throw new UnsupportedOperationException("Body cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public EmailMessage html(String html) {
        throw new UnsupportedOperationException("Body cannot be changed for retrieved from mailbox message.");
    }

    @Override
    public List<EmailAttachment> getAttachments() {
        if (attachments == null) {
            attachments = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return extractAttachments(mimeMessage);
                } catch (MessagingException | IOException e) {
                    throw new EmailMessagingException(e);
                }
            });
        }
        return attachments;
    }

    @Override
    public void setAttachments(List<EmailAttachment> attachments) {
        throw new UnsupportedOperationException("New files cannot be attached to retrieved from mailbox message.");
    }

    @Override
    public EmailMessage attach(File file) {
        throw new UnsupportedOperationException("New files cannot be attached to retrieved from mailbox message.");
    }

    @Override
    public EmailMessage attach(Path filePath) {
        throw new UnsupportedOperationException("New files cannot be attached to retrieved from mailbox message.");
    }

    @Override
    public EmailMessage attach(String fileName, InputStream fileContent, String mimeType) {
        throw new UnsupportedOperationException("New files cannot be attached to retrieved from mailbox message.");
    }

    @Override
    public boolean isRead() {
        if (isRead == null) {
            isRead = openFolderAndPerform(Folder.READ_ONLY, () -> {
                try {
                    return mimeMessage.getFlags().contains(Flags.Flag.SEEN);
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
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
            isRead = openFolderAndPerform(Folder.READ_WRITE, () -> {
                try {
                    mimeMessage.setFlag(Flags.Flag.SEEN, read);
                    return read;
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
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

    private List<EmailBodyPart> extractBodyParts(Part part) throws MessagingException, IOException {
        List<EmailBodyPart> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(extractBodyParts(childPart));
            }
        } else if (part.getContent() instanceof Part) {
            result.addAll(this.extractBodyParts((Part) part.getContent()));

        } else if (mimeType.contains(MIME_TEXT_PLAIN)) {
            Object content = part.getContent();
            if (content instanceof String) {
                result.add(new EmailBodyPart((String) content, EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN));
            }

        } else if (mimeType.contains(MIME_TEXT_HTML)) {
            Object content = part.getContent();
            if (content instanceof String) {
                result.add(new EmailBodyPart((String) content, EmailBodyPart.CONTENT_TYPE_TEXT_HTML));
            }
        }

        return result;
    }

    private List<EmailAttachment> extractAttachments(Part part) throws MessagingException, IOException {
        List<EmailAttachment> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(extractAttachments(childPart));
            }

        } else if (!mimeType.contains(MIME_TEXT_PLAIN)
                && !mimeType.contains(MIME_TEXT_HTML)
                && (Part.ATTACHMENT.equalsIgnoreCase(disposition) || Part.INLINE.equalsIgnoreCase(disposition))) {

            if (mimeType.contains(";")) {
                mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            }
            result.add(new EmailAttachment(part.getFileName(), part.getInputStream(), mimeType));

        } else if (part.getContent() instanceof Part) {
            result.addAll(extractAttachments((Part) part.getContent()));
        }

        return result;
    }

    private String extractCharset(Part part) throws MessagingException, IOException {
        String charset = null;
        String mimeType = part.getContentType().toLowerCase();

        if (mimeType.contains(CHARSET_KEY)) {
            int endOfCharsetSubstring = mimeType.indexOf(";", mimeType.indexOf(CHARSET_KEY));
            if (endOfCharsetSubstring >= 0) {
                charset = mimeType.substring(mimeType.indexOf(CHARSET_KEY) + CHARSET_KEY.length(), endOfCharsetSubstring).trim().toUpperCase();
            } else {
                charset = mimeType.substring(mimeType.indexOf(CHARSET_KEY) + CHARSET_KEY.length()).trim().toUpperCase();
            }

        } else if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                charset = extractCharset(childPart);
                if (charset != null) {
                    break;
                }
            }

        } else if (part.getContent() instanceof Part) {
            charset = extractCharset((Part) part.getContent());
        }

        return charset;
    }

    private <T> T openFolderAndPerform(int mode, Supplier<T> action) {
        try {
            Folder folder = mimeMessage.getFolder();
            if (folder.isOpen() && folder.getMode() >= mode) {
                return action.get();
            } else {
                boolean folderWasOpen = false;
                if (folder.isOpen()) {
                    folderWasOpen = true;
                    folder.close(false);
                }
                folder.open(mode);
                try {
                    return action.get();
                } finally {
                    if (!folderWasOpen) {
                        folder.close(false);
                    }
                }
            }
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }
}

