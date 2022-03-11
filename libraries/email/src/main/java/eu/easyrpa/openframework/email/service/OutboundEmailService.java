package eu.easyrpa.openframework.email.service;

import eu.easyrpa.openframework.email.EmailMessage;

/**
 * Single interface for all outbound email services that provides functionality for sending of email messages based
 * on specific outbound email protocols.
 */
public interface OutboundEmailService {

    /**
     * Sends given email message.
     *
     * @param message the email message to send.
     */
    void send(EmailMessage message);
}