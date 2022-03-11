package eu.easyrpa.openframework.email.service;

/**
 * Provides list of support protocols for inbound email service. Also it helps to identify specific protocol that is
 * necessary to use for working with mailbox.
 */
public enum InboundEmailProtocol {

    /**
     * Not encrypted POP3 protocol.
     * <p>
     * The port number used by default: <b>110</b>.
     */
    POP3("pop3", "110"),

    /**
     * POP3 protocol encrypted using TLS.
     * <p>
     * The port number used by default: <b>995</b>.
     */
    POP3_OVER_TLS("pop3", "995"),

    /**
     * POP3 protocol encrypted using SSL.
     * <p>
     * The port number used by default: <b>995</b>.
     */
    POP3S("pop3s", "995"),

    /**
     * Not encrypted IMAP protocol.
     * <p>
     * The port number used by default: <b>143</b>.
     */
    IMAP("imap", "143"),

    /**
     * IMAP protocol encrypted using TLS.
     * <p>
     * The port number used by default: <b>993</b>.
     */
    IMAP_OVER_TLS("imap", "993"),

    /**
     * IMAP protocol encrypted using SSL.
     * <p>
     * The port number used by default: <b>993</b>.
     */
    IMAPS("imaps", "993");

    /**
     * The name of this protocol.
     */
    private String protocolName;

    /**
     * The default port that is used for this protocol.
     */
    private String defaultPort;

    InboundEmailProtocol(String protocolName, String defaultPort) {
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
