package eu.ibagroup.easyrpa.examples.email.message_sending_with_attachments.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@ApTaskEntry(name = "Send Message with Attachment")
@Slf4j
public class SendMessageWithAttachment extends ApTask {

    private static final String ATTACHMENT_SUBJECT = "Test email with attachment";
    private static final String ATTACHMENT_BODY = "This message was sent by EasyRPA Bot and has attached file.";

    private static final String INLINE_ATTACHMENT_SUBJECT = "Test email with inline attachment";
    private static final String INLINE_ATTACHMENT_BODY = "This message was sent by EasyRPA Bot and includes following file:";
    private static final String ANOTHER_BODY_TEXT = "Some text in the end.";

    private static final String PATH_TO_FILE = "/Test.xlsx";

    @Configuration(value = "outbound.email.server")
    private String outboundEmailServer;

    @Configuration(value = "outbound.email.protocol")
    private String outboundEmailProtocol;

    @Configuration(value = "email.recipients")
    private String emailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private EmailSender emailSender;

    @Override
    public void execute() throws IOException {

        log.info("Read '{}' file.", PATH_TO_FILE);
        File testFile = readResourceFile(PATH_TO_FILE);

        log.info("Send email message to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, outboundEmailServer, outboundEmailProtocol, emailUserCredentials.getUser());

        log.info("Send message with attached file.");
        new EmailMessage(emailSender).subject(ATTACHMENT_SUBJECT).body(ATTACHMENT_BODY).attach(testFile).send();

        log.info("Send message with attachment in the body.");
        new EmailMessage(emailSender).subject(INLINE_ATTACHMENT_SUBJECT)
                .body(INLINE_ATTACHMENT_BODY)
                .inline(testFile)
                .body(ANOTHER_BODY_TEXT)
                .send();

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
