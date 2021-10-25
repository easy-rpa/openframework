package eu.ibagroup.easyrpa.openframework.email.service.javax;

import com.sun.mail.imap.IMAPFolder;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ImapPop3EmailClient implements EmailClient {
    public static String DEFAULT_INBOX_FOLDER_NAME = "INBOX";

    private final ImapPop3EmailClient.SupportedProtocol protocol;

    private final String host;

    private final String port;

    private final String user;

    private final String password;

    private final boolean ssl;

    private Store store;

    private Session session;

    private MessageConverter<Message> messageConverter;

    private String defaultInboxFolder;

    public ImapPop3EmailClient(ImapPop3EmailClient.SupportedProtocol protocol, String host, String port, String user, String password, boolean ssl) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.ssl = ssl;
        this.defaultInboxFolder = DEFAULT_INBOX_FOLDER_NAME;
        this.protocol = protocol;
        if (ImapPop3EmailClient.SupportedProtocol.IMAP.equals(protocol)) {
            this.initImapStore();
        } else {
            this.initPop3Store();
        }

    }

    private void initImapStore() {
        Properties props = System.getProperties();
        String protocolName = this.ssl ? "imaps" : "imap";
        props.setProperty("mail.store.protocol", protocolName);
        props.put("mail." + protocolName + ".host", this.getHost());
        props.put("mail." + protocolName + ".port", this.getPort());
        props.put("mail." + protocolName + ".fetchsize", "5000000");
        this.initStore(props, protocolName);
    }

    private void initPop3Store() {
        Properties props = System.getProperties();
        String protocolName = "pop3";
        props.setProperty("mail.store.protocol", protocolName);
        props.put("mail." + protocolName + ".host", this.getHost());
        props.put("mail." + protocolName + ".port", this.getPort());
        if (this.ssl) {
            props.setProperty(String.format("mail.%s.socketFactory.class", protocolName), "javax.net.ssl.SSLSocketFactory");
            props.setProperty(String.format("mail.%s.socketFactory.fallback", protocolName), "false");
            props.setProperty(String.format("mail.%s.socketFactory.port", protocolName), String.valueOf(this.port));
        }

        this.initStore(props, protocolName);
    }

    private void initStore(Properties props, String protocolName) {
        this.session = Session.getDefaultInstance(props, (Authenticator) null);
        this.messageConverter = new JavaxMimeMessageConverter(this.session);

        try {
            this.store = this.session.getStore(protocolName);
        } catch (NoSuchProviderException var4) {
            throw new EmailMessagingException(var4);
        }
    }

    @Override
    public List<EmailMessage> fetchMessages(String folderName, Predicate<EmailMessage> isSatisfy) throws EmailMessagingException {
        //TODO Implement this
        return null;
    }

    public List<EmailMessage> fetchAllMessages(String folderName) {
        return this.connectAndFetchMessages(() -> {
            Folder folder = this.openFolder(folderName, 1);
            Message[] messages = folder.getMessages();
            List<EmailMessage> emailMessages = this.messageConverter.convertAllToEmailMessage(messages);
            folder.close(false);
            return emailMessages;
        });
    }

    private List<EmailMessage> connectAndFetchMessages(ImapPop3EmailClient.MessageSupplier supplier) {
        if (this.connect()) {
            List<EmailMessage> var2;
            try {
                var2 = Collections.unmodifiableList(supplier.fetch());
            } catch (MessagingException var6) {
                throw new EmailMessagingException(var6);
            } finally {
                this.disconnect();
            }

            return var2;
        } else {
            throw new EmailMessagingException("Unable to connect");
        }
    }

    private Folder openFolder(String folderName, int mode) throws MessagingException {
        Folder folder = this.store.getFolder(folderName);
        if (folder.exists()) {
            folder.open(mode);
            return folder;
        } else {
            throw new EmailMessagingException("Folder \"" + folderName + "\" doesn't exist.");
        }
    }

    public List<EmailMessage> fetchUnreadMessages(String folderName, boolean markRead) {
        return this.connectAndFetchMessages(() -> {
            int accessMode = markRead ? 2 : 1;
            Folder folder = this.openFolder(folderName, accessMode);
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm term = new FlagTerm(seen, false);
            Message[] messages = folder.search(term);
            if (markRead) {
                folder.setFlags(messages, seen, true);
            }

            List<EmailMessage> emailMessages = this.messageConverter.convertAllToEmailMessage(messages);
            folder.close(markRead);
            return emailMessages;
        });
    }

    public List<EmailMessage> searchMessages(String folderName, SearchTerm term) {
        return this.connectAndFetchMessages(() -> {
            Folder folder = this.openFolder(folderName, 1);
            Message[] messages = folder.search(term);
            List<EmailMessage> emailMessages = this.messageConverter.convertAllToEmailMessage(messages);
            folder.close(false);
            return emailMessages;
        });
    }

    public List<String> fetchFolderList() {
        List<String> folderNames;
        try {
            this.connect();
            Folder[] folders = this.store.getDefaultFolder().list("*");
            folderNames = Arrays.stream(folders).map(Folder::getFullName).collect(Collectors.toList());
        } catch (MessagingException var6) {
            throw new EmailMessagingException(var6);
        } finally {
            this.disconnect();
        }

        return Collections.unmodifiableList(folderNames);
    }

    public boolean createFolder(String folderName) {
        if (this.connect()) {
            try {
                Folder folder = this.store.getFolder(folderName);
                return folder.create(1);
            } catch (MessagingException var3) {
                throw new EmailMessagingException(var3);
            }
        } else {
            throw new EmailMessagingException("Unable to connect.");
        }
    }

    public EmailMessage putMessage(EmailMessage message, String targetFolder) {
        if (!this.connect()) {
            throw new EmailMessagingException("Unable to connect");
        } else {
            EmailMessage var7;
            try {
                EmailMessage savedEmailMessage = null;
                IMAPFolder destination = (IMAPFolder) this.openFolder(targetFolder, 2);
                Message nativeMessage = (Message) this.messageConverter.createNativeMessage(message);
                Message[] created = destination.addMessages(new Message[] { nativeMessage });
                if (created.length != 1) {
                    String m = "Wrong saved message count: " + created.length + ". " + (created.length == 0 ? "No messages saved." : "To many messages saved.");
                    throw new EmailMessagingException(m);
                }

                savedEmailMessage = this.messageConverter.convertToEmailMessage(created[0]);
                destination.close(true);
                var7 = savedEmailMessage;
            } catch (MessagingException var11) {
                throw new EmailMessagingException(var11);
            } finally {
                this.disconnect();
            }

            return var7;
        }
    }

    public EmailMessage copyMessage(EmailMessage message, String targetFolder) {
        return (EmailMessage) this.connectAndModifyMessage(message, (m) -> {
            EmailMessage copy = null;
            IMAPFolder destination = (IMAPFolder) this.openFolder(targetFolder, 2);
            Message[] created = destination.addMessages(new Message[] { m });
            if (created.length == 1) {
                copy = this.messageConverter.convertToEmailMessage(created[0]);
                return copy;
            } else {
                String messageText = "Wrong saved message count: " + created.length + ". " + (created.length == 0 ? "No messages saved." : "To many messages saved.");
                throw new EmailMessagingException(messageText);
            }
        });
    }

    public boolean removeMessage(EmailMessage message) {
        return (Boolean) this.connectAndModifyMessage(message, (m) -> {
            Folder f = m.getFolder();
            m.setFlag(Flags.Flag.DELETED, true);
            Message[] deleted = f.expunge();
            return deleted.length == 1;
        });
    }

    public boolean markRead(EmailMessage message) {
        return (Boolean) this.connectAndModifyMessage(message, (m) -> {
            m.setFlag(Flags.Flag.SEEN, true);
            return true;
        });
    }

    public EmailMessage moveMessage(EmailMessage message, String targetFolder) {
        EmailMessage moved = this.copyMessage(message, targetFolder);
        if (this.removeMessage(message)) {
            return moved;
        } else {
            throw new EmailMessagingException("Unable to remove original message after copy: \n" + message);
        }
    }

    private <T> T connectAndModifyMessage(EmailMessage message, MessageModifier<T> modifier) {
        if (message.getParentFolder().isPresent()) {
            if (message.getId().isPresent()) {
                if (this.connect()) {
                    T var6;
                    try {
                        Folder folder = this.openFolder((String) message.getParentFolder().get(), 2);
                        Message nativeMessage = this.findMessageById(folder, (String) message.getId().get());
                        T result = modifier.modify(nativeMessage);
                        folder.close(true);
                        var6 = result;
                    } catch (MessagingException var10) {
                        throw new EmailMessagingException(var10);
                    } finally {
                        this.disconnect();
                    }

                    return var6;
                } else {
                    throw new EmailMessagingException("Unable to connect");
                }
            } else {
                throw new EmailMessagingException("Message doesn't have an id: " + message);
            }
        } else {
            throw new EmailMessagingException("Message doesn't have a folder: " + message);
        }
    }

    private Message findMessageById(Folder folder, String id) throws MessagingException {
        UIDFolder uidFolder = (UIDFolder) folder;

        long uid;
        try {
            uid = Long.parseLong(id);
        } catch (NumberFormatException var7) {
            throw new EmailMessagingException("Cannot parse uid \"" + id + "\"", var7);
        }

        return uidFolder.getMessageByUID(uid);
    }

    private boolean connect() {
        try {
            if (!this.store.isConnected()) {
                this.store.connect(this.getUser(), this.getPassword());
            }
        } catch (MessagingException e) {
            throw new EmailMessagingException("Unable to connect", e);
        }

        return this.store.isConnected();
    }

    private boolean disconnect() {
        try {
            if (this.store.isConnected()) {
                this.store.close();
            }
        } catch (MessagingException var2) {
        }

        return !this.store.isConnected();
    }

    public String getHost() {
        return this.host;
    }

    public String getPort() {
        return this.port;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public SupportedProtocol getProtocol() {
        return this.protocol;
    }

    public String getDefaultInboxFolder() {
        return this.defaultInboxFolder;
    }

    public void setDefaultInboxFolder(String defaultInboxFolder) {
        this.defaultInboxFolder = defaultInboxFolder;
    }

    @FunctionalInterface
    private interface MessageModifier<T> {
        T modify(Message var1) throws MessagingException;
    }

    @FunctionalInterface
    private interface MessageSupplier {
        List<EmailMessage> fetch() throws MessagingException;
    }
}
