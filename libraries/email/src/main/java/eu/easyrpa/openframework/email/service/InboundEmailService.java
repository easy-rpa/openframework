package eu.easyrpa.openframework.email.service;

import eu.easyrpa.openframework.email.EmailMessage;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Single interface for all inbound email services that provides functionality for working with mailbox based
 * on specific inbound email protocols.
 */
public interface InboundEmailService {

    /**
     * Gets amount of email messages in the mailbox folder with given name.
     *
     * @param folderName the name of mailbox folder where is necessary to count messages.
     * @return amount of email messages in the mailbox folder with given name.
     */
    int getMessageCount(String folderName);

    /**
     * Searches email message in the mailbox with given message ID.
     * <p>
     * It views all mailbox folders to find the necessary message.
     *
     * @param messageId the unique identifier of email message that is necessary to find.
     * @return {@link EmailMessage} object representing found email message or <code>null</code> if nothing is found.
     */
    EmailMessage fetchMessage(String messageId);

    /**
     * Gets email messages contained in the mailbox folder with given name and satisfy to specific condition.
     *
     * @param folderName the name of mailbox folder where is necessary to collect email messages.
     * @param isSatisfy  lambda expression representing specific condition. It should return <code>true</code> to let
     *                   email message get into results.
     * @return list of {@link EmailMessage} objects representing satisfying email messages.
     */
    List<EmailMessage> fetchMessages(String folderName, Predicate<EmailMessage> isSatisfy);

    /**
     * Gets all email messages contained in all mailbox folders and satisfy to specific condition.
     *
     * @param isSatisfy lambda expression representing specific condition. It should return <code>true</code> to let
     *                  email message get into results.
     * @return list of {@link EmailMessage} objects representing satisfying email messages.
     */
    List<EmailMessage> fetchAllMessages(Predicate<EmailMessage> isSatisfy);

    /**
     * Gets unread messages contained in the mailbox folder with given name.
     *
     * @param folderName the name of mailbox folder where is necessary to search unread messages.
     * @param markRead   whether is necessary to mark all found unread messages as read immediately.
     * @return list of {@link EmailMessage} objects representing unread email messages.
     */
    List<EmailMessage> fetchUnreadMessages(String folderName, boolean markRead);

    /**
     * Waits appearing of email messages in the mailbox folder with given name that satisfy to specific condition.
     *
     * @param folderName    the name of mailbox folder where is necessary to check messages.
     * @param isSatisfy     lambda expression representing specific condition. It should return <code>true</code> to let
     *                      email message get into results.
     * @param timeout       the maximum time of waiting necessary messages.
     * @param checkInterval amount of time that defines period of checking newly come messages.
     * @return {@link CompletableFuture} object with list of {@link EmailMessage} objects representing satisfying email
     * messages as result.
     */
    CompletableFuture<List<EmailMessage>> waitMessages(String folderName, Predicate<EmailMessage> isSatisfy, Duration timeout, Duration checkInterval);

    /**
     * Makes a copy of given email message in the specified folder.
     *
     * @param message      the source email message that should be copied.
     * @param targetFolder the name of mailbox folder where the email message should be copied.
     * @return {@link EmailMessage} object representing copied message.
     */
    EmailMessage copyMessage(EmailMessage message, String targetFolder);

    /**
     * Moves given email message to the specified folder.
     *
     * @param message      the source email message that should be moved.
     * @param targetFolder the name of mailbox folder where the email message should be moved.
     * @return {@link EmailMessage} object representing moved message. The source message will be deleted.
     */
    EmailMessage moveMessage(EmailMessage message, String targetFolder);

    /**
     * Updates parameters of given email message in the mailbox.
     *
     * @param message the email message whose parameters should be updated in the mailbox.
     */
    void updateMessage(EmailMessage message);

    /**
     * Updates parameters of given email messages in the mailbox.
     *
     * @param messages the list of email messages whose parameters should be updated in the mailbox.
     */
    void updateMessages(List<EmailMessage> messages);

    /**
     * Deletes given email message from the mailbox.
     * <p>
     * The message is deleted permanently without Trash.
     *
     * @param message the email message to delete.
     */
    void deleteMessage(EmailMessage message);

    /**
     * Deletes given email messages from the mailbox.
     * <p>
     * Messages are deleted permanently without Trash.
     *
     * @param messages the list of email messages to delete.
     */
    void deleteMessages(List<EmailMessage> messages);

    /**
     * Gets the list of folder names that are present in the mailbox.
     *
     * @return list of folder names that are present in the mailbox.
     */
    List<String> listFolders();

    /**
     * Creates a new folder with given name in the mailbox.
     *
     * @param folderName the name of folder to create.
     * @return <code>true</code> if the folder has been created successfully and <code>false</code> otherwise.
     */
    boolean createFolder(String folderName);

    /**
     * Renames given mailbox folder.
     *
     * @param folderName    the name of source mailbox folder.
     * @param newFolderName a new name of the folder.
     * @return <code>true</code> if the folder has been renamed successfully and <code>false</code> otherwise.
     */
    boolean renameFolder(String folderName, String newFolderName);

    /**
     * Deletes given mailbox folder with all messages and sub-folders contained in it.
     *
     * @param folderName the name of mailbox folder to delete.
     * @return <code>true</code> if the folder has been deleted successfully and <code>false</code> otherwise.
     */
    boolean deleteFolder(String folderName);
}
