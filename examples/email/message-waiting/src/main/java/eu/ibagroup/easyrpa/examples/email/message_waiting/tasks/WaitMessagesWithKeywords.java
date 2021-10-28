package eu.ibagroup.easyrpa.examples.email.message_waiting.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.EmailClientProvider;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.EmailClient;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApTaskEntry(name = "Wait Messages with Keywords")
@Slf4j
public class WaitMessagesWithKeywords extends ApTask {

    private static final String MAILBOX_FOLDER_NAME = "Inbox";

    private static final List<String> LOOKUP_KEYWORDS = Arrays.asList("database", "DB", "storage");

    @Configuration(value = "email.service")
    private String emailService;

    @Configuration(value = "email.service.protocol")
    private String emailServiceProtocol;

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private RPAServicesAccessor rpaServices;

    @Override
    public void execute() {

        log.info("Wait appearing of messages in folder '{}' of '{}' mailbox.", MAILBOX_FOLDER_NAME, mailboxCredentials.getUser());

        EmailClient emailClient;

        if (rpaServices != null) {
            log.info("Initialize email client using RPA services.");
            emailClient = new EmailClientProvider(rpaServices).getClient();
        } else {
            log.info("Initialize email client using service '{}', protocol '{}' and credentials for '{}'", emailService, emailServiceProtocol, mailboxCredentials.getUser());
            emailClient = new EmailClientProvider().service(emailService).serviceProtocotl(emailServiceProtocol)
                    .mailbox(mailboxCredentials.getUser(), mailboxCredentials.getPassword()).getClient();
        }

        log.info("Wait messages that contain any of '{}' keywords in subject or body.", LOOKUP_KEYWORDS);
        List<EmailMessage> messages = emailClient.waitMessages(MAILBOX_FOLDER_NAME, msg -> {
            boolean subjectContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getSubject()::contains);
            boolean bodyContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getText()::contains);
            return subjectContainsKeywords || bodyContainsKeywords;
        }, Duration.ofMinutes(30), Duration.ofMinutes(5));

        log.info("Retrieved messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getFrom().getPersonal());
        });
    }
}
