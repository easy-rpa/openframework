package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;

import java.util.List;

public interface EmailClient {
    default List<EmailMessage> fetchAllMessages() throws EmailMessagingException {
        return this.fetchAllMessages(this.getDefaultInboxFolder());
    }

    default List<EmailMessage> fetchUnreadMessages(boolean markRead) throws EmailMessagingException {
        return this.fetchUnreadMessages(this.getDefaultInboxFolder(), markRead);
    }

    List<EmailMessage> fetchAllMessages(String folderName) throws EmailMessagingException;

    List<EmailMessage> fetchUnreadMessages(String folderName, boolean markRead) throws EmailMessagingException;

    List<String> fetchFolderList() throws EmailMessagingException;

    boolean createFolder(String folderName) throws EmailMessagingException;

    boolean removeMessage(EmailMessage message) throws EmailMessagingException;

    EmailMessage putMessage(EmailMessage message, String targetFolder) throws EmailMessagingException;

    EmailMessage copyMessage(EmailMessage message, String targetFolder) throws EmailMessagingException;

    EmailMessage moveMessage(EmailMessage message, String targetFolder) throws EmailMessagingException;

    String getDefaultInboxFolder() throws EmailMessagingException;

    void setDefaultInboxFolder(String defaultInboxFolder) throws EmailMessagingException;

    public enum SupportedProtocol {
        POP3, IMAP, EXCHANGE;

        private SupportedProtocol() {
        }

        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
