package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public interface InboundEmailService {

    int getMessageCount(String folderName);

    EmailMessage fetchMessage(String messageId);

    List<EmailMessage> fetchMessages(String folderName, Predicate<EmailMessage> isSatisfy) throws EmailMessagingException;

    List<EmailMessage> fetchAllMessages(Predicate<EmailMessage> isSatisfy);

    List<EmailMessage> fetchUnreadMessages(String folderName, boolean markRead) throws EmailMessagingException;

    CompletableFuture<List<EmailMessage>> waitMessages(String folderName, Predicate<EmailMessage> isSatisfy, Duration timeout, Duration checkInterval) throws EmailMessagingException;

    EmailMessage copyMessage(EmailMessage message, String targetFolder) throws EmailMessagingException;

    EmailMessage moveMessage(EmailMessage message, String targetFolder) throws EmailMessagingException;

    void saveMessage(EmailMessage message) throws EmailMessagingException;

    void saveMessages(List<EmailMessage> messages) throws EmailMessagingException;

    void deleteMessage(EmailMessage message) throws EmailMessagingException;

    void deleteMessages(List<EmailMessage> messages) throws EmailMessagingException;

    List<String> listFolders() throws EmailMessagingException;

    boolean createFolder(String folderName) throws EmailMessagingException;

    boolean renameFolder(String folderName, String newFolderName) throws EmailMessagingException;

    boolean deleteFolder(String folderName) throws EmailMessagingException;
}
