package services;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import okhttp3.Request;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is an Outlook email client service that provides functionality for working with mailbox folders and email
 * messages in them.
 * <p>
 *  The main purpose of this class is to simplify the work with Microsoft Outlook. With its functionality, you can
 *  send, delete, update, read email messages, create different mail folders and all of that you can do by writing one
 *  line of code.
 */
public class OutlookEmailService {

   /**
    * Instance of authentication and authorization helper for Azure API services
    */
    private final GraphServiceProvider azureAuth;

    /**
     * An instance of GraphServiceClient object to make requests against the service.
     * You can use a single client instance for the lifetime of the application.
     */
    private GraphServiceClient<Request> graphServiceClient;


    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServicesAccessor;

    /**
     * Default constructor for {@code OutlookEmailService}.
     */
    public OutlookEmailService()   {
        azureAuth = new GraphServiceProvider();
        try {
            graphServiceClient = azureAuth.getGraphServiceClient(
                    challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    /**
     * Constructs OutlookEmailService with provided {@link RPAServicesAccessor}.
     * <p>
     * This constructor is used in case of injecting of this OutlookEmailService using {@link Inject} annotation.
     * This is a preferable way of working with this class. E.g.:
     * <pre>
     * {@code @Inject}
     *  private OutlookEmailService outlookEmailService;
     *
     *   public void execute() throws Exception {
     *       . . .
     *         outlookEmailService.sendMail("Subject", "Content", "Recipient");
     *
     *          . . .
     *     }
     * </pre>
     *
     * @param rpaService instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     */
    @Inject
    public OutlookEmailService(RPAServicesAccessor rpaService){
        this.rpaServicesAccessor = rpaService;
        azureAuth = new GraphServiceProvider(rpaService);
    }


    /**
     * Sends a simple email message
     *
     * @param subject   defines the subject of your email message
     * @param body      defines the main content of your email message
     * @param recipient defines a list of email addresses, where your email message will be sent
     */
    public void sendMail(String subject, String body, String... recipient) {

        Message message = MessageBuilder.newBuilder()
                .setMessageContent(body).setMessageSubject(subject).setRecipient(recipient).build().getMessage();

        this.graphServiceClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();

    }

    /**
     * Return an instance of certain email message
     *
     * @param messageID defines a unique identifier of email message
     * @return the instance of the outlook message model
     */
    public Message getMessage(String messageID) {
        return graphServiceClient.me().messages(messageID)
                .buildRequest()
                .get();
    }

    /**
     * Deletes certain email message
     *
     * @param messageID defines a unique identifier of email message
     */
    public void deleteMessage(String messageID) {
        graphServiceClient.me().messages(messageID)
                .buildRequest()
                .delete();
    }

    /**
     * Sends a reply to a certain email message
     *
     * @param comment         the text content of your reply
     * @param messageID       defines a unique identifier of email message
     * @param emailRecipients defines a list of email addresses, where your email message will be sent
     */
    public void replyToMessage(String comment, String messageID, String emailRecipients) {
        Message message = MessageBuilder.newBuilder()
                .setRecipient(emailRecipients).build().getMessage();

        graphServiceClient.me().messages(messageID)
                .reply(MessageReplyParameterSet
                        .newBuilder()
                        .withMessage(message)
                        .withComment(comment)
                        .build())
                .buildRequest()
                .post();
    }

    /**
     * Reply to all recipients of a message
     *
     * @param comment   the text content of your reply
     * @param messageID defines a unique identifier of email message
     */
    public void replyToAll(String comment, String messageID) {
        graphServiceClient.me().messages(messageID)
                .createReplyAll(MessageCreateReplyAllParameterSet
                        .newBuilder()
                        .withMessage(null)
                        .withComment(comment)
                        .build())
                .buildRequest()
                .post();

    }

    /**
     * Forward a message to a concrete list of recipients
     *
     * @param messageID  defines a unique identifier of email message
     * @param comment    the text content of your reply
     * @param recipients defines a list of email addresses, where your email message will be sent
     */
    public void forwardMessage(String messageID, String comment, String... recipients) {
        Message message = MessageBuilder.newBuilder()
                .setRecipient(recipients).build().getMessage();

        graphServiceClient.me().messages(messageID)
                .forward(MessageForwardParameterSet
                        .newBuilder()
                        .withMessage(message)
                        .withComment(comment)
                        .build())
                .buildRequest()
                .post();
    }

    /**
     * Get the messages in the signed-in user's mailbox from concrete mail folder
     *
     * @param mailFolderName name of mail folder
     * @return an instance of {@code MessageCollectionPage}, which defines a list of messages
     */
    public MessageCollectionPage getAllMessagesFromMailFolder(String mailFolderName) {

        return graphServiceClient.me()
                .mailFolders(mailFolderName)
                .messages()
                .buildRequest()
                .get();

    }

    /**
     * Moves concrete message to another mail folder
     *
     * @param mailFolderName name of mail folder
     * @param messageID      defines a unique identifier of email message
     */
    public void moveMessage(String mailFolderName, String messageID) {

        graphServiceClient.me().messages(messageID)
                .move(MessageMoveParameterSet
                        .newBuilder()
                        .withDestinationId(mailFolderName)
                        .build())
                .buildRequest()
                .post();
    }

    /**
     * Returns a collection of mail folders
     *
     * @return an instance of {@code MailFolderCollectionPage}, which defines a list of mail folders
     */
    public MailFolderCollectionPage listMailFolders() {
        return graphServiceClient.me().mailFolders()
                .buildRequest()
                .get();
    }

    /**
     * Creates a mail folder
     *
     * @param mailFolderName name of mail folder
     */
    public void createMailFolder(String mailFolderName) {
        MailFolder mailFolder = new MailFolder();
        mailFolder.displayName = mailFolderName;
        mailFolder.id = mailFolderName;
        mailFolder.isHidden = false;

        graphServiceClient.me().mailFolders()
                .buildRequest()
                .post(mailFolder);
    }

    public MailFolder getMailFolder(String mailFolderName) {
        MailFolderCollectionPage mailFolders = graphServiceClient.me().mailFolders()
                .buildRequest()
                .filter("displayName eq '" + mailFolderName + "'")
                .get();

        assert mailFolders != null;
        return mailFolders.getCurrentPage().get(0);
    }

    /**
     * Updates a mail folder
     *
     * @param oldMailFolderName folder name to be replaced
     * @param newMailFolderName folder name to be replaces with
     */
    public void renameMailFolder(String oldMailFolderName, String newMailFolderName) {
        MailFolder newMailFolder = new MailFolder();
        newMailFolder.displayName = newMailFolderName;

        MailFolderCollectionPage mailFolders = graphServiceClient.me().mailFolders()
                .buildRequest()
                .filter("displayName eq '" + oldMailFolderName + "'")
                .get();

        if (mailFolders != null) {
            graphServiceClient.me().mailFolders(Objects.requireNonNull(mailFolders.getCurrentPage().get(0).id))
                    .buildRequest()
                    .patch(newMailFolder);
        }


    }

    /**
     * Deletes a mail folder
     *
     * @param mailFolderName name of mail folder
     */
    public void deleteMailFolder(String mailFolderName) {
        MailFolderCollectionPage mailFolders = graphServiceClient.me().mailFolders()
                .buildRequest()
                .filter("displayName eq '" + mailFolderName + "'")
                .get();


        if (mailFolders != null) {
            graphServiceClient.me().mailFolders(Objects.requireNonNull(mailFolders.getCurrentPage().get(0).id))
                    .buildRequest()
                    .delete();
        }
    }

    /**
     * Gets all email messages contained in all mailbox folders.
     * <p>
     * <b>WARNING:</b> This is a heavy operations and can take much time.
     *
     * @return list of {@link Message} objects representing existing email messages.
     */
    public java.util.List<Message> fetchAllMessages() {
        MessageCollectionPage messages = graphServiceClient.me().messages()
                .buildRequest()
                .top(50)
                .get();

        assert messages != null;
        return getMessages(messages);
    }

    /**
     * Gets all email messages contained in the mailbox folder with given name.
     *
     * @param folderName the name of mailbox folder which is necessary to get all messages from.
     * @return list of {@link Message} objects representing existing email messages.
     */
    public java.util.List<Message> fetchMessages(String folderName) {
        MailFolder mailFolder = this.getMailFolder(folderName);
        assert mailFolder.id != null;
        MessageCollectionPage messages = graphServiceClient.me().mailFolders(mailFolder.id)
                .messages()
                .buildRequest()
                .top(50)
                .get();

        assert messages != null;
        return getMessages(messages);
    }

    /**
     * Gets all email messages contained in the default mailbox folder.
     *
     * @return list of {@link Message} objects representing existing email messages.
     */
    public java.util.List<Message> fetchMessages() {
        MessageCollectionPage messages = graphServiceClient.me().mailFolders("inbox")
                .messages()
                .buildRequest()
                .top(50)
                .get();

        assert messages != null;
        return getMessages(messages);
    }

    /**
     * Private method that helps to get a list of remaining email messages
     *
     * @param messages the collection of existing email messages
     * @return list of {@link Message} objects representing existing email messages.
     */
    private java.util.List<Message> getMessages(MessageCollectionPage messages) {

        List<Message> result = new ArrayList<>(messages.getCurrentPage());

        boolean moreMessagesAvailable = messages.getNextPage() != null;
        while (moreMessagesAvailable) {
            messages.getNextPage().buildRequest().top(50).get();
            result.addAll(messages.getCurrentPage());
            if (messages.getNextPage() == null) moreMessagesAvailable = false;
        }

        return result;
    }
}
