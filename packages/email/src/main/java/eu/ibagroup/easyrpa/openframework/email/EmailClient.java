package eu.ibagroup.easyrpa.openframework.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ibagroup.easyrpa.openframework.core.model.RPASecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.service.EmailConfigParam;
import eu.ibagroup.easyrpa.openframework.email.service.EmailServiceFactory;
import eu.ibagroup.easyrpa.openframework.email.service.InboundEmailProtocol;
import eu.ibagroup.easyrpa.openframework.email.service.InboundEmailService;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class EmailClient {

    private static final String DEFAULT_INBOX_FOLDER_NAME = "INBOX";
    private static final Duration DEFAULT_CHECK_TIMEOUT = Duration.ofHours(1);
    private static final Duration DEFAULT_CHECK_INTERVAL = Duration.ofMinutes(1);

    private static final InboundEmailProtocol DEFAULT_INBOUND_EMAIL_PROTOCOL = InboundEmailProtocol.IMAP;

    private RPAServicesAccessor rpaServices;

    private String server;
    private InboundEmailProtocol protocol;
    private String secret;

    private InboundEmailService service;

    private String defaultFolder;

    public EmailClient() {
    }

    @Inject
    public EmailClient(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
    }

    public String getServer() {
        if (server == null) {
            server = getConfigParam(EmailConfigParam.INBOUND_EMAIL_SERVER);
        }
        return server;
    }

    public void setServer(String emailServerHostAndPort) {
        this.server = emailServerHostAndPort;
        this.service = null;
    }

    public EmailClient server(String emailServerHostAndPort) {
        setServer(emailServerHostAndPort);
        return this;
    }

    public InboundEmailProtocol getProtocol() {
        if (protocol == null) {
            String protocolStr = getConfigParam(EmailConfigParam.INBOUND_EMAIL_PROTOCOL);
            protocol = protocolStr != null ? InboundEmailProtocol.valueOf(protocolStr.toUpperCase()) : DEFAULT_INBOUND_EMAIL_PROTOCOL;
        }
        return protocol;
    }

    public void setProtocol(InboundEmailProtocol protocol) {
        this.protocol = protocol;
        this.service = null;
    }

    public EmailClient protocol(InboundEmailProtocol protocol) {
        setProtocol(protocol);
        return this;
    }

    public EmailClient protocol(String protocol) {
        setProtocol(InboundEmailProtocol.valueOf(protocol.toUpperCase()));
        return this;
    }

    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(EmailConfigParam.INBOUND_EMAIL_SECRET);
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

    public EmailClient secret(String secret) {
        setSecret(secret);
        return this;
    }

    public EmailClient secret(String userName, String password) {
        try {
            setSecret(new ObjectMapper().writeValueAsString(new RPASecretCredentials(userName, password)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public String getDefaultFolder() {
        if (defaultFolder == null) {
            String folderName = getConfigParam(EmailConfigParam.MAILBOX_DEFAULT_FOLDER);
            defaultFolder = folderName != null ? folderName : DEFAULT_INBOX_FOLDER_NAME;
        }
        return defaultFolder;
    }

    public void setDefaultFolder(String defaultFolder) {
        this.defaultFolder = defaultFolder;
    }

    public EmailClient defaultFolder(String defaultFolder) {
        setDefaultFolder(defaultFolder);
        return this;
    }

    public int getMessageCount() {
        initService();
        return getMessageCount(getDefaultFolder());
    }

    public int getMessageCount(String folderName) {
        initService();
        return this.service.getMessageCount(folderName);
    }

    public EmailMessage fetchMessage(String messageId) {
        initService();
        return this.service.fetchMessage(messageId);
    }

    public List<EmailMessage> fetchMessages() {
        initService();
        return this.service.fetchMessages(getDefaultFolder(), null);
    }

    public List<EmailMessage> fetchMessages(String folderName) {
        initService();
        return this.service.fetchMessages(folderName, null);
    }

    public List<EmailMessage> fetchMessages(Predicate<EmailMessage> isSatisfy) {
        initService();
        return this.service.fetchMessages(getDefaultFolder(), isSatisfy);
    }

    public List<EmailMessage> fetchMessages(String folderName, Predicate<EmailMessage> isSatisfy) {
        initService();
        return this.service.fetchMessages(folderName, isSatisfy);
    }

    public List<EmailMessage> fetchAllMessages() {
        initService();
        return this.service.fetchAllMessages(null);
    }

    public List<EmailMessage> fetchAllMessages(Predicate<EmailMessage> isSatisfy) {
        initService();
        return this.service.fetchAllMessages(isSatisfy);
    }

    public List<EmailMessage> fetchUnreadMessages(boolean markRead) {
        initService();
        return this.service.fetchUnreadMessages(getDefaultFolder(), markRead);
    }

    public List<EmailMessage> fetchUnreadMessages(String folderName, boolean markRead) {
        initService();
        return this.service.fetchUnreadMessages(folderName, markRead);
    }

    public CompletableFuture<List<EmailMessage>> waitMessages(Predicate<EmailMessage> isSatisfy) {
        initService();
        return this.service.waitMessages(getDefaultFolder(), isSatisfy, DEFAULT_CHECK_TIMEOUT, DEFAULT_CHECK_INTERVAL);
    }

    public CompletableFuture<List<EmailMessage>> waitMessages(Predicate<EmailMessage> isSatisfy, Duration timeout) {
        initService();
        return this.service.waitMessages(getDefaultFolder(), isSatisfy, timeout, DEFAULT_CHECK_INTERVAL);
    }

    public CompletableFuture<List<EmailMessage>> waitMessages(Predicate<EmailMessage> isSatisfy, Duration timeout, Duration checkInterval) {
        initService();
        return this.service.waitMessages(getDefaultFolder(), isSatisfy, timeout, checkInterval);
    }

    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, Predicate<EmailMessage> isSatisfy) {
        initService();
        return this.service.waitMessages(folderName, isSatisfy, DEFAULT_CHECK_TIMEOUT, DEFAULT_CHECK_INTERVAL);
    }

    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, Predicate<EmailMessage> isSatisfy, Duration timeout) {
        initService();
        return this.service.waitMessages(folderName, isSatisfy, timeout, DEFAULT_CHECK_INTERVAL);
    }

    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, Predicate<EmailMessage> isSatisfy, Duration timeout, Duration checkInterval) {
        initService();
        return this.service.waitMessages(folderName, isSatisfy, timeout, checkInterval);
    }

    public EmailMessage copyMessage(EmailMessage message, String targetFolder) {
        initService();
        return this.service.copyMessage(message, targetFolder);
    }

    public EmailMessage moveMessage(EmailMessage message, String targetFolder) {
        initService();
        return this.service.moveMessage(message, targetFolder);
    }

    public void updateMessage(EmailMessage message) {
        initService();
        this.service.updateMessage(message);
    }

    public void updateMessages(List<EmailMessage> messages) {
        initService();
        this.service.updateMessages(messages);
    }

    public void deleteMessage(EmailMessage message) {
        initService();
        this.service.deleteMessage(message);
    }

    public void deleteMessages(List<EmailMessage> messages) {
        initService();
        this.service.deleteMessages(messages);
    }

    public List<String> listFolders() {
        initService();
        return this.service.listFolders();
    }

    public boolean createFolder(String folderName) {
        initService();
        return this.service.createFolder(folderName);
    }

    public boolean renameFolder(String folderName, String newFolderName) {
        initService();
        return this.service.renameFolder(folderName, newFolderName);
    }

    public boolean deleteFolder(String folderName) {
        initService();
        return this.service.deleteFolder(folderName);
    }

    protected void initService() {
        if (this.service == null) {
            this.service = EmailServiceFactory.getInstance().getInboundService(getServer(), getProtocol(), getSecret());
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
