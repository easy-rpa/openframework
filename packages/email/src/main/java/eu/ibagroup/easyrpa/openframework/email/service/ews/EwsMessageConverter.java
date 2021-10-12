package eu.ibagroup.easyrpa.openframework.email.service.ews;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailFlag;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;
import microsoft.exchange.webservices.data.core.EwsServiceXmlReader;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.apache.commons.io.IOUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EwsMessageConverter extends MessageConverter<Item> {
    private ExchangeService service;

    EwsMessageConverter(ExchangeService service) {
        super();
        this.service = service;
    }

    @Override
    public Item createNativeMessage(EmailMessage emailMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage msg;
        // native email message
        try {
            msg = new microsoft.exchange.webservices.data.core.service.item.EmailMessage(service);
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
        //body
        MessageBody messageBody = new MessageBody();
        if (emailMessage.getHtml().isPresent()) {
            messageBody.setBodyType(BodyType.HTML);
            messageBody.setText(emailMessage.getHtml().get());

        } else {
            messageBody.setText(emailMessage.getText());
        }

        try {
            msg.setBody(messageBody);
        } catch (Exception e) {

        }
        //subject
        try {
            msg.setSubject(emailMessage.getSubject());
        } catch (Exception e) {

        }
        //address from
        microsoft.exchange.webservices.data.property.complex.EmailAddress addressFrom = new microsoft.exchange.webservices.data.property.complex.EmailAddress();
        addressFrom.setAddress(emailMessage.getFrom() != null ? emailMessage.getFrom().getAddress().replaceAll(" ", "") : null);
        try {
            msg.setFrom(addressFrom);
        } catch (Exception e) {

        }
        //address to
        emailMessage.getTo().forEach(m -> {
            try {
                msg.getToRecipients().add(m.getAddress());
            } catch (ServiceLocalException e) {

            }
        });
        //attachments
        emailMessage.getAttachments().forEach(att -> {
            try {
                FileAttachment fileAttachment = msg.getAttachments().addFileAttachment(att.getFileName(), att.getContent());
                fileAttachment.setIsInline(false);
                fileAttachment.setContentType(att.getMimeType());

            } catch (ServiceLocalException e) {

            }
        });
        // bcc
        emailMessage.getBcc().forEach(m -> {
            try {
                msg.getBccRecipients().add(m.getAddress());
            } catch (ServiceLocalException e) {

            }
        });
        // cc
        emailMessage.getCc().forEach(m -> {
            try {
                msg.getCcRecipients().add(m.getAddress());
            } catch (ServiceLocalException e) {

            }
        });
        // flag  read
        try {
            msg.setIsRead(emailMessage.getFlags().stream().anyMatch(f -> f.equals(EmailFlag.READ)));
        } catch (Exception e) {

        }

        // inline attachments
        emailMessage.getInlineFiles().forEach(att -> {
            try {
                FileAttachment fileAttachment = msg.getAttachments().addFileAttachment(att.getFileName(), att.getContent());
                fileAttachment.setIsInline(true);
                fileAttachment.setContentId(att.getFileName());

            } catch (ServiceLocalException e) {

            }
        });

        return msg;
    }

    @Override
    public EmailMessage convertToEmailMessage(Item nativeMessage) {
        EmailAddress from = extractFrom(nativeMessage);
        List<EmailAddress> to = extractTo(nativeMessage);

        EmailMessage.EmailMessageBuilder builder = EmailMessage.EmailMessageBuilder.newMessage(from, to);
        builder.id(extractId(nativeMessage)).addCc(extractCc(nativeMessage)).addBcc(extractBcc(nativeMessage)).html(extractHtml(nativeMessage)).text(extractText(nativeMessage)).subject(extractSubject(nativeMessage)).addAttachments(extractAttachments(nativeMessage))
                .addInlineFiles(extractInlineAttachments(nativeMessage));

        return builder.build();
    }

    protected String extractId(Item nativeMessage) {
        try {
            return nativeMessage.getId().toString();
        } catch (ServiceLocalException e) {
        }
        return null;
    }

    protected EmailAddress extractFrom(Item nativeMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage;
        emailMessage = ItemConverter(nativeMessage);
        if (emailMessage != null) {
            microsoft.exchange.webservices.data.property.complex.EmailAddress emailAddress;
            try {
                emailAddress = emailMessage.getFrom();
                return EmailAddress.of(emailAddress.getAddress());
            } catch (ServiceLocalException e) {

            }
        }
        return null;
    }

    protected List<EmailAddress> extractReplyTo(Item nativeMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage;
        emailMessage = ItemConverter(nativeMessage);
        if (emailMessage != null) {
            try {
                return extractEmailAddressCollection(emailMessage.getReplyTo());
            } catch (ServiceLocalException e) {
            }
        }
        return Collections.emptyList();
    }

    protected List<EmailAddress> extractTo(Item nativeMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage;
        emailMessage = ItemConverter(nativeMessage);
        if (emailMessage != null) {
            try {
                return extractEmailAddressCollection(emailMessage.getToRecipients());
            } catch (ServiceLocalException e) {
            }
        }
        return Collections.emptyList();
    }

    protected List<EmailAddress> extractCc(Item nativeMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage;
        emailMessage = ItemConverter(nativeMessage);
        if (emailMessage != null) {
            try {
                return extractEmailAddressCollection(emailMessage.getCcRecipients());
            } catch (ServiceLocalException e) {
            }
        }
        return Collections.emptyList();
    }

    protected List<EmailAddress> extractBcc(Item nativeMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage;
        emailMessage = ItemConverter(nativeMessage);
        if (emailMessage != null) {
            try {
                return extractEmailAddressCollection(emailMessage.getBccRecipients());
            } catch (ServiceLocalException e) {
            }
        }
        return Collections.emptyList();
    }

    protected ZonedDateTime extractDate(Item nativeMessage) {

        try {
            return ZonedDateTime.ofInstant(nativeMessage.getDateTimeReceived().toInstant(), ZoneId.systemDefault());
        } catch (ServiceLocalException e) {

            return null;
        }
    }

    protected String extractHtml(Item nativeMessage) {
        try {
            if (nativeMessage.getBody().getBodyType() == BodyType.HTML) {
                return nativeMessage.getBody().toString();
            } else {
                return "";
            }
        } catch (ServiceLocalException e) {
            return null;
        }
    }

    protected String extractText(Item nativeMessage) {
        try {
            MessageBody body = nativeMessage.getBody();
            if (body.getBodyType() == BodyType.HTML) {
                String html = body.toString();
                EwsServiceXmlReader reader = new EwsServiceXmlReader(IOUtils.toInputStream(html, "UTF-8"), service);
                body.readTextValueFromXml(reader);
                return body.toString();
            } else {
                return body.toString();
            }
        } catch (Exception e) {
            return null;
        }
    }

    protected String extractSubject(Item nativeMessage) {
        try {
            return nativeMessage.getSubject();
        } catch (ServiceLocalException e) {
            return null;
        }
    }

    protected List<EmailAttachment> extractAttachments(Item nativeMessage) {
        List<EmailAttachment> emailAttachments = new ArrayList<>();
        try {
            if (nativeMessage.getHasAttachments()) {

                for (Attachment a : nativeMessage.getAttachments()) {
                    if (!a.getIsInline()) {
                        String name = a.getName();
                        byte[] content = new byte[0];
                        String contentType = null;
                        if (a instanceof FileAttachment) {
                            FileAttachment fa = (FileAttachment) a;
                            fa.load();
                            content = fa.getContent();
                            contentType = fa.getContentType();
                        }
                        EmailAttachment emailAttachment = new EmailAttachment(name, content, contentType);
                        emailAttachments.add(emailAttachment);
                    }

                }
            }
        } catch (Exception e) {
        }
        return emailAttachments;
    }

    protected List<EmailAttachment> extractInlineAttachments(Item nativeMessage) {
        List<EmailAttachment> emailAttachments = new ArrayList<>();
        try {
            if (nativeMessage.getHasAttachments()) {

                for (Attachment a : nativeMessage.getAttachments()) {
                    if (a.getIsInline()) {
                        String name = a.getName();
                        byte[] content = new byte[0];
                        String contentType = null;
                        if (a instanceof FileAttachment) {
                            FileAttachment fa = (FileAttachment) a;
                            fa.load();
                            content = fa.getContent();
                            contentType = fa.getContentType();
                        }
                        EmailAttachment emailAttachment = new EmailAttachment(name, content, contentType);
                        emailAttachments.add(emailAttachment);
                    }
                }
            }
        } catch (Exception e) {
        }
        return emailAttachments;
    }

    protected String extractParentFolder(Item nativeMessage) {
        try {
            FolderId folderId = nativeMessage.getParentFolderId();
            Folder folder = Folder.bind(this.service, folderId);
            return folder.getDisplayName();
        } catch (Exception e) {
            return null;
        }
    }

    protected Set<EmailFlag> extractFlags(Item nativeMessage) {
        microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage;
        emailMessage = ItemConverter(nativeMessage);
        if (emailMessage != null) {
            Set<EmailFlag> emailFlags = new HashSet<>();
            try {
                emailFlags.add(emailMessage.getIsRead() ? EmailFlag.READ : EmailFlag.UNREAD);
            } catch (ServiceLocalException e) {

                return Collections.emptySet();
            }
            return emailFlags;
        } else {
        }
        return Collections.emptySet();
    }

    private microsoft.exchange.webservices.data.core.service.item.EmailMessage ItemConverter(Item item) {
        if (item instanceof microsoft.exchange.webservices.data.core.service.item.EmailMessage) {
            return (microsoft.exchange.webservices.data.core.service.item.EmailMessage) item;
        } else {
        }
        return null;
    }

    private List<EmailAddress> extractEmailAddressCollection(EmailAddressCollection addressCollection) {
        return addressCollection.getItems().stream().map(it -> EmailAddress.of(it.getAddress(), it.getName())).collect(Collectors.toList());
    }
}