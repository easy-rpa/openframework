package eu.easyrpa.examples.email.inbox_messages_listing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.easyrpa.openframework.email.EmailClient;
import eu.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Get Inbox Messages")
@Slf4j
public class GetInboxMessages extends ApTask {

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Getting all messages from folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("There are {} messages in folder '{}'.", emailClient.getMessageCount(), emailClient.getDefaultFolder());

        log.info("Fetch messages using email client.");
        List<EmailMessage> messages = emailClient.fetchMessages();

        log.info("List fetched messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
        });
    }
}
