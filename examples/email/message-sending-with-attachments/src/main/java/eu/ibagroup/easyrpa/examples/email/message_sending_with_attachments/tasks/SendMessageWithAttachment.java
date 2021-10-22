package eu.ibagroup.easyrpa.examples.email.message_sending_with_attachments.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.Email;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@ApTaskEntry(name = "Send Message with Attachment")
@Slf4j
public class SendMessageWithAttachment extends ApTask {

    private static final String SUBJECT = "Test email with attachment";
    private static final String BODY = "This message was sent by EasyRPA Bot and has attached file.";
    private static final String PATH_TO_FILE = "/Test.xlsx";

    @Configuration(value = "email.service")
    private String emailService;

    @Configuration(value = "email.service.protocol")
    private String emailServiceProtocol;

    @Configuration(value = "email.recipients")
    private String emailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private RPAServicesAccessor rpaServices;

    @Override
    public void execute() throws IOException {

        log.info("Read '{}' file.", PATH_TO_FILE);
        File testFile = readResourceFile(PATH_TO_FILE);

        log.info("Send email message to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, emailService, emailServiceProtocol, emailUserCredentials.getUser());

        Email.create().service(emailService).serviceProtocol(emailServiceProtocol)
                .credentials(emailUserCredentials.getUser(), emailUserCredentials.getPassword())
                .recipients(emailRecipients)
                .subject(SUBJECT).body(BODY).attach(testFile).send();

        log.info("Send the same message using RPA services accessor.");

        Email.create(rpaServices).subject(SUBJECT).body(BODY).attach(testFile).send();

        log.info("Messages have been sent successfully");
    }

    public File readResourceFile(String path) {
        try {
            return new File(this.getClass().getResource(path).toURI());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Reading of file '%s' has failed.", path), e);
        }
    }
}
