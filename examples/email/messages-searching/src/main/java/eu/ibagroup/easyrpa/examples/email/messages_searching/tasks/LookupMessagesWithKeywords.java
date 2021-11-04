package eu.ibagroup.easyrpa.examples.email.messages_searching.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@ApTaskEntry(name = "Lookup Messages with Keywords")
@Slf4j
public class LookupMessagesWithKeywords extends ApTask {

    private static final List<String> LOOKUP_KEYWORDS = Arrays.asList("database", "storage");

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Lookup messages with keywords in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("Fetch messages that contain any of '{}' keywords in subject or body.", LOOKUP_KEYWORDS);
        List<EmailMessage> messages = emailClient.fetchMessages(msg -> {
            boolean subjectContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getSubject()::contains);
            boolean bodyContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getBody()::contains);
            return subjectContainsKeywords || bodyContainsKeywords;
        });

        log.info("List fetched messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
        });
    }
}
