package eu.easyrpa.examples.azure.services.outlook_message_reading.tasks;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import eu.easyrpa.openframework.azure.services.GraphServiceProvider;

import javax.inject.Inject;

@ApTaskEntry(name = "Reading of all messages from Outlook test task")
@Slf4j
public class ReadMessagesTask extends ApTask {

    @Inject
    private GraphServiceProvider graphServiceProvider;

    @Override
    public void execute() {
        log.info("Building GraphServiceClient to make a request");
        GraphServiceClient<Request> graphClient = graphServiceProvider.getClient();

        log.info("Getting all messages from Outlook");
        MessageCollectionPage messages = graphClient.me()
                .messages()
                .buildRequest()
                .get();


        log.info("Logging all messages subject into console");
        if (messages != null) {
            for (Message message : messages.getCurrentPage()) {
                log.info(message.subject);
            }
        }

        log.info("All messages have been logged successfully");
    }
}
