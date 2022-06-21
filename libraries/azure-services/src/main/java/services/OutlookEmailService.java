package services;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import okhttp3.Request;
import services.exception.GraphAuthException;

import javax.inject.Inject;
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
    private final AzureAuth azureAuth;

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
        azureAuth = new AzureAuth();
        try {
            graphServiceClient = azureAuth.initializeGraphForUserAuth(
                    challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    /**
     * Constructs OutlookEmailService with provided {@link RPAServicesAccessor}.
     * <p>
     * This constructor is used in case of injecting of this OutlookEmailService using {@link Inject} annotation.
     * This is preferable way of working with this class. E.g.:
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
        azureAuth = new AzureAuth(rpaService);
    }


    /**
     * Sends a simple email message
     *
     * @param subject defines the subject of your email message
     * @param body defines the main content of your email message
     * @param recipient defines a list of email addresses, where your email message will be sent
     */
    public  void sendMail(String subject, String body, String... recipient)  {

        Message message = MessageBuilder.newBuilder()
                .setMessageContent(body).setMessageSubject(subject).setRecipient(recipient).build().getMessage();

        try {
            this.graphServiceClient.me()
                    .sendMail(UserSendMailParameterSet.newBuilder()
                            .withMessage(message)
                            .build())
                    .buildRequest()
                    .post();
        } catch (Exception e){
            throw  new GraphAuthException("Graph has not been initialized for user auth",e);
        }
    }

    /**
     * Return an instance of certain email message
     * @param messageID defines a unique identifier of email message
     * @return the instance of the outlook message model
     */
    public  Message getMessage(String messageID){
        return graphServiceClient.me().messages(messageID)
                .buildRequest()
                .get();
    }

    /**
     * Deletes certain email message
     * @param messageID defines a unique identifier of email message
     */
    public  void deleteMessage(String messageID) {
        graphServiceClient.me().messages(messageID)
                .buildRequest()
                .delete();
    }

    /**
     * Sends a reply to a certain email message
     * @param comment the text content of your reply
     * @param messageID defines a unique identifier of email message
     * @param emailRecipients defines a list of email addresses, where your email message will be sent
     */
    public  void replyToMessage(String comment, String messageID, String emailRecipients){
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
     * @param comment the text content of your reply
     * @param messageID defines a unique identifier of email message
     */
    public void replyToAll(String comment, String messageID){
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
     * @param messageID defines a unique identifier of email message
     * @param comment the text content of your reply
     * @param recipients defines a list of email addresses, where your email message will be sent
     */
    public void forwardMessage(String messageID, String comment, String... recipients){
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
     * @param mailFolderName name of mail folder
     * @return an instance of {@code MessageCollectionPage}, which defines a list of messages
     */
    public MessageCollectionPage getAllMessagesFromMailFolder(String mailFolderName)  {
        try {
            return graphServiceClient.me()
                    .mailFolders(mailFolderName)
                    .messages()
                    .buildRequest()
                    .get();
        } catch (Exception e){
            throw  new GraphAuthException("Graph has not been initialized for user auth",e);
        }
    }

    /**
     * Moves concrete message to another mail folder
     * @param mailFolderName name of mail folder
     * @param messageID defines a unique identifier of email message
     */
    public void moveMessage(String mailFolderName, String messageID){

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
     * @return an instance of {@code MailFolderCollectionPage}, which defines a list of mail folders
     */
    public MailFolderCollectionPage listMailFolders(){
        return graphServiceClient.me().mailFolders()
                .buildRequest()
                .get();
    }

    /**
     * Creates a mail folder
     * @param mailFolderName name of mail folder
     */
    public void createMailFolder(String mailFolderName){
        MailFolder mailFolder = new MailFolder();
        mailFolder.displayName = mailFolderName;
        mailFolder.isHidden = false;

        graphServiceClient.me().mailFolders()
                .buildRequest()
                .post(mailFolder);
    }

    /**
     * Updates a mail folder
     * @param oldMailFolderName folder name to be replaced
     * @param newMailFolderName folder name to be replaces with
     */
    public void updateMailFolder(String oldMailFolderName,String newMailFolderName){
        MailFolder mailFolder = new MailFolder();
        mailFolder.displayName = newMailFolderName;

        graphServiceClient.me().mailFolders(oldMailFolderName)
                .buildRequest()
                .patch(mailFolder);
    }

    /**
     * Deletes a mail folder
     * @param mailFolderName name of mail folder
     */
    public void deleteMailFolder(String mailFolderName){
        graphServiceClient.me().mailFolders(mailFolderName)
                .buildRequest()
                .delete();
    }
}
