package eu.ibagroup.easyrpa.examples.email.attachments_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.EmailClientProvider;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.EmailClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApTaskEntry(name = "Read Messages with Attachments")
@Slf4j
public class ReadMessagesWithAttachments extends ApTask {

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

        log.info("Initialize email client using parameters that are provided in the process configuration.");
        EmailClient emailClient = new EmailClientProvider(rpaServices).getClient();

        log.info("Fetch messages that contain attachments.");
        List<EmailMessage> messages = emailClient.fetchMessages(MAILBOX_FOLDER_NAME, EmailMessage::hasAttachments);

        if (messages.size() > 0) {
            log.info("Message with attachments found.");
            EmailMessage msg = messages.get(0);
            EmailAttachment attachment = msg.getAttachments().get(0);

            try {
                log.info("Read content of the first attached file.");
                List<String> content = IOUtils.readLines(attachment.getInputStream(), StandardCharsets.UTF_8);

                content.forEach(log::info);

            } catch (Exception e) {
                throw new RuntimeException("Reading of attached file content has failed.", e);
            }
        } else {
            log.info("No messages with attachments found in folder '{}' of '{}' mailbox.", MAILBOX_FOLDER_NAME, mailboxCredentials.getUser());
        }
    }
}
