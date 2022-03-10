package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.service.javax.ImapPop3EmailService;
import eu.ibagroup.easyrpa.openframework.email.service.javax.SmtpEmailService;

/**
 * Singleton implementation of factory to build inbound/outbound email services intended to work with specific
 * inbound/outbound email protocols.
 */
public class EmailServiceFactory {

    /**
     * Singleton instance of this factory.
     */
    private static EmailServiceFactory instance;

    /**
     * Constructs a new instance of this factory.
     * <p>
     * FOR INTERNAL USE ONLY.
     */
    private EmailServiceFactory() {
    }

    /**
     * Gets the singleton instance of this factory.
     *
     * @return singleton instance of this factory.
     */
    public static EmailServiceFactory getInstance() {
        if (instance == null) {
            instance = new EmailServiceFactory();
        }
        return instance;
    }

    /**
     * Creates a new instance of specific inbound email service that is intended to work with given email server
     * based on given protocol.
     *
     * @param hostAndPort the host name or IP-address of inbound email server. The port number also can be specified
     *                    here if it's different from default.The default port number is depends on specified
     *                    {@code protocol}.
     * @param protocol    the {@link InboundEmailProtocol} representing supported by this email library protocol that
     *                    is necessary to use for interaction with inbound email server.
     * @param secret      the secret information necessary for authentication on inbound email server. Usually it's a
     *                    JSON string with username and password.
     * @return a new instance of specific inbound email service that depends on given protocol.
     */
    public InboundEmailService getInboundService(String hostAndPort, InboundEmailProtocol protocol, String secret) {
        if (protocol != null) {
            return new ImapPop3EmailService(hostAndPort, protocol, secret);
        }
        return null;
    }

    /**
     * Creates a new instance of specific outbound email service that is intended to work with given email server
     * based on given protocol.
     *
     * @param hostAndPort the host name or IP-address of outbound email server. The port number also can be specified
     *                    here if it's different from default. The default port number is depends on specified
     *                    {@code protocol}.
     * @param protocol    the {@link OutboundEmailProtocol} representing supported by this email library protocol that
     *                    is necessary to use for interaction with outbound email server.
     * @param secret      the secret information necessary for authentication on outbound email server. Usually it's a
     *                    JSON string with username and password.
     * @return a new instance of specific outbound email service that depends on given protocol.
     */
    public OutboundEmailService getOutboundService(String hostAndPort, OutboundEmailProtocol protocol, String secret) {
        if (protocol != null) {
            return new SmtpEmailService(hostAndPort, protocol, secret);
        }
        return null;
    }
}
