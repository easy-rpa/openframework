package eu.ibagroup.easyrpa.openframework.email.service;

/**
 * Provides list of support protocols for outbound email service. Also it helps to identify specific protocol that is
 * necessary to use for sending of email messages.
 */
public enum OutboundEmailProtocol {

    /**
     * Not encrypted SMTP protocol.
     * <p>
     * The port number used by default: <b>25</b>.
     */
    SMTP("smtp", "25"),

    /**
     * SMTP protocol encrypted using TLS (STARTTLS enabled).
     * <p>
     * The port number used by default: <b>587</b>.
     */
    SMTP_OVER_TLS("smtp", "587"),

    /**
     * SMTP protocol encrypted using SSL.
     * <p>
     * The port number used by default: <b>465</b>.
     */
    SMTPS("smtps", "465");

    /**
     * The name of this protocol.
     */
    private String protocolName;

    /**
     * The default port that is used for this protocol.
     */
    private String defaultPort;

    OutboundEmailProtocol(String protocolName, String defaultPort) {
        this.protocolName = protocolName;
        this.defaultPort = defaultPort;
    }

    /**
     * Gets default port that is used for this protocol.
     *
     * @return string with default port that is used for this protocol.
     */
    public String getDefaultPort() {
        return defaultPort;
    }

    /**
     * Gets the name of this protocol.
     *
     * @return string with name of this protocol.
     */
    public String getProtocolName() {
        return protocolName;
    }
}
