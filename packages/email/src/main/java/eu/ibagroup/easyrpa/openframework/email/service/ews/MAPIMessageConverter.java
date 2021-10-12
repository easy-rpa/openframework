package eu.ibagroup.easyrpa.openframework.email.service.ews;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailFlag;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.datatypes.Chunks;
import org.apache.poi.hsmf.datatypes.StringChunk;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.StringUtil.StringsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MAPIMessageConverter extends MessageConverter<MAPIMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(MAPIMessageConverter.class);

    public MAPIMessageConverter() {
    }

    public MAPIMessage createNativeMessage(EmailMessage emailMessage) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public EmailMessage convertToEmailMessage(MAPIMessage nativeMessage) {
        nativeMessage.guess7BitEncoding();
        EmailAddress from = this.extractFrom(nativeMessage);
        EmailMessage.EmailMessageBuilder builder = EmailMessage.EmailMessageBuilder.newMessage(from, new EmailAddress[0]);
        builder.id(this.extractId(nativeMessage));
        builder.addReplyTo(this.extractReplyTo(nativeMessage));
        builder.date(this.extractDate(nativeMessage));
        builder.subject(this.extractSubject(nativeMessage));
        builder.text(this.extractText(nativeMessage));
        builder.html(this.extractHtml(nativeMessage));
        builder.inlineFiles(this.extractInlineFiles(nativeMessage));
        builder.parentFolder(this.extractParentFolder(nativeMessage));
        builder.addFlags(this.extractFlags(nativeMessage));
        builder.addAttachments(this.extractAttachments(nativeMessage));
        this.extractRecepients(nativeMessage, builder);
        return builder.build();
    }

    private List<EmailAddress> extractReplyTo(MAPIMessage nativeMessage) {
        return new ArrayList<>();
    }

    private ZonedDateTime extractDate(MAPIMessage nativeMessage) {
        ZonedDateTime zdt = null;

        try {
            Calendar date = nativeMessage.getMessageDate();
            zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        } catch (ChunkNotFoundException e) {
            try {
                String[] headers = nativeMessage.getHeaders();
                String[] h = headers;
                int length = headers.length;

                for (int i = 0; i < length; ++i) {
                    String header = h[i];
                    if (StringUtil.startsWithIgnoreCase(header, "date:")) {
                        zdt = ZonedDateTime.parse(header.substring("date:".length()));
                        break;
                    }
                }
            } catch (ChunkNotFoundException ex) {
                LOG.warn("Unable to get ZonedDateTime using nativeMessage.getHeaders()", ex);
                zdt = ZonedDateTime.now();
            }
        }

        return zdt;
    }

    private String extractSubject(MAPIMessage nativeMessage) {
        String subject = "";

        try {
            subject = nativeMessage.getSubject();
        } catch (ChunkNotFoundException e) {
            LOG.warn("Cannot extcact email subject", e);
        }

        return subject;
    }

    private String extractText(MAPIMessage nativeMessage) {
        String textBody = "";

        try {
            textBody = nativeMessage.getTextBody();
        } catch (ChunkNotFoundException e) {
            LOG.warn("Cannot extract email text", e);
        }

        return textBody;
    }

    private String extractHtml(MAPIMessage nativeMessage) {
        try {
            return nativeMessage.getHtmlBody();
        } catch (ChunkNotFoundException e) {
            LOG.info("No html in email", e);
            return null;
        }
    }

    private String extractParentFolder(MAPIMessage nativeMessage) {
        return null;
    }

    private Set<EmailFlag> extractFlags(MAPIMessage nativeMessage) {
        return new HashSet<>();
    }

    private String extractId(MAPIMessage nativeMessage) {
        Chunks mainChunks = nativeMessage.getMainChunks();
        StringChunk messageId = mainChunks.getMessageId();
        return messageId == null ? null : messageId.getValue();
    }

    private void extractRecepients(MAPIMessage nativeMessage, EmailMessage.EmailMessageBuilder builder) {
        StringsIterator emailRecepients = null;
        String toNames = null;
        String ccNames = null;
        String bccNames = null;

        try {
            emailRecepients = new StringsIterator(nativeMessage.getRecipientEmailAddressList());
            toNames = nativeMessage.getDisplayTo();
            ccNames = nativeMessage.getDisplayCC();
            bccNames = nativeMessage.getDisplayBCC();
        } catch (ChunkNotFoundException e) {
            throw new EmailMessagingException("Unable to get emails from MAPIMessage", e);
        }

        String[] names;
        int namesCount;
        int i;
        String name;
        if (toNames != null && !toNames.isEmpty()) {
            names = toNames.split(";\\s*");
            namesCount = names.length;

            for (i = 0; i < namesCount; ++i) {
                name = names[i];
                builder.addTo(new EmailAddress[] { EmailAddress.of(emailRecepients.next(), name) });
            }
        }

        if (ccNames != null && !ccNames.isEmpty()) {
            names = ccNames.split(";\\s*");
            namesCount = names.length;

            for (i = 0; i < namesCount; ++i) {
                name = names[i];
                builder.addCc(new EmailAddress[] { EmailAddress.of(emailRecepients.next(), name) });
            }
        }

        if (bccNames != null && !bccNames.isEmpty()) {
            names = bccNames.split(";\\s*");
            namesCount = names.length;

            for (i = 0; i < namesCount; ++i) {
                name = names[i];
                builder.addBcc(new EmailAddress[] { EmailAddress.of(emailRecepients.next(), name) });
            }
        }

    }

    private EmailAddress extractFrom(MAPIMessage nativeMessage) {
        String emailAddress = null;

        try {
            String[] headers = nativeMessage.getHeaders();
            int length = headers.length;

            for (int i = 0; i < length; ++i) {
                String header = headers[i];
                if (StringUtil.startsWithIgnoreCase(header, "from:")) {
                    emailAddress = header.substring("from:".length());
                    break;
                }
            }
        } catch (ChunkNotFoundException e) {
            LOG.info("No mail sender", e);
            return null;
        }

        return EmailAddress.of(emailAddress);
    }

    private List<EmailAttachment> extractAttachments(MAPIMessage nativeMessage) {
        List<EmailAttachment> attachments = new ArrayList<>();
        AttachmentChunks[] attachmentFiles = nativeMessage.getAttachmentFiles();
        int length = attachmentFiles.length;

        for (int i = 0; i < length; ++i) {
            AttachmentChunks att = attachmentFiles[i];
            StringChunk name = att.getAttachLongFileName();
            if (name == null) {
                name = att.getAttachFileName();
            }

            String attName = name == null ? null : name.getValue();
            StringChunk attachMimeTag = att.getAttachMimeTag();
            String mimeTag = attachMimeTag == null ? null : attachMimeTag.getValue();
            byte[] embeddedAttachment = att.getEmbeddedAttachmentObject();
            if (embeddedAttachment != null) {
                attachments.add(new EmailAttachment(attName, embeddedAttachment, mimeTag));
            }
        }

        return attachments;
    }

    private List<EmailAttachment> extractInlineFiles(MAPIMessage nativeMessage) {
        return new ArrayList<>();
    }
}
