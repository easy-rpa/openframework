package eu.ibagroup.easyrpa.examples.email.message_waiting.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ApTaskEntry(name = "Wait Messages with Keywords")
@Slf4j
public class WaitMessagesWithKeywords extends ApTask {

    private static final List<String> LOOKUP_KEYWORDS = Arrays.asList("database", "storage");

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() throws ExecutionException, InterruptedException {

        log.info("Wait appearing of messages in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("Wait messages that contain any of '{}' keywords in subject or body.", LOOKUP_KEYWORDS);
        List<EmailMessage> messages = emailClient.waitMessages(msg -> {
            log.info("Check message '{}'", msg.getSubject());
            boolean subjectContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getSubject()::contains);
            boolean bodyContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getText()::contains);
            return subjectContainsKeywords || bodyContainsKeywords;
        }, Duration.ofMinutes(30), Duration.ofSeconds(5)).get();

        log.info("Retrieved messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
        });
    }
}
