package services;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import okhttp3.Request;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

public class OutlookEmailService {
    private AzureAuth azureAuth;

    private GraphServiceClient<Request> graphServiceClient;

    private RPAServicesAccessor rpaServicesAccessor;

    public OutlookEmailService()   {
        azureAuth = new AzureAuth();
        try {
            graphServiceClient = azureAuth.initializeGraphForUserAuth(
                    challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    @Inject
    public OutlookEmailService(RPAServicesAccessor rpaService){
        this();
        this.rpaServicesAccessor = rpaService;
    }

    public  void sendMail(String subject, String body, String recipient) throws Exception {
        // Ensure client isn't null
        if (this.graphServiceClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        // Create a new message
        final Message message = new Message();
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        final Recipient toRecipient = new Recipient();
        toRecipient.emailAddress = new EmailAddress();
        toRecipient.emailAddress.address = recipient;
        message.toRecipients = List.of(toRecipient);

        // Send the message
        this.graphServiceClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();
    }

    public  Message getMessage(String messageID){
        return graphServiceClient.me().messages(messageID)
                .buildRequest()
                .get();
    }

    public  void deleteMessage(String messageID) {
        graphServiceClient.me().messages(messageID)
                .buildRequest()
                .delete();
    }

    public  void sendMessages(String messageID){
        graphServiceClient.me().messages(messageID)
                .send()
                .buildRequest()
                .post();
    }

    public  void replyToMessage(){
        Message message = new Message();
        LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = "samanthab@contoso.onmicrosoft.com";
        emailAddress.name = "Samantha Booth";
        toRecipients.emailAddress = emailAddress;
        toRecipientsList.add(toRecipients);
        Recipient toRecipients1 = new Recipient();
        EmailAddress emailAddress1 = new EmailAddress();
        emailAddress1.address = "randiw@contoso.onmicrosoft.com";
        emailAddress1.name = "Randi Welch";
        toRecipients1.emailAddress = emailAddress1;
        toRecipientsList.add(toRecipients1);
        message.toRecipients = toRecipientsList;

        String comment = "Samantha, Randi, would you name the group please?";

        graphServiceClient.me().messages("AAMkADA1MTAAAAqldOAAA=")
                .reply(MessageReplyParameterSet
                        .newBuilder()
                        .withMessage(message)
                        .withComment(comment)
                        .build())
                .buildRequest()
                .post();
    }



}
