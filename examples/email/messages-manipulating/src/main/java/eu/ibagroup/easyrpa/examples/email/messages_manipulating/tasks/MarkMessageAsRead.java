package eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Mark Message As Read")
@Slf4j
public class MarkMessageAsRead extends ApTask {

    @Inject
    private EmailClient emailClient;

    @Input("messageId")
    private String messageId;

    @Override
    public void execute() {
        log.info("Marking message with id '{}' as read.", messageId);
        EmailMessage message = emailClient.fetchMessage(messageId);

        if (message != null) {
            log.info("Mark input message as read.");
//            message.markRead();

        } else {
            log.error("Message with id '{}' is not found.", messageId);
        }
    }
}
