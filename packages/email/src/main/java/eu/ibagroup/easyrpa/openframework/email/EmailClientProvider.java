package eu.ibagroup.easyrpa.openframework.email;

import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.service.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.service.ews.EwsEmailsClient;
import eu.ibagroup.easyrpa.openframework.email.service.javax.ImapPop3EmailClient;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

public class EmailClientProvider {

    private static final String DEFAULT_EMAIL_TYPE_NAME = "email";

    private static final String DEFAULT_EMAIL_SERVICE_PROTOCOL = "IMAP";

    private static final String SERVICE_PROTOCOL_CFG_NAME_TPL = "%s_service_protocol";
    private static final String SERVICE_CFG_NAME_TPL = "%s_service";
    private static final String EXCHANGE_SERVER_CFG_NAME_TPL = "%_exchange_server";
    private static final String EXCHANGE_DOMAIN_CFG_NAME_TPL = "%_exchange_domain";
    private static final String EXCHANGE_VERSION_CFG_NAME_TPL = "%_exchange_version";

    private String typeName = DEFAULT_EMAIL_TYPE_NAME;

    private String emailService;
    private String emailServiceProtocol;

    private String emailExchangeServer;
    private String emailExchangeDomain;
    private String emailExchangeVersion;

    private RPAServicesAccessor cfg;

    private String userName;

    private String password;

    @Inject
    public EmailClientProvider(RPAServicesAccessor cfg, String userName, String password) {
        this.cfg = cfg;
        this.userName = userName;
        this.password = password;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        this.emailService = null;
        this.emailServiceProtocol = null;

        this.emailExchangeServer = null;
        this.emailExchangeDomain = null;
        this.emailExchangeVersion = null;
    }

    public EmailClient getClient() {

        String emailService = getEmailService();
        if (emailService == null) {
            throw new EmailMessagingException("Email service address is not defined.");
        }

        String[] hostAndPort = emailService.split(":");
        String host = hostAndPort[0];
        String port = hostAndPort.length > 1 ? hostAndPort[1] : "";

        EmailClient.SupportedProtocol protocol = EmailClient.SupportedProtocol.valueOf(getEmailServiceProtocol().toUpperCase());

        if (EmailClient.SupportedProtocol.EXCHANGE.equals(protocol)) {
            return createExchangeEmailClient();
        } else if (EmailClient.SupportedProtocol.POP3.equals(protocol) || EmailClient.SupportedProtocol.IMAP.equals(protocol)) {
            return new ImapPop3EmailClient(protocol, host, port, userName, password, true);
        } else {
            throw new EmailMessagingException("Unsupported protocol: " + protocol.getName());
        }
    }

    public String getEmailService() {
        if (emailService == null) {
            emailService = cfg.getConfigParam(String.format(SERVICE_CFG_NAME_TPL, typeName));
            if (emailService == null && !DEFAULT_EMAIL_TYPE_NAME.equals(typeName)) {
                emailService = cfg.getConfigParam(String.format(SERVICE_CFG_NAME_TPL, DEFAULT_EMAIL_TYPE_NAME));
            }
        }
        return emailService;
    }

    public void setEmailService(String emailServiceHostAndPort) {
        this.emailService = emailServiceHostAndPort;
    }

    public EmailClientProvider service(String emailServiceHostAndPort) {
        setEmailService(emailServiceHostAndPort);
        return this;
    }

    public String getEmailServiceProtocol() {
        if (emailServiceProtocol == null) {
            emailExchangeDomain = getParam(SERVICE_PROTOCOL_CFG_NAME_TPL);
            if (emailServiceProtocol == null) {
                emailServiceProtocol = DEFAULT_EMAIL_SERVICE_PROTOCOL;
            }
        }
        return emailServiceProtocol;
    }

    public void setEmailServiceProtocol(String emailServiceProtocol) {
        this.emailServiceProtocol = emailServiceProtocol;
    }

    public EmailClientProvider protocol(String emailServiceProtocol) {
        setEmailServiceProtocol(emailServiceProtocol);
        return this;
    }

    public String getEmailExchangeServer() {
        if (emailExchangeServer == null) {
            emailExchangeServer = getParam(EXCHANGE_SERVER_CFG_NAME_TPL);
        }
        return emailExchangeServer;
    }

    public void setEmailExchangeServer(String emailExchangeServer) {
        this.emailExchangeServer = emailExchangeServer;
    }

    public EmailClientProvider exchangeServer(String emailExchangeServer) {
        setEmailExchangeServer(emailExchangeServer);
        return this;
    }

    public String getEmailExchangeDomain() {
        if (emailExchangeDomain == null) {
            emailExchangeDomain = getParam(EXCHANGE_DOMAIN_CFG_NAME_TPL);
        }
        return emailExchangeDomain;
    }

    public void setEmailExchangeDomain(String emailExchangeDomain) {
        this.emailExchangeDomain = emailExchangeDomain;
    }

    public EmailClientProvider exchangeDomain(String emailExchangeDomain) {
        setEmailExchangeDomain(emailExchangeDomain);
        return this;
    }

    public String getEmailExchangeVersion() {
        if (emailExchangeVersion == null) {
            emailExchangeVersion = getParam(EXCHANGE_VERSION_CFG_NAME_TPL);
        }
        return emailExchangeVersion;
    }

    public void setEmailExchangeVersion(String emailExchangeVersion) {
        this.emailExchangeVersion = emailExchangeVersion;
    }

    public EmailClientProvider exchangeVersion(String emailExchangeVersion) {
        setEmailExchangeVersion(emailExchangeVersion);
        return this;
    }

    public RPAServicesAccessor getCfg() {
        return cfg;
    }

    private EmailClient createExchangeEmailClient() {
        String server = getEmailExchangeServer();
        String domain = getEmailExchangeDomain();
        ExchangeVersion exchangeVersion = ExchangeVersion.valueOf(getEmailExchangeVersion());

        if (StringUtils.isEmpty(domain)) {
            return new EwsEmailsClient(server, userName, password, exchangeVersion);
        } else {
            return new EwsEmailsClient(server, userName, password, domain, exchangeVersion);
        }
    }

    private String getParam(String template) {
        String result = null;

        if (cfg == null) {
            return null;
        }

        try {
            result = cfg.getConfigParam(String.format(template, typeName));
        } catch (Exception e) {
            //do nothing
        }

        if (result == null && !DEFAULT_EMAIL_TYPE_NAME.equals(typeName)) {
            try {
                result = cfg.getConfigParam(String.format(template, DEFAULT_EMAIL_TYPE_NAME));
            } catch (Exception e) {
                //do nothing
            }
        }
        return result;
    }
}
