package eu.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.email.EmailClient;
import eu.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Delete Message")
@Slf4j
public class DeleteMessage extends ApTask {

    @Inject
    private EmailClient emailClient;

    @Input("messageId")
    private String messageId;

    @Override
    public void execute() {
        log.info("Deleting of message with id '{}'.", messageId);
        EmailMessage message = emailClient.fetchMessage(messageId);

        if (message != null) {
            log.info("Delete message.");
            emailClient.deleteMessage(message);

            message = emailClient.fetchMessage(messageId);
            if (message == null) {
                log.info("Message has been deleted successfully.");
            } else {
                log.warn("Something went wrong. Message is not deleted.");
            }

        } else {
            log.error("Message with id '{}' is not found.", messageId);
        }
    }
}
