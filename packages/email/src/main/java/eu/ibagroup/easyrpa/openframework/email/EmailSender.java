package eu.ibagroup.easyrpa.openframework.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ibagroup.easyrpa.openframework.core.model.RPASecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.service.EmailConfigParam;
import eu.ibagroup.easyrpa.openframework.email.service.EmailServiceFactory;
import eu.ibagroup.easyrpa.openframework.email.service.OutboundEmailProtocol;
import eu.ibagroup.easyrpa.openframework.email.service.OutboundEmailService;

import javax.inject.Inject;

public class EmailSender {

    private static final OutboundEmailProtocol DEFAULT_OUTBOUND_EMAIL_PROTOCOL = OutboundEmailProtocol.SMTP;

    private RPAServicesAccessor rpaServices;

    private String server;
    private OutboundEmailProtocol protocol;
    private String secret;

    private OutboundEmailService service;

    public EmailSender() {
    }

    @Inject
    public EmailSender(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    public String getServer() {
        if (server == null) {
            server = getConfigParam(EmailConfigParam.OUTBOUND_EMAIL_SERVER);
        }
        return server;
    }

    public void setServer(String emailServerHostAndPort) {
        this.server = emailServerHostAndPort;
        this.service = null;
    }

    public EmailSender server(String emailServerHostAndPort) {
        setServer(emailServerHostAndPort);
        return this;
    }

    public OutboundEmailProtocol getProtocol() {
        if (protocol == null) {
            String protocolStr = getConfigParam(EmailConfigParam.OUTBOUND_EMAIL_PROTOCOL);
            protocol = protocolStr != null ? OutboundEmailProtocol.valueOf(protocolStr.toUpperCase()) : DEFAULT_OUTBOUND_EMAIL_PROTOCOL;
        }
        return protocol;
    }

    public void setProtocol(OutboundEmailProtocol protocol) {
        this.protocol = protocol;
        this.service = null;
    }

    public EmailSender protocol(OutboundEmailProtocol protocol) {
        setProtocol(protocol);
        return this;
    }

    public EmailSender protocol(String protocol) {
        setProtocol(OutboundEmailProtocol.valueOf(protocol.toUpperCase()));
        return this;
    }

    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(EmailConfigParam.OUTBOUND_EMAIL_SECRET);
            if (secretAlias != null) {
                secret = rpaServices.getSecret(secretAlias, String.class);
            }
        }
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
        this.service = null;
    }

    public EmailSender secret(String secret) {
        setSecret(secret);
        return this;
    }

    public EmailSender secret(String userName, String password) {
        try {
            setSecret(new ObjectMapper().writeValueAsString(new RPASecretCredentials(userName, password)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void send(EmailMessage message) {
        message.send(this);
    }

    protected void sendMessage(EmailMessage message) {
        initService();
        message.beforeSend();
        this.service.send(message);
    }

    protected void initService() {
        if (this.service == null) {
            this.service = EmailServiceFactory.getInstance().getOutboundService(getServer(), getProtocol(), getSecret());
        }
    }

    protected String getConfigParam(String key) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getConfigParam(key);
        } catch (Exception e) {
            //do nothing
        }

        return result;
    }

}
