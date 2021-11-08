package eu.ibagroup.easyrpa.examples.email.attachments_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.exception.BreakEmailCheckException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApTaskEntry(name = "Read Messages with Attachments")
@Slf4j
public class ReadMessagesWithAttachments extends ApTask {

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Lookup messages with attachments in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());

        log.info("There are {} messages in folder '{}'.", emailClient.getMessageCount(), emailClient.getDefaultFolder());

        log.info("Fetch first message that contain attachments.");
        List<EmailMessage> messages = emailClient.fetchMessages(msg -> {
            log.info("Check message '{}'", msg.getSubject());
            if (msg.hasAttachments()) {
                //By throwing this exception it stops further checking of emails and return this message as single result
                throw new BreakEmailCheckException(true);
            }
            return false;
        });

        if (messages.size() > 0) {
            log.info("Message with attachments found.");
            EmailMessage msg = messages.get(0);
            EmailAttachment attachment = msg.getAttachments().get(0);

            try {
                log.info("Read content of the attached file.");
                List<String> content = IOUtils.readLines(attachment.getInputStream(), StandardCharsets.UTF_8);

                content.forEach(log::info);

            } catch (Exception e) {
                throw new RuntimeException("Reading of attached file content has failed.", e);
            }
        } else {
            log.info("No messages with attachments found in folder '{}' of '{}' mailbox.", emailClient.getDefaultFolder(), mailboxCredentials.getUser());
        }
    }
}
