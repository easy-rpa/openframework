package eu.ibagroup.easyrpa.examples.email.message_sending_with_attachments.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAttachment;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@ApTaskEntry(name = "Send Message with Attachment")
@Slf4j
public class SendMessageWithAttachment extends ApTask {

    private static final String PATH_TO_FILE = "/Test.xlsx";
    private static final String ATTACHMENT_SUBJECT = "Test email with attachment";
    private static final String ATTACHMENT_BODY = "This message was sent by EasyRPA Bot and has attached file.";

    private static final String PATH_TO_IMAGE = "/Image.png";
    private static final String INLINE_ATTACHMENT_SUBJECT = "Test email with inline attachment";
    private static final String INLINE_ATTACHMENT_BODY_TPL = "This message was sent by EasyRPA Bot and " +
            "includes an image: <br> %s <br> Some text in the end.";

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
        log.info("Send email messages to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, outboundEmailServer, outboundEmailProtocol, emailUserCredentials.getUser());

        log.info("Read '{}' file.", PATH_TO_FILE);
        File testFile = readResourceFile(PATH_TO_FILE);

        log.info("Send message with attached file.");
        new EmailMessage(emailSender).subject(ATTACHMENT_SUBJECT).html(ATTACHMENT_BODY).attach(testFile).send();

        log.info("Read '{}' image.", PATH_TO_IMAGE);
        File imageFile = readResourceFile(PATH_TO_IMAGE);

        log.info("Send message with attached image in the body.");
        String body = String.format(INLINE_ATTACHMENT_BODY_TPL, EmailAttachment.getImagePlaceholder(imageFile.getName(), 541, 391));
        new EmailMessage(emailSender).subject(INLINE_ATTACHMENT_SUBJECT).html(body).attach(imageFile).send();

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
