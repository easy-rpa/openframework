package eu.ibagroup.easyrpa.openframework.email.service.javax;

import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.*;
import eu.ibagroup.easyrpa.openframework.email.message.templates.FreeMarkerTemplate;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;
import freemarker.template.TemplateException;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class MimeMessageConverter extends MessageConverter<Message> {

    private static final String MIME_MULTIPART = "multipart";
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String MIME_TEXT_HTML = "text/html";
    private static final String MIME_MESSAGE = "message/rfc822";

    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_ID = "Content-ID";

    private final Session session;

    public MimeMessageConverter(Session session) {
        this.session = session;
    }

    @Override
    public Message convertToNativeMessage(EmailMessage emailMessage) {
        MimeMessage message = new MimeMessage(this.session);

        try {

            if (emailMessage.getFrom() != null) {
                message.setFrom(new InternetAddress(emailMessage.getFrom().toString()));
            } else if (emailMessage.getSender() != null) {
                message.setFrom(new InternetAddress(emailMessage.getSender().toString()));
            }

            emailMessage.getRecipients().forEach(to -> {
                try {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.toString()));
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
            emailMessage.getCcRecipients().forEach(cc -> {
                try {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc.toString()));
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
            emailMessage.getBccRecipients().forEach(bcc -> {
                try {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc.toString()));
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
            message.setReplyTo(emailMessage.getReplyTo().stream().map(address -> {
                try {
                    return new InternetAddress(address.toString());
                } catch (AddressException e) {
                    throw new EmailMessagingException(e);
                }
            }).toArray(InternetAddress[]::new));

            message.setSubject(emailMessage.getSubject());

            if (emailMessage.getDate() != null) {
                message.setSentDate(Date.from(emailMessage.getDate().toInstant()));
            }

            Multipart contentRoot = new MimeMultipart("alternative");

            String charset = emailMessage.getCharset();
            String charsetString = "; charset=" + (charset == null || charset.trim().isEmpty() ? StandardCharsets.UTF_8 : charset);

            for (EmailBodyPart part : emailMessage.getBodyParts()) {
                MimeBodyPart mimeBodyPart = null;

                if (part instanceof EmailBodyText) {
                    mimeBodyPart = new MimeBodyPart();
                    mimeBodyPart.setContent(((EmailBodyText) part).getContent(), MIME_TEXT_HTML + charsetString);

                } else if (part instanceof EmailBodyTemplate) {
                    FreeMarkerTemplate template = new FreeMarkerTemplate(((EmailBodyTemplate) part).getTemplateText(), emailMessage.getBodyProperties());
                    mimeBodyPart = new MimeBodyPart();
                    mimeBodyPart.setContent(template.compile(), MIME_TEXT_HTML + charsetString);

                } else if (part instanceof EmailAttachment) {
                    mimeBodyPart = createFilePart((EmailAttachment) part, Part.INLINE);
                }

                if (mimeBodyPart != null) {
                    contentRoot.addBodyPart(mimeBodyPart);
                }
            }

            for (EmailAttachment attachment : emailMessage.getAttachments()) {
                contentRoot.addBodyPart(createFilePart(attachment, Part.ATTACHMENT));
            }

            if (emailMessage.isRead()) {
                message.setFlags(new Flags(Flags.Flag.SEEN), true);
            }

            if (emailMessage.getPreviousMessage() != null) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(convertToNativeMessage(emailMessage.getPreviousMessage()), MIME_MESSAGE);
            }

            message.setContent(contentRoot);
            message.saveChanges();

            return message;
        } catch (MessagingException | IOException | TemplateException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage convertToEmailMessage(Message nativeMessage) {
        try {
            MimeMessage mimeMessage = (MimeMessage) nativeMessage;

            String messageId = null;
            String folderName = null;
            ZonedDateTime dateTime = null;

            Folder folder = mimeMessage.getFolder();
            if (folder != null) {
                messageId = folder instanceof UIDFolder ? String.valueOf(((UIDFolder) folder).getUID(mimeMessage)) : null;
                folderName = folder.getFullName();
            }
            Date date = mimeMessage.getReceivedDate();
            if (date != null) {
                dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } else {
                date = mimeMessage.getSentDate();
                if (date != null) {
                    dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                }
            }

            EmailMessage emailMessage = new EmailMessage(messageId, folderName, dateTime);

            emailMessage.setSender(convertToEmailAddress(mimeMessage.getSender()));
            List<EmailAddress> fromAddress = convertToEmailAddresses(mimeMessage.getFrom());
            if (fromAddress.size() > 0) {
                emailMessage.setFrom(fromAddress.get(0));
            }
            emailMessage.setRecipients(convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.TO)));
            emailMessage.setCcRecipients(convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.CC)));
            emailMessage.setBccRecipients(convertToEmailAddresses(mimeMessage.getRecipients(Message.RecipientType.BCC)));
            emailMessage.setReplyTo(convertToEmailAddresses(mimeMessage.getReplyTo()));

            //TODO extract charset

            emailMessage.setSubject(mimeMessage.getSubject());
            emailMessage.setBodyParts(extractBodyParts(mimeMessage));
            emailMessage.setAttachments(extractAttachments(mimeMessage));
            emailMessage.setPreviousMessage(extractPreviousMessage(mimeMessage));
            emailMessage.setRead(mimeMessage.getFlags().contains(Flags.Flag.SEEN));

            return emailMessage;

        } catch (MessagingException | IOException e) {
            throw new EmailMessagingException(e);
        }
    }

    private MimeBodyPart createFilePart(EmailAttachment attachment, String disposition) {
        MimeBodyPart filePart;
        try {
            byte[] attachmentBytesBase64 = Base64.getEncoder().encode(attachment.getContent());
            InternetHeaders headers = new InternetHeaders();
            headers.setHeader(HEADER_CONTENT_ID, this.formatContentId(attachment));
            headers.setHeader(HEADER_CONTENT_TYPE, this.formatContentType(attachment));
            headers.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, "base64");
            headers.setHeader(HEADER_CONTENT_DISPOSITION, this.formatContentDisposition(attachment, disposition));
            filePart = new MimeBodyPart(headers, attachmentBytesBase64);
            filePart.setFileName(attachment.getFileName());
            filePart.setDisposition(disposition);
            return filePart;
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
        EmailAddress emailAddress = null;
        if (address != null) {
            if (address instanceof InternetAddress) {
                InternetAddress iAddress = (InternetAddress) address;
                emailAddress = new EmailAddress(iAddress.getAddress(), iAddress.getPersonal());
            } else {
                emailAddress = new EmailAddress(address.toString());
            }
        }
        return emailAddress;
    }

    private List<EmailBodyPart> extractBodyParts(Part part) throws MessagingException, IOException {
        List<EmailBodyPart> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(extractBodyParts(childPart));
            }

        } else if (mimeType.contains(MIME_TEXT_PLAIN) || mimeType.contains(MIME_TEXT_HTML)) {
            Object content = part.getContent();
            if (content instanceof String) {
                result.add(new EmailBodyText((String) content));

            } else if (content instanceof Part) {
                result.addAll(this.extractBodyParts((Part) content));
            }

        } else if (Part.INLINE.equalsIgnoreCase(disposition)) {
            if (mimeType.contains(";")) {
                mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            }
            result.add(new EmailAttachment(part.getFileName(), part.getInputStream(), mimeType));
        }

        return result;
    }

    private List<EmailAttachment> extractAttachments(Part part) throws MessagingException, IOException {
        List<EmailAttachment> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();

        if (Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
            if (mimeType.contains(";")) {
                mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            }
            result.add(new EmailAttachment(part.getFileName(), part.getInputStream(), mimeType));

        } else if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(extractAttachments(childPart));
            }

        } else if (part.getContent() instanceof Part) {
            result.addAll(extractAttachments((Part) part.getContent()));
        }

        return result;
    }


    private EmailMessage extractPreviousMessage(Part part) throws MessagingException, IOException {
        EmailMessage result = null;
        String mimeType = part.getContentType().toLowerCase();

        if (mimeType.contains(MIME_MESSAGE) && part.getContent() instanceof MimeMessage) {
            result = convertToEmailMessage((MimeMessage) part.getContent());

        } else if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result = extractPreviousMessage(childPart);
                if (result != null) {
                    break;
                }
            }

        } else if (part.getContent() instanceof Part) {
            result = extractPreviousMessage((Part) part.getContent());
        }

        return result;
    }
}
