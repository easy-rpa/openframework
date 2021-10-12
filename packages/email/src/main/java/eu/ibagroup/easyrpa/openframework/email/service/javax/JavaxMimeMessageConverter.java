package eu.ibagroup.easyrpa.openframework.email.service.javax;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailFlag;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;
import eu.ibagroup.easyrpa.openframework.email.utils.EmailUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.UIDFolder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JavaxMimeMessageConverter extends MessageConverter<Message> {
    private static final String MIME_MULTIPART = "multipart";

    private static final String MIME_TEXT_PLAIN = "text/plain";

    private static final String MIME_TEXT_HTML = "text/html";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    private static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String HEADER_CONTENT_ID = "Content-ID";

    private static final Logger logger = LoggerFactory.getLogger(JavaxMimeMessageConverter.class);

    private final Session session;

    public JavaxMimeMessageConverter(Session session) {
        this.session = session;
    }

    public Message createNativeMessage(EmailMessage emailMessage) {
        MimeMessage message = new MimeMessage(this.session);

        try {
            message.setFrom(new InternetAddress(emailMessage.getFrom().toString()));
            Iterator<EmailAddress> addressIterator = emailMessage.getTo().iterator();

            EmailAddress bcc;
            while (addressIterator.hasNext()) {
                bcc = addressIterator.next();
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(bcc.toString()));
            }

            addressIterator = emailMessage.getCc().iterator();

            while (addressIterator.hasNext()) {
                bcc = addressIterator.next();
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(bcc.toString()));
            }

            addressIterator = emailMessage.getBcc().iterator();

            while (addressIterator.hasNext()) {
                bcc = addressIterator.next();
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc.toString()));
            }

            message.setReplyTo((Address[]) emailMessage.getReplyTo().stream().map((addr) -> {
                try {
                    return new InternetAddress(addr.toString());
                } catch (AddressException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray((x$0) -> {
                return new InternetAddress[x$0];
            }));
            message.setSubject(emailMessage.getSubject());
            if (emailMessage.getDate().isPresent()) {
                ZonedDateTime date = (ZonedDateTime) emailMessage.getDate().get();
                message.setSentDate(Date.from(date.toInstant()));
            }

            Multipart contentRoot = new MimeMultipart("alternative");
            String charset = emailMessage.getCharset();
            String charsetString = "; charset=" + (StringUtils.isEmpty(charset) ? DEFAULT_CHARSET : charset);
            BodyPart bodyText = new MimeBodyPart();
            bodyText.setContent(emailMessage.getText(), MIME_TEXT_PLAIN + charsetString);
            contentRoot.addBodyPart(bodyText);
            Optional<String> bodyHtmlContent = emailMessage.getHtml();
            if (bodyHtmlContent.isPresent()) {
                BodyPart bodyHtml = new MimeBodyPart();
                bodyHtml.setContent(bodyHtmlContent.get(), MIME_TEXT_HTML + charsetString);
                contentRoot.addBodyPart(bodyHtml);
            }

            List<EmailAttachment> files = emailMessage.getInlineFiles();
            Iterator<EmailAttachment> filesIterator = files.iterator();

            EmailAttachment attachment;
            MimeBodyPart attachmentFile;
            while (filesIterator.hasNext()) {
                attachment = filesIterator.next();
                attachmentFile = this.createFilePart(attachment, "inline");
                contentRoot.addBodyPart(attachmentFile);
            }

            filesIterator = emailMessage.getAttachments().iterator();

            while (filesIterator.hasNext()) {
                attachment = filesIterator.next();
                attachmentFile = this.createFilePart(attachment, "attachment");
                contentRoot.addBodyPart(attachmentFile);
            }

            message.setContent(contentRoot);
            message.saveChanges();

            return message;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    public EmailMessage convertToEmailMessage(Message nativeMessage) {
        EmailAddress from = this.extractFrom(nativeMessage);
        List<EmailAddress> to = this.extractTo(nativeMessage);
        EmailMessage.EmailMessageBuilder builder = EmailMessage.EmailMessageBuilder.newMessage(from, to);
        builder.id(this.extractMessageId(nativeMessage)).addCc(this.extractCc(nativeMessage)).addBcc(this.extractBcc(nativeMessage)).addReplyTo(this.extractReplyTo(nativeMessage)).date(this.extractDate(nativeMessage)).subject(this.extractSubject(nativeMessage)).text(this.extractText(nativeMessage))
                .html(this.extractHtml(nativeMessage)).inlineFiles(this.extractInlineFiles(nativeMessage)).parentFolder(this.extractParentFolder(nativeMessage)).addFlags(this.extractFlags(nativeMessage)).addAttachments(this.extractAttachments(nativeMessage));
        return builder.build();
    }

    private MimeBodyPart createFilePart(EmailAttachment attachment, String disposition) {
        MimeBodyPart mimePart = null;

        try {
            byte[] attachmentBytesBase64 = Base64.getEncoder().encode(attachment.getContent());
            InternetHeaders headers = new InternetHeaders();
            headers.setHeader(HEADER_CONTENT_ID, this.formatContentId(attachment));
            headers.setHeader(HEADER_CONTENT_TYPE, this.formatContentType(attachment));
            headers.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, "base64");
            headers.setHeader(HEADER_CONTENT_DISPOSITION, this.formatContentDisposition(attachment, disposition));
            mimePart = new MimeBodyPart(headers, attachmentBytesBase64);
            mimePart.setFileName(attachment.getFileName());
            mimePart.setDisposition(disposition);
            return mimePart;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private String formatContentId(EmailAttachment attachment) {
        return "<" + attachment.getFileName() + ">";
    }

    private String formatContentType(EmailAttachment attachment) {
        return attachment.getMimeType() + "; name=\"" + attachment.getFileName() + "\"";
    }

    private String formatContentDisposition(EmailAttachment attachment, String disposition) {
        return disposition.toLowerCase() + "; filename=\"" + attachment.getFileName() + "\"";
    }

    private Set<EmailFlag> extractFlags(Message nativeMessage) {
        Set<EmailFlag> flags = new HashSet<>();
        try {
            if (!nativeMessage.getFlags().contains(Flags.Flag.SEEN)) {
                flags.add(EmailFlag.UNREAD);
            }

            return flags;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private String extractParentFolder(Message nativeMessage) {
        Folder folder = nativeMessage.getFolder();
        return folder != null ? folder.getFullName() : null;
    }

    private String extractMessageId(Message nativeMessage) {
        String messageId = null;

        try {
            UIDFolder folder = (UIDFolder) nativeMessage.getFolder();
            if (folder != null) {
                messageId = String.valueOf(folder.getUID(nativeMessage));
            }

            return messageId;
        } catch (MessagingException var4) {
            throw new EmailMessagingException(var4);
        }
    }

    private String extractHtml(Message nativeMessage) {
        String html = null;

        try {
            html = this.extractBodyText(nativeMessage, "text/html");
            return html;
        } catch (IOException | MessagingException var4) {
            throw new EmailMessagingException(var4);
        }
    }

    private String extractText(Message nativeMessage) {
        String text = "";

        try {
            text = this.extractBodyText(nativeMessage, "text/plain");
            if (text == null) {
                String html = this.extractBodyText(nativeMessage, "text/html");
                if (html != null) {
                    text = EmailUtils.htmlToText(html);
                } else {
                    logger.warn("No text or html body was found in message: " + this.nativeMessageToString(nativeMessage));
                }
            }

            return text;
        } catch (IOException | MessagingException var4) {
            throw new EmailMessagingException("Unable to extract body text", var4);
        }
    }

    private String extractBodyText(Part part, String type) throws MessagingException, IOException {
        String result = null;
        String mimeType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();
        if (mimeType.contains(MIME_MULTIPART) && disposition == null) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount() && result == null; ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result = this.extractBodyText(childPart, type);
            }
        } else if (mimeType.contains(type) && disposition == null) {
            Object content = part.getContent();
            if (content instanceof String) {
                result = (String) content;
            } else if (content instanceof Part) {
                result = this.extractBodyText((Part) content, type);
            }
        }

        return result;
    }

    private List<EmailAttachment> extractAttachments(Message nativeMessage) {
        try {
            return this.extractFiles(nativeMessage, "attachment");
        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
    }

    private List<EmailAttachment> extractInlineFiles(Message nativeMessage) {
        try {
            List<EmailAttachment> result = this.extractFiles(nativeMessage, "inline");
            return result;
        } catch (MessagingException | IOException var3) {
            throw new EmailMessagingException(var3);
        }
    }

    private List<EmailAttachment> extractFiles(Part part, String dispositionCode) throws MessagingException, IOException {
        List<EmailAttachment> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();
        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(this.extractFiles(childPart, dispositionCode));
            }
        } else {
            String disposition = part.getDisposition();
            if (disposition != null && disposition.equalsIgnoreCase(dispositionCode)) {
                if (mimeType.contains(";")) {
                    mimeType = mimeType.substring(0, mimeType.indexOf(";"));
                }

                result.add(new EmailAttachment(part.getFileName(), part.getInputStream(), mimeType));
            }
        }

        return result;
    }

    private String extractSubject(Message nativeMessage) {
        try {
            return nativeMessage.getSubject();
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private ZonedDateTime extractDate(Message nativeMessage) {
        ZonedDateTime result = null;

        try {
            Date date = nativeMessage.getReceivedDate();
            if (date != null) {
                result = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } else {
                date = nativeMessage.getSentDate();
                if (date != null) {
                    result = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                }
            }

            return result;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private List<EmailAddress> extractReplyTo(Message nativeMessage) {
        List<EmailAddress> replyTo;
        try {
            Address[] addrList = nativeMessage.getReplyTo();
            replyTo = this.convertToEmailAddresses(addrList);
            return replyTo;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private List<EmailAddress> extractTo(Message nativeMessage) {
        return this.extractRecipients(nativeMessage, Message.RecipientType.TO);
    }

    private List<EmailAddress> extractCc(Message nativeMessage) {
        return this.extractRecipients(nativeMessage, Message.RecipientType.CC);
    }

    private List<EmailAddress> extractBcc(Message nativeMessage) {
        return this.extractRecipients(nativeMessage, Message.RecipientType.BCC);
    }

    private List<EmailAddress> extractRecipients(Message nativeMessage, Message.RecipientType recepientType) {
        List<EmailAddress> recepients = new ArrayList<>();

        try {
            Address[] addrList = nativeMessage.getRecipients(recepientType);
            if (addrList != null) {
                recepients = this.convertToEmailAddresses(addrList);
            }

            return recepients;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private List<EmailAddress> convertToEmailAddresses(Address[] addrList) {
        List<EmailAddress> emailAddresses = new ArrayList<>();
        Address[] addresses = addrList;
        int length = addrList.length;

        for (int i = 0; i < length; ++i) {
            Address addr = addresses[i];
            if (addr instanceof InternetAddress) {
                InternetAddress iAddr = (InternetAddress) addr;
                String address = iAddr.getAddress();
                String name = iAddr.getPersonal();
                emailAddresses.add(EmailAddress.of(address, name));
            }
        }

        return emailAddresses;
    }

    private EmailAddress extractFrom(Message nativeMessage) {
        Address[] fromAddress;
        try {
            fromAddress = nativeMessage.getFrom();
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }

        String from = fromAddress[0].toString();
        return EmailAddress.of(from);
    }

    private String nativeMessageToString(Message message) {
        StringBuilder b = new StringBuilder();
        b.append("{folder:");
        b.append(this.extractParentFolder(message));
        b.append(",  uid:");
        b.append(", subject:");
        b.append(this.extractSubject(message));
        b.append("}");
        return b.toString();
    }
}