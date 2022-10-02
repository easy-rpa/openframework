package eu.easyrpa.examples.azure.services.outlook_message_reading.tasks;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import services.GraphServiceProvider;

import javax.inject.Inject;

@ApTaskEntry(name = "Reading of all messages from Outlook test task")
@Slf4j
public class ReadMessagesTask extends ApTask {

    @Inject
    private GraphServiceProvider graphServiceProvider;

    @Override
    public void execute() {
        GraphServiceClient<Request> graphClient = graphServiceProvider.getGraphServiceClient();

        MessageCollectionPage messages = graphClient.me()
                .messages()
                .buildRequest()
                .get();


        if (messages != null) {
            for (Message message : messages.getCurrentPage()) {
                log.info(message.subject);
            }
        }

    }
}
