package eu.ibagroup.easyrpa.openframework.email.service;

public enum InboundEmailProtocol {

    POP3("pop3", "110"),
    POP3_OVER_TSL("pop3", "110"),
    POP3S("pop3s", "995"),
    IMAP("imap", "143"),
    IMAP_OVER_TSL("imap", "143"),
    IMAPS("imaps", "993");

    private String protocolName;

    private String defaultPort;

    InboundEmailProtocol(String protocolName, String defaultPort) {
        this.protocolName = protocolName;
        this.defaultPort = defaultPort;
    }

    public String getDefaultPort() {
        return defaultPort;
    }

    public String getProtocolName() {
        return protocolName;
    }
}
