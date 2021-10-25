package eu.ibagroup.easyrpa.examples.email.messages_searching.tasks;

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
import java.util.Arrays;
import java.util.List;

@ApTaskEntry(name = "Lookup Messages with Attachments")
@Slf4j
public class LookupMessagesWithAttachments extends ApTask {

    private static final String MAILBOX_FOLDER_NAME = "Inbox";

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

        log.info("Lookup messages with attachments in folder '{}' of '{}' mailbox.", MAILBOX_FOLDER_NAME, mailboxCredentials.getUser());

        EmailClient emailClient;

        if (rpaServices != null) {
            log.info("Initialize email client using RPA services.");
            emailClient = new EmailClientProvider(rpaServices).getClient();
        } else {
            log.info("Initialize email client using service '{}', protocol '{}' and credentials for '{}'", emailService, emailServiceProtocol, mailboxCredentials.getUser());
            emailClient = new EmailClientProvider().service(emailService).serviceProtocotl(emailServiceProtocol)
                    .mailbox(mailboxCredentials.getUser(), mailboxCredentials.getPassword()).getClient();
        }

        log.info("Fetch messages that contain attachments.");
        List<EmailMessage> messages = emailClient.fetchMessages(MAILBOX_FOLDER_NAME, EmailMessage::hasAttachments);

        log.info("List fetched messages:");
        messages.forEach(msg -> {
            log.info("'{}' from '{}'", msg.getSubject(), msg.getFrom().getPersonal());
        });
    }
}
