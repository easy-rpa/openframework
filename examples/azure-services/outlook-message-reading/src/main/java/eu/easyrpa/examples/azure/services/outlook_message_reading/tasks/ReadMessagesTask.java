package eu.easyrpa.examples.azure.services.outlook_message_reading.tasks;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.MessageCollectionPage;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import services.OutlookEmailService;

import javax.inject.Inject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@ApTaskEntry(name = "btuh")
public class ReadMessagesTask extends ApTask {

    @Inject
    private OutlookEmailService outlookEmailService;

    @Override
    public void execute() throws Exception {
        MessageCollectionPage messages = outlookEmailService.getAllMessagesFromMailFolder("inbox");

        for (Message message: messages.getCurrentPage()) {
            System.out.println("Message: " + message.subject);
            System.out.println("  From: " + message.from.emailAddress.name);
            System.out.println("  Status: " + (message.isRead ? "Read" : "Unread"));
            System.out.println("  Received: " + message.receivedDateTime
                    // Values are returned in UTC, convert to local time zone
                    .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        }

    }
}
