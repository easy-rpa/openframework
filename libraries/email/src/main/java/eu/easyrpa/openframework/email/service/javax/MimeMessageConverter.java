package eu.easyrpa.openframework.email.service.javax;

import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.message.EmailAddress;
import eu.easyrpa.openframework.email.message.EmailAttachment;
import eu.easyrpa.openframework.email.service.MessageConverter;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    private Set<String> getInlineFileNames(EmailMessage emailMessage) {
        Set<String> result = new HashSet<>();
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

    private MimeBodyPart createBodyPart(EmailMessage emailMessage) {
        try {
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
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }

    private MimeBodyPart createAttachmentPart(EmailAttachment attachment, String disposition) {
        try {
            byte[] attachmentBytesBase64 = Base64.getEncoder().encode(attachment.getContent());
            InternetHeaders headers = new InternetHeaders();
            headers.setHeader(HEADER_CONTENT_ID, this.formatContentId(attachment));
            headers.setHeader(HEADER_CONTENT_TYPE, this.formatContentType(attachment));
            headers.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, "base64");
            headers.setHeader(HEADER_CONTENT_DISPOSITION, this.formatContentDisposition(attachment, disposition));
            MimeBodyPart filePart = new MimeBodyPart(headers, attachmentBytesBase64);
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

    private String formatSubMessageHeader(EmailMessage subMessage, boolean isHtml, boolean isForwardedMessage) {
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
