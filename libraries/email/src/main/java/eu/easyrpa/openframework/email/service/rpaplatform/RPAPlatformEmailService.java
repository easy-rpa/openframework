package eu.easyrpa.openframework.email.service.rpaplatform;

import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.service.OutboundEmailService;

/**
 * Implementation of outbound email service that uses embedded into RPA platform functionality for
 * sending email messages.
 */
public class RPAPlatformEmailService implements OutboundEmailService {

    private String channel;

    private RPAServicesAccessor rpaServices;

    public RPAPlatformEmailService(String channel, RPAServicesAccessor rpaServices) {
        this.channel = channel;
        this.rpaServices = rpaServices;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public void send(EmailMessage message) {
        if (message.getTemplate() != null) {
            rpaServices.sendMessage(channel, message.getTemplate(), message.getBodyProperties(), message.getAttachments());
        } else {
            String content = message.hasHtml() ? message.getHtml() : message.getText();
            rpaServices.sendMessage(channel, message.getSubject(), content, message.getAttachments());
        }
    }
}
