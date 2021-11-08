package eu.ibagroup.easyrpa.openframework.email.service;

public enum OutboundEmailProtocol {
    SMTP("smtp", "25"),
    SMTP_OVER_TSL("smtp", "587"),
    SMTPS("smtps", "465");

    private String protocolName;

    private String defaultPort;

    OutboundEmailProtocol(String protocolName, String defaultPort) {
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
