package eu.easyrpa.examples.azure.services.outlook_message_reading.tasks;

import com.microsoft.graph.models.Message;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import services.OutlookEmailService;

import javax.inject.Inject;

@ApTaskEntry(name = "btuh")
public class ReadMessagesTask extends ApTask {

    @Inject
    private OutlookEmailService outlookEmailService;

    @Override
    public void execute() throws Exception {
        Message message = outlookEmailService.getMessage("MessageID");

        //This is how you get access to message subject
        String subject = message.subject;
        System.out.println(subject);

        //This is how you get access to message content
        String body = message.body.content;
        System.out.println(body);



    }
}
