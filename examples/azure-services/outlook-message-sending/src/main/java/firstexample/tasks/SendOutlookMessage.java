package firstexample.tasks;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import eu.easyrpa.openframework.azure.services.GraphServiceProvider;

import java.util.LinkedList;

@Slf4j
@ApTaskEntry(name = "Send Outlook message")
public class SendOutlookMessage extends ApTask {

//    @Inject
//    private GraphServiceProvider graphServiceProvider;

    @Configuration(value = "mail.recipients")
    private String simpleEmailRecipients;

    @Configuration(value = "mail.subject")
    private String subject;

    @Configuration(value = "mail.body")
    private String body;

    @Override
    public void execute()  {

        GraphServiceProvider graphServiceProvider = new GraphServiceProvider("dc59bb45-5a6e-47ca-820d-2f049ae03848","common",
                "user.read,mail.read,mail.send,mail.readwrite,files.readwrite");
        GraphServiceClient<Request> graphClient = graphServiceProvider.getGraphServiceClient();

        final Message message = new Message();
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        String[] recipients = simpleEmailRecipients.split(",");
        for(String emailRecipient: recipients) {
            Recipient toRecipient = new Recipient();
            toRecipient.emailAddress = new EmailAddress();
            toRecipient.emailAddress.address = emailRecipient;
            toRecipientsList.add(toRecipient);
        }
        message.toRecipients = toRecipientsList;

        graphClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();
    }


}
