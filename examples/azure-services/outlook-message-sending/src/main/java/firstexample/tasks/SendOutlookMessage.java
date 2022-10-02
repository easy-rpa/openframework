package firstexample.tasks;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import services.GraphServiceProvider;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Send Outlook message")
public class SendOutlookMessage extends ApTask {

    @Inject
    private GraphServiceProvider graphServiceProvider;

    @Override
    public void execute()  {
        GraphServiceClient<Request> graphClient = graphServiceProvider.getGraphServiceClient();

        String subject = "Hello!";
        String body = "Hello world";
        String recipient = "put your email here";
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
        graphClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();
    }


}
