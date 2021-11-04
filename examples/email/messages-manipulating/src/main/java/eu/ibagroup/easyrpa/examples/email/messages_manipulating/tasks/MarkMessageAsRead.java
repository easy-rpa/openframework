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

    @Input("message")
    private EmailMessage msg;

    @Override
    public void execute() {

        if (msg != null) {
            log.info("Mark input message as read.");
            emailClient.saveMessage(msg.markRead());
        } else {
            log.info("Message is not provided. Skip marking.");
        }
    }
}
