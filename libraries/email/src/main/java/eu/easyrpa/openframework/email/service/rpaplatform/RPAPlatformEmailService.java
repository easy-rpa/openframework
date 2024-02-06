package eu.easyrpa.openframework.email.service.rpaplatform;

import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.service.OutboundEmailService;

/**
 * Implementation of outbound email service that uses embedded into RPA platform functionality for
 * sending email messages.
 */
public class RPAPlatformEmailService implements OutboundEmailService {

    private final RPAServicesAccessor rpaServices;

    public RPAPlatformEmailService(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    @Override
    public void send(EmailMessage message) {
        if (message.getChannel() != null) {
            if (message.getTemplate() != null) {
                rpaServices.sendMessage(
                        message.getChannel(), message.getTemplate(),
                        message.getBodyProperties(), message.getAttachments()
                );
            } else {
                String content = message.hasHtml() ? message.getHtml() : message.getText();
                rpaServices.sendMessage(message.getChannel(), message.getSubject(), content, message.getAttachments());
            }
        }
    }
}
