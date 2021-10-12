package eu.ibagroup.easyrpa.openframework.email.service.ews;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.EmailSender;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public class EwsEmailSender implements EmailSender {

    private MessageConverter<Item> messageConverter;

    public EwsEmailSender() {
        this.messageConverter = new EwsMessageConverter(new ExchangeService(ExchangeVersion.Exchange2010_SP2));
    }

    @Override
    public void sendEmail(EmailMessage message) throws EmailMessagingException {
        Item item = messageConverter.createNativeMessage(message);

        if (item instanceof microsoft.exchange.webservices.data.core.service.item.EmailMessage) {
            microsoft.exchange.webservices.data.core.service.item.EmailMessage msg = (microsoft.exchange.webservices.data.core.service.item.EmailMessage) item;

            try {
                msg.sendAndSaveCopy();
            } catch (Exception ex) {
                throw new EmailMessagingException("Could not sent email", ex);
            }
        } else {
            throw new EmailMessagingException("Could not create email");
        }
    }

    @Override
    public void sendEmail(final String from, final String to, final String subject, final String body) throws EmailMessagingException {
        EmailMessage message = EmailMessage.EmailMessageBuilder.newMessage(EmailAddress.of(from), EmailAddress.of(to)).text(body).subject(subject).build();

        sendEmail(message);
    }

    @Override
    public void sendEmail(String from, final List<String> to, final String subject, final String body) throws EmailMessagingException {
        List<EmailAddress> toEmailAddresses = to.stream().map(EmailAddress::of).collect(Collectors.toList());
        EmailMessage message = EmailMessage.EmailMessageBuilder.newMessage(EmailAddress.of(from), toEmailAddresses).text(body).subject(subject).build();

        sendEmail(message);
    }

}
