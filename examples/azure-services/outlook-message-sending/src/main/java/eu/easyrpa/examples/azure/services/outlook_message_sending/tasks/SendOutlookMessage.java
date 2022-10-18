package eu.easyrpa.examples.azure.services.outlook_message_sending.tasks;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import eu.easyrpa.openframework.azure.services.GraphServiceProvider;

import javax.inject.Inject;
import java.util.LinkedList;

@Slf4j
@ApTaskEntry(name = "Send Outlook message")
public class SendOutlookMessage extends ApTask {

    @Inject
    private GraphServiceProvider graphServiceProvider;

    @Configuration(value = "mail.recipients")
    private String simpleEmailRecipients;

    @Configuration(value = "mail.subject")
    private String subject;

    @Configuration(value = "mail.body")
    private String body;

    @Override
    public void execute()  {
        log.info("Building GraphServiceClient to make a request");
        GraphServiceClient<Request> graphClient = graphServiceProvider.getClient();

        final Message message = new Message();
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        String[] recipients = simpleEmailRecipients.split(";");
        for(String emailRecipient: recipients) {
            Recipient toRecipient = new Recipient();
            toRecipient.emailAddress = new EmailAddress();
            toRecipient.emailAddress.address = emailRecipient;
            toRecipientsList.add(toRecipient);
        }
        message.toRecipients = toRecipientsList;

        log.info("Send simple email message to '{}' with subject '{}' and a body '{}'.",
                simpleEmailRecipients, message.subject, message.body);
        graphClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();
    }


}
