package eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Get Unread Message")
@Slf4j
public class GetUnreadMessage extends ApTask {

    @Inject
    private EmailClient emailClient;

    @Output("messageId")
    private String messageId;

    @Override
    public void execute() {
        log.info("Lookup unread message in folder '{}'", emailClient.getDefaultFolder());

        log.info("Fetch all unread messages.");
        List<EmailMessage> messages = emailClient.fetchUnreadMessages("Personal", false);

        if (messages.size() > 0) {
            log.info("Unread messages found. Take first one.");
            messageId = messages.get(0).getId();
        } else {
            log.info("No unread messages found in folder '{}'.", emailClient.getDefaultFolder());
        }
    }
}
