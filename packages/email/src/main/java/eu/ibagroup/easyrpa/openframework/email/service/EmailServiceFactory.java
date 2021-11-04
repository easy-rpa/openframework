package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.service.javax.ImapPop3EmailService;
import eu.ibagroup.easyrpa.openframework.email.service.javax.SmtpEmailService;

public class EmailServiceFactory {

    private static EmailServiceFactory instance;

    private EmailServiceFactory() {
    }

    public static EmailServiceFactory getInstance() {
        if (instance == null) {
            instance = new EmailServiceFactory();
        }
        return instance;
    }

    public InboundEmailService getInboundService(String hostAndPort, InboundEmailProtocol protocol, String secret) {
        if (protocol != null) {
            return new ImapPop3EmailService(hostAndPort, protocol, secret);
        }
        return null;
    }

    public OutboundEmailService getOutboundService(String hostAndPort, OutboundEmailProtocol protocol, String secret) {
        if (protocol != null) {
            return new SmtpEmailService(hostAndPort, protocol, secret);
        }
        return null;
    }
}
