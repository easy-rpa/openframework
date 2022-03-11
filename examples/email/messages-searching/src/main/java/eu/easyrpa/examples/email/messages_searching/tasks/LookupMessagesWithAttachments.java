package eu.easyrpa.examples.email.messages_searching.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.easyrpa.openframework.email.EmailClient;
import eu.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Lookup Messages with Attachments")
@Slf4j
public class LookupMessagesWithAttachments extends ApTask {

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Lookup messages with attachments in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("Fetch messages that contain attachments.");
        List<EmailMessage> messages = emailClient.fetchMessages(EmailMessage::hasAttachments);

        log.info("List fetched messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
        });
    }
}
