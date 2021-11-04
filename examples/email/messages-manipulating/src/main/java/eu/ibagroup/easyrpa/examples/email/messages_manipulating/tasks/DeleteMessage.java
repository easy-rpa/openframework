package eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Delete Message")
@Slf4j
public class DeleteMessage extends ApTask {

    @Inject
    private EmailClient emailClient;

    @Input("message")
    private EmailMessage msg;

    @Override
    public void execute() {

        if (msg != null) {
            log.info("Delete input message.");
//            emailClient.deleteMessage(msg);

            msg = emailClient.fetchMessage(msg.getId());

            if (msg == null) {
                log.info("Message has been deleted successfully.");
            } else {
                log.warn("Something went wrong. Message is not deleted.");
            }

        } else {
            log.info("Message is not provided. Skip deleting.");
        }
    }
}
