package eu.easyrpa.openframework.email.service.javax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.imap.IMAPFolder;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.easyrpa.openframework.email.search.SearchQuery;
import eu.easyrpa.openframework.email.service.EmailServiceSecret;
import eu.easyrpa.openframework.email.service.InboundEmailProtocol;
import eu.easyrpa.openframework.email.service.InboundEmailService;
import eu.easyrpa.openframework.email.service.MessageConverter;

import javax.mail.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of inbound email services that is working based on IMAP and POP3 protocols.
 */
public class ImapPop3EmailService implements InboundEmailService {

    private static final int DEFAULT_BATCH_SIZE = 10;

    private final String host;

    private final String port;

    private final InboundEmailProtocol protocol;

    private final String user;

    private final String password;

    private Store store;

    private Session session;

    private MessageConverter<Message> messageConverter;

    private SearchTermConverter searchTermConverter;

    private int batchSize = DEFAULT_BATCH_SIZE;

    public ImapPop3EmailService(String server, InboundEmailProtocol protocol, String secret) {

        if (server.contains(":")) {
            String[] parts = server.split(":");
            this.host = parts[0];
            this.port = parts[1];
        } else {
            this.host = server;
            this.port = protocol.getDefaultPort();
        }

        this.protocol = protocol;

        try {
            EmailServiceSecret credentials = new ObjectMapper().readValue(secret, EmailServiceSecret.class);
            user = credentials.getUser();
            password = credentials.getPassword();
        } catch (JsonProcessingException e) {
            throw new EmailMessagingException(e);
        }

        this.session = Session.getInstance(getConfigurationFor(protocol), null);

        this.messageConverter = new MimeMessageConverter(this.session);
        this.searchTermConverter = new SearchTermConverter(this.messageConverter);

        try {
            this.store = this.session.getStore(protocol.getProtocolName());
        } catch (NoSuchProviderException e) {
            throw new EmailMessagingException(e);
        }
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public InboundEmailProtocol getProtocol() {
        return protocol;
    }

    public Session getSession() {
        return session;
    }

    public Store getStore() {
        return store;
    }

    public MessageConverter<Message> getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter<Message> messageConverter) {
        this.messageConverter = messageConverter;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public int getMessageCount(String folderName) {
        return openFolderAndPerform(folderName, Folder.READ_ONLY, folder -> {
            try {
                return folder.getMessageCount();
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public EmailMessage getMessage(String messageId) {
        long uid = Long.parseLong(messageId);
        return walkOverAllFolders(folder -> {
            try {
                UIDFolder uidFolder = (UIDFolder) folder;
                Message message = uidFolder.getMessageByUID(uid);
                if (message != null) {
                    return messageConverter.convertToEmailMessage(message);
                }
                return null;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public List<EmailMessage> fetchMessages(String folderName, SearchQuery searchQuery) {
        Function<Folder, List<EmailMessage>> searchAction = folder -> {
            try {
                Message[] messages;
                if (searchQuery != null) {
                    messages = folder.search(searchTermConverter.convert(searchQuery));
                } else {
                    messages = folder.getMessages();
                }
                return messageConverter.convertAllToEmailMessages(messages);
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        };

        if (folderName != null) {
            return openFolderAndPerform(folderName, Folder.READ_ONLY, searchAction);
        }

        List<EmailMessage> result = new ArrayList<>();
        walkOverAllFolders(folder -> {
            result.addAll(searchAction.apply(folder));
            return false;
        });
        return result;
    }

    @Override
    public CompletableFuture<List<EmailMessage>> waitMessages(String folderName, SearchQuery searchQuery,
                                                              Duration timeout, Duration checkInterval) {
        if (timeout == null) {
            throw new IllegalArgumentException("Timeout must be specified.");
        }
        if (checkInterval == null) {
            throw new IllegalArgumentException("Check interval must be specified.");
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                long endTime = System.currentTimeMillis() + timeout.toMillis();

                List<EmailMessage> result = fetchMessages(folderName, searchQuery);
                if (!result.isEmpty()) {
                    return result;
                }

                SearchQuery query = searchQuery != null
                        ? searchQuery.and().date().after(new Date())
                        : SearchQuery.date().after(new Date());

                ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
                ScheduledFuture<?> scheduledFuture = ex.scheduleAtFixedRate(() -> {
                    if (result.isEmpty()) {
                        result.addAll(fetchMessages(folderName, query));
                    }
                }, 0, checkInterval.toMillis(), TimeUnit.MILLISECONDS);

                while (true) {
                    Thread.sleep(1000);
                    if (System.currentTimeMillis() > endTime || !result.isEmpty()) {
                        scheduledFuture.cancel(true);
                        ex.shutdown();
                        return result;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public EmailMessage copyMessage(EmailMessage message, String targetFolder) {
        return findMessageAndPerform(message, Folder.READ_ONLY, msg -> {
            try {
                IMAPFolder target = (IMAPFolder) msg.getFolder().getStore().getFolder(targetFolder);
                if (target.exists()) {
                    target.open(Folder.READ_WRITE);
                    try {
                        Message[] copied = target.addMessages(new Message[]{msg});
                        if (copied.length == 1) {
                            return messageConverter.convertToEmailMessage(copied[0]);
                        }
                        return null;
                    } finally {
                        target.close(false);
                    }
                } else {
                    throw new EmailMessagingException(String.format("Target folder '%s' does not exist.", targetFolder));
                }
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public EmailMessage moveMessage(EmailMessage message, String targetFolder) {
        return findMessageAndPerform(message, Folder.READ_WRITE, msg -> {
            try {
                IMAPFolder target = (IMAPFolder) msg.getFolder().getStore().getFolder(targetFolder);
                if (target.exists()) {
                    target.open(Folder.READ_WRITE);
                    try {
                        Message[] moved = target.addMessages(new Message[]{msg});
                        if (moved.length == 1) {
                            msg.setFlag(Flags.Flag.DELETED, true);
                            msg.getFolder().expunge();
                            return messageConverter.convertToEmailMessage(moved[0]);
                        }
                        return null;
                    } finally {
                        target.close(false);
                    }
                } else {
                    throw new EmailMessagingException(String.format("Target folder '%s' does not exist.", targetFolder));
                }
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public void updateMessage(EmailMessage message) {
        findMessageAndPerform(message, Folder.READ_WRITE, msg -> {
            try {
                msg.setFlag(Flags.Flag.SEEN, message.isRead());
                return true;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public void updateMessages(List<EmailMessage> messages) {
        findAllMessagesAndPerform(messages, (message, msg) -> {
            try {
                msg.setFlag(Flags.Flag.SEEN, message.isRead());
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
            return false;
        });
    }

    @Override
    public void deleteMessage(EmailMessage message) {
        findMessageAndPerform(message, Folder.READ_WRITE, msg -> {
            try {
                Folder folder = msg.getFolder();
                msg.setFlag(Flags.Flag.DELETED, true);
                folder.expunge();
                return true;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public void deleteMessages(List<EmailMessage> messages) {
        findAllMessagesAndPerform(messages, (message, msg) -> {
            try {
                msg.setFlag(Flags.Flag.DELETED, true);
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
            return false;
        });
    }

    @Override
    public List<String> listFolders() {
        return connectAndPerform(store -> {
            try {
                Folder[] folders = store.getDefaultFolder().list("*");
                return Arrays.stream(folders).map(Folder::getFullName).collect(Collectors.toList());
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public boolean createFolder(String folderName) {
        return connectAndPerform(store -> {
            try {
                Folder folder = store.getFolder(folderName);
                if (!folder.exists()) {
                    return folder.create(Folder.HOLDS_MESSAGES);
                }
                return false;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public boolean renameFolder(String folderName, String newFolderName) {
        return connectAndPerform(store -> {
            try {
                Folder folder = store.getFolder(folderName);
                Folder newFolder = store.getFolder(newFolderName);
                if (folder.exists() && !newFolder.exists()) {
                    return folder.renameTo(newFolder);
                }
                return false;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    @Override
    public boolean deleteFolder(String folderName) {
        return connectAndPerform(store -> {
            try {
                Folder folder = store.getFolder(folderName);
                if (folder.exists()) {
                    return folder.delete(true);
                }
                return false;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    private void findAllMessagesAndPerform(List<EmailMessage> messages, BiFunction<EmailMessage, Message, Boolean> action) {

        Map<String, List<EmailMessage>> groupedMessages = messages.stream()
                .filter(m -> m.getParentFolder() != null && m.getId() != null)
                .collect(Collectors.groupingBy(EmailMessage::getParentFolder));

        for (String folderName : groupedMessages.keySet()) {
            List<EmailMessage> msgGroup = groupedMessages.get(folderName);
            boolean res = openFolderAndPerform(folderName, Folder.READ_WRITE, folder -> {
                try {
                    UIDFolder uidFolder = (UIDFolder) folder;
                    for (EmailMessage msg : msgGroup) {
                        Message m = uidFolder.getMessageByUID(Long.parseLong(msg.getId()));
                        if (m != null) {
                            if (action.apply(msg, m)) {
                                return true;
                            }
                        }
                    }
                    return false;
                } catch (MessagingException e) {
                    throw new EmailMessagingException(e);
                }
            });
            if (res) {
                return;
            }
        }
    }

    private <T> T findMessageAndPerform(EmailMessage message, int mode, Function<Message, T> action) {
        if (message.getParentFolder() == null) {
            throw new EmailMessagingException("Message does not have a folder specified.");
        }
        if (message.getId() == null) {
            throw new EmailMessagingException("Message does not have a id specified.");
        }
        return openFolderAndPerform(message.getParentFolder(), mode, folder -> {
            try {
                UIDFolder uidFolder = (UIDFolder) folder;
                Message msg = uidFolder.getMessageByUID(Long.parseLong(message.getId()));
                if (msg != null) {
                    return action.apply(msg);
                } else {
                    throw new EmailMessagingException(String.format("Message with Id '%s' does not exist in folder '%s'.",
                            message.getId(), message.getParentFolder()));
                }
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    private <T> T openFolderAndPerform(String folderName, int mode, Function<Folder, T> action) {
        return connectAndPerform(store -> {
            try {
                Folder folder = store.getFolder(folderName);
                if (folder.exists()) {
                    folder.open(mode);
                    try {
                        return action.apply(folder);
                    } finally {
                        folder.close(false);
                    }
                } else {
                    throw new EmailMessagingException(String.format("Folder '%s' does not exist.", folderName));
                }
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    private <T> T walkOverAllFolders(Function<Folder, T> action) {
        return connectAndPerform(store -> {
            try {
                Folder[] folders = store.getDefaultFolder().list("*");
                for (Folder folder : folders) {
                    if (folder.exists() && (folder.getType() & Folder.HOLDS_MESSAGES) > 0) {
                        folder.open(Folder.READ_ONLY);
                        try {
                            T result = action.apply(folder);
                            if (result != null && (!(result instanceof Boolean) || ((Boolean) result))) {
                                return result;
                            }
                        } finally {
                            folder.close(false);
                        }
                    }
                }
                return null;
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        });
    }

    private <T> T connectAndPerform(Function<Store, T> action) {
        try {
            if (!this.store.isConnected()) {
                this.store.connect(user, password);
            }
            if (!this.store.isConnected()) {
                throw new EmailMessagingException("Email store connection failed.");
            }
            return action.apply(this.store);
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        } finally {
            try {
                if (this.store.isConnected()) {
                    this.store.close();
                }
            } catch (MessagingException e) {
                //do nothing
            }
        }
    }

    private Properties getConfigurationFor(InboundEmailProtocol protocol) {
        Properties props = new Properties();
        props.put("mail.store.protocol", protocol.getProtocolName());
        props.put(String.format("mail.%s.host", protocol.getProtocolName()), host);
        props.put(String.format("mail.%s.port", protocol.getProtocolName()), port);

        if (protocol == InboundEmailProtocol.POP3_OVER_TLS || protocol == InboundEmailProtocol.IMAP_OVER_TLS) {
            props.put(String.format("mail.%s.auth", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.starttls.enable", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.starttls.required", protocol.getProtocolName()), "true");

        } else if (protocol == InboundEmailProtocol.POP3S || protocol == InboundEmailProtocol.IMAPS) {
            props.put(String.format("mail.%s.auth", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.ssl.enable", protocol.getProtocolName()), "true");
            props.put(String.format("mail.%s.ssl.protocols", protocol.getProtocolName()), "TLSv1.2");
            props.put(String.format("mail.%s.socketFactory.class", protocol.getProtocolName()), "javax.net.ssl.SSLSocketFactory");
            props.put(String.format("mail.%s.socketFactory.fallback", protocol.getProtocolName()), "false");
            props.put(String.format("mail.%s.socketFactory.port", protocol.getProtocolName()), port);
        }

        return props;
    }
}
