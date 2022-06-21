package services;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import okhttp3.Request;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

public class OutlookEmailService {
    private final AzureAuth azureAuth;

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
        this.rpaServicesAccessor = rpaService;
        azureAuth = new AzureAuth(rpaService);
    }

    public  void sendMail(String subject, String body, String recipient) throws Exception {
        // Ensure client isn't null
        if (this.graphServiceClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        Message message = MessageBuilder.newBuilder()
                .setMessageContent(body).setMessageSubject(subject).setRecipient(recipient).build().getMessage();

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


}
