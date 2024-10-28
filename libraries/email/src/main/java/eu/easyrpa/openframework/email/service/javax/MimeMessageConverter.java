package eu.easyrpa.openframework.email.service.javax;

import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.message.EmailAddress;
import eu.easyrpa.openframework.email.message.EmailAttachment;
import eu.easyrpa.openframework.email.message.EmailBodyPart;
import eu.easyrpa.openframework.email.service.MessageConverter;
import org.apache.commons.lang3.StringUtils;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MessageConverter} for converting of {@link EmailMessage} to {@link MimeMessage}.
 */
public class MimeMessageConverter extends MessageConverter<Message> {

    private static final Pattern INLINE_IMAGE_NAME_RE = Pattern.compile("src=\"cid:([\\w.]+)\"");
    private static final Pattern INLINE_FILE_NAME_RE = Pattern.compile("<i>\\(See attached file: ([\\w.]+)\\)</i>");

    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_ID = "Content-ID";
    private static final String CHARSET_KEY = "charset=";
    private static final String MIME_MULTIPART = "multipart";


    private final Session session;

    public MimeMessageConverter(Session session) {
        this.session = session;
    }

    /**
     * Converts {@link EmailMessage} message to the {@link MimeMessage} message.
     *
     * @param emailMessage the source {@link EmailMessage} to convert.
     * @return the {@link MimeMessage} message corresponding to the source {@link EmailMessage} message.
     */
    @Override
    public Message convertToNativeMessage(EmailMessage emailMessage) {
        try {

            if (emailMessage instanceof MimeMessageWrapper) {
                MimeMessage message = ((MimeMessageWrapper) emailMessage).getMimeMessage();
                message.saveChanges();
                return message;
            }

            MimeMessage message = new MimeMessage(this.session);


            Map<String, String> headers = emailMessage.getHeaders();
            if (headers != null) {
                headers.forEach((k, v) -> {
                    try {
                        message.setHeader(k, v);
                    } catch (MessagingException e) {
                        throw new EmailMessagingException(e);
                    }
                });
            }

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

            Multipart contentRoot = new MimeMultipart("mixed");

            contentRoot.addBodyPart(createBodyPart(emailMessage));

            Set<String> inlineFileName = getInlineFileNames(emailMessage);
            for (EmailAttachment attachment : emailMessage.getAttachments()) {
                contentRoot.addBodyPart(createAttachmentPart(attachment,
                        inlineFileName.contains(attachment.getFileName()) ? Part.INLINE : Part.ATTACHMENT));
            }

            if (emailMessage.isRead()) {
                message.setFlags(new Flags(Flags.Flag.SEEN), true);
            }

            message.setContent(contentRoot);
            message.saveChanges();

            return message;
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    /**
     * Converts the {@link MimeMessage} message to {@link EmailMessage} message.
     *
     * @param nativeMessage the source {@link MimeMessage} message to convert.
     * @return the {@link EmailMessage} corresponding to the source {@link MimeMessage} message.
     */
    @Override
    public EmailMessage convertToEmailMessage(Message nativeMessage) {
        return new MimeMessageWrapper((MimeMessage) nativeMessage);
    }

    public static void updateBodyPart(Part part, EmailBodyPart newContent, String charset) throws MessagingException, IOException {
        String mimeType = part.getContentType().toLowerCase();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                updateBodyPart(childPart, newContent, charset);
            }
        } else if (part.getContent() instanceof Part) {
            updateBodyPart((Part) part.getContent(), newContent, charset);

        } else if (part instanceof MimeBodyPart
                && (mimeType.contains(newContent.getContentType())
                || (mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_RTF) && newContent.isHtml()))) {
            MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
            charset = charset == null || charset.trim().isEmpty() ? StandardCharsets.UTF_8.name() : charset;

            if (newContent.isHtml()) {
                String html = newContent.getContent();
                html = html.replaceAll(EmailAttachment.INLINE_IMAGE_PLACEHOLDER_RE.pattern(),
                                "<img src=\"cid:$1\" alt=\"$1\" width=\"$2\" height=\"$3\">")
                        .replaceAll(EmailAttachment.INLINE_ATTACHMENT_PLACEHOLDER_RE.pattern(),
                                "<i>(See attached file: $1)</i>");

                mimeBodyPart.setText(html, charset, "html");

            } else if (newContent.isText()) {
                String text = newContent.getContent();

                mimeBodyPart.setText(text, charset, "plain");
            }
        }
    }

    public static MimeBodyPart createBodyPart(EmailMessage emailMessage) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDisposition(Part.INLINE);

        String charset = emailMessage.getCharset();
        charset = charset == null || charset.trim().isEmpty() ? StandardCharsets.UTF_8.name() : charset;

        if (emailMessage.hasHtml()) {
            String html = emailMessage.getHtml();
            html = html.replaceAll(EmailAttachment.INLINE_IMAGE_PLACEHOLDER_RE.pattern(),
                            "<img src=\"cid:$1\" alt=\"$1\" width=\"$2\" height=\"$3\">")
                    .replaceAll(EmailAttachment.INLINE_ATTACHMENT_PLACEHOLDER_RE.pattern(),
                            "<i>(See attached file: $1)</i>");

            String subMessageHeader = null;
            String subMessageHead = null;
            String subMessageBody = null;

            if (emailMessage.getForwardedMessage() != null) {
                subMessageHeader = formatSubMessageHeader(emailMessage.getForwardedMessage(), true, true);
                subMessageBody = emailMessage.getForwardedMessage().getHtml();
                if (subMessageBody.contains("<head>")) {
                    subMessageHead = StringUtils.substringBetween(subMessageBody, "<head>", "</head>");
                }
                if (subMessageBody.contains("<body>")) {
                    subMessageBody = StringUtils.substringBetween(subMessageBody, "<body>", "</body>");
                }

            } else if (emailMessage.getReplyOnMessage() != null) {
                subMessageHeader = formatSubMessageHeader(emailMessage.getReplyOnMessage(), true, false);
                subMessageBody = emailMessage.getReplyOnMessage().getHtml();
                if (subMessageBody.contains("<head>")) {
                    subMessageHead = StringUtils.substringBetween(subMessageBody, "<head>", "</head>");
                }
                if (subMessageBody.contains("<body>")) {
                    subMessageBody = StringUtils.substringBetween(subMessageBody, "<body>", "</body>");
                }
                String bq = "<blockquote style=\"margin:0px 0px 0px 0.8ex;border-left:1px solid rgb(204,204,204);padding-left:1ex\">%s</blockquote>";
                subMessageBody = String.format(bq, subMessageBody);
            }

            if (subMessageBody != null) {
                if (subMessageHead != null && html.contains("<head>")) {
                    html = html.replaceAll("</head>", subMessageHead + "</head>");
                } else if (subMessageHead != null && !html.contains("<head>")) {
                    html = html.replaceAll("<html>", "<html><head>" + subMessageHead + "</head>");
                }
                html = html.replaceAll("</body>", "<br><div>" + subMessageHeader + subMessageBody + "</div></body>");
            }

            mimeBodyPart.setText(html, charset, "html");

        } else if (emailMessage.hasText()) {
            String text = emailMessage.getText();

            String subMessageBody = null;
            if (emailMessage.getForwardedMessage() != null) {
                String subMessageHeader = formatSubMessageHeader(emailMessage.getForwardedMessage(), false, true);
                subMessageBody = subMessageHeader + emailMessage.getForwardedMessage().getText();
            } else if (emailMessage.getReplyOnMessage() != null) {
                String subMessageHeader = formatSubMessageHeader(emailMessage.getReplyOnMessage(), false, false);
                subMessageBody = subMessageHeader + emailMessage.getReplyOnMessage().getText();
            }
            if (subMessageBody != null) {
                text += "\n\n\n" + subMessageBody;
            }

            mimeBodyPart.setText(text, charset, "plain");
        }

        return mimeBodyPart;
    }

    public static List<EmailBodyPart> extractBodyParts(Part part) throws MessagingException, IOException {
        List<EmailBodyPart> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(extractBodyParts(childPart));
            }
        } else if (part.getContent() instanceof Part) {
            result.addAll(extractBodyParts((Part) part.getContent()));

        } else if (mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN)) {
            Object content = part.getContent();
            if (content instanceof String) {
                result.add(new EmailBodyPart((String) content, EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN));
            }

        } else if (mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_HTML)) {
            Object content = part.getContent();
            if (content instanceof String) {
                result.add(new EmailBodyPart((String) content, EmailBodyPart.CONTENT_TYPE_TEXT_HTML));
            }

        } else if (mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_RTF)) {
            Object content = part.getContent();
            if (content instanceof String) {
                result.add(new EmailBodyPart((String) content, EmailBodyPart.CONTENT_TYPE_TEXT_RTF));
            }
        }

        return result;
    }

    public static void addAttachment(MimeMessage message, EmailAttachment attachment) throws MessagingException, IOException {
        String mimeType = message.getContentType().toLowerCase();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) message.getContent();

            Set<String> inlineFileName = getInlineFileNames(message);

            multipart.addBodyPart(createAttachmentPart(attachment,
                    inlineFileName.contains(attachment.getFileName()) ? Part.INLINE : Part.ATTACHMENT));

        } else {
            throw new UnsupportedOperationException("New files can be attached only to multipart content.");
        }
    }

    public static void removeAttachments(Part part) throws MessagingException, IOException {
        if (part.getContentType().toLowerCase().contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            List<BodyPart> attachments = new ArrayList<>();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                String childMimeType = childPart.getContentType().toLowerCase();
                String disposition = childPart.getDisposition();

                if (childPart.getContent() instanceof Part) {
                    removeAttachments((Part) part.getContent());

                } else if (!childMimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN)
                        && !childMimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_HTML)
                        && !childMimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_RTF)
                        && (Part.ATTACHMENT.equalsIgnoreCase(disposition)
                        || Part.INLINE.equalsIgnoreCase(disposition))) {
                    attachments.add(childPart);
                }
            }
            for (BodyPart attachment : attachments) {
                multipart.removeBodyPart(attachment);
            }
        } else if (part.getContent() instanceof Part) {
            removeAttachments((Part) part.getContent());
        }
    }

    public static MimeBodyPart createAttachmentPart(EmailAttachment attachment, String disposition) throws MessagingException {
        byte[] attachmentBytesBase64 = Base64.getEncoder().encode(attachment.getContent());
        InternetHeaders headers = new InternetHeaders();
        headers.setHeader(HEADER_CONTENT_ID, formatContentId(attachment));
        headers.setHeader(HEADER_CONTENT_TYPE, formatContentType(attachment));
        headers.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, "base64");
        headers.setHeader(HEADER_CONTENT_DISPOSITION, formatContentDisposition(attachment, disposition));
        MimeBodyPart filePart = new MimeBodyPart(headers, attachmentBytesBase64);
        filePart.setFileName(attachment.getFileName());
        filePart.setDisposition(disposition);
        return filePart;
    }

    public static List<EmailAttachment> extractAttachments(Part part) throws MessagingException, IOException {
        List<EmailAttachment> result = new ArrayList<>();
        String mimeType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();

        if (mimeType.contains(MIME_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();

            for (int i = 0; i < multipart.getCount(); ++i) {
                BodyPart childPart = multipart.getBodyPart(i);
                result.addAll(extractAttachments(childPart));
            }

        } else if (!mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_PLAIN)
                && !mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_HTML)
                && !mimeType.contains(EmailBodyPart.CONTENT_TYPE_TEXT_RTF)
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

    public static Set<String> getInlineFileNames(MimeMessage message) throws MessagingException, IOException {
        List<EmailBodyPart> bodyParts = extractBodyParts(message);
        List<String> lookupParts = bodyParts.stream().map(EmailBodyPart::getContent).collect(Collectors.toList());
        return getInlineFileNames(lookupParts);
    }

    public static Set<String> getInlineFileNames(EmailMessage emailMessage) {
        List<String> lookupParts = new ArrayList<>();
        if (emailMessage.hasText()) {
            lookupParts.add(emailMessage.getText());
            if (emailMessage.getForwardedMessage() != null) {
                lookupParts.add(emailMessage.getForwardedMessage().getText());
            }
            if (emailMessage.getReplyOnMessage() != null) {
                lookupParts.add(emailMessage.getReplyOnMessage().getText());
            }
        }
        if (emailMessage.hasHtml()) {
            lookupParts.add(emailMessage.getHtml());
            if (emailMessage.getForwardedMessage() != null) {
                lookupParts.add(emailMessage.getForwardedMessage().getHtml());
            }
            if (emailMessage.getReplyOnMessage() != null) {
                lookupParts.add(emailMessage.getReplyOnMessage().getHtml());
            }
        }
        return getInlineFileNames(lookupParts);
    }

    private static Set<String> getInlineFileNames(List<String> lookupParts) {
        Set<String> result = new HashSet<>();
        for (String part : lookupParts) {
            if (part == null) {
                continue;
            }
            Matcher matcher = EmailAttachment.INLINE_IMAGE_PLACEHOLDER_RE.matcher(part);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
            matcher = EmailAttachment.INLINE_ATTACHMENT_PLACEHOLDER_RE.matcher(part);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
            matcher = INLINE_IMAGE_NAME_RE.matcher(part);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
            matcher = INLINE_FILE_NAME_RE.matcher(part);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        }
        return result;
    }

    public static String extractCharset(Part part) throws MessagingException, IOException {
        String charset = null;
        String mimeType = part.getContentType().toLowerCase();

        if (mimeType.contains(CHARSET_KEY)) {
            int endOfCharsetSubstring = mimeType.indexOf(";", mimeType.indexOf(CHARSET_KEY));
            if (endOfCharsetSubstring >= 0) {
                charset = mimeType.substring(mimeType.indexOf(CHARSET_KEY) + CHARSET_KEY.length(), endOfCharsetSubstring).trim().toUpperCase();
            } else {
                charset = mimeType.substring(mimeType.indexOf(CHARSET_KEY) + CHARSET_KEY.length()).trim().toUpperCase();
            }
            if (charset.startsWith("\"") && charset.endsWith("\"")) {
                charset = charset.substring(1, charset.length() - 1);
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

    private static String formatContentId(EmailAttachment attachment) {
        return "<" + attachment.getFileName() + ">";
    }

    private static String formatContentType(EmailAttachment attachment) {
        return attachment.getMimeType() + "; name=\"" + attachment.getFileName() + "\"";
    }

    private static String formatContentDisposition(EmailAttachment attachment, String disposition) {
        return disposition.toLowerCase() + "; filename=\"" + attachment.getFileName() + "\"";
    }

    private static String formatSubMessageHeader(EmailMessage subMessage, boolean isHtml, boolean isForwardedMessage) {
        List<String> header = new ArrayList<>();
        if (isForwardedMessage) {
            header.add("---------- Forwarded message ---------");
        } else {
            header.add("---------- Reply on ---------");
        }

        EmailAddress from = subMessage.getFrom() != null ? subMessage.getFrom() : subMessage.getSender();
        header.add(String.format("From: %s", isHtml ? from.toHtml() : from.toString()));

        LocalDateTime date = subMessage.getDateTime();
        if (date != null) {
            header.add(String.format("Date: %s", date.format(DateTimeFormatter.ofPattern(EmailMessage.USED_DATE_TME_FORMAT_PATTERN))));
        }

        header.add(String.format("Subject: %s", subMessage.getSubject()));

        String to = subMessage.getRecipients().stream()
                .map(ea -> isHtml ? ea.toHtml() : ea.toString())
                .collect(Collectors.joining(isHtml ? ",&nbsp;" : ", "));
        header.add(String.format("To: %s", to));

        if (subMessage.getCcRecipients() != null && !subMessage.getCcRecipients().isEmpty()) {
            String cc = subMessage.getCcRecipients().stream()
                    .map(ea -> isHtml ? ea.toHtml() : ea.toString())
                    .collect(Collectors.joining(isHtml ? ",&nbsp;" : ", "));
            header.add(String.format("Cc: %s", cc));
        }

        header.add(isHtml ? "<br>" : "\n");
        return isHtml ?
                String.format("<div>%s</div>", String.join("\n<br>\n", header))
                : String.join("\n", header);
    }
}
