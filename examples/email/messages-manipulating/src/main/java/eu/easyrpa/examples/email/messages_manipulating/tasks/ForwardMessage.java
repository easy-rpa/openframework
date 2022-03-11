package eu.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.email.EmailClient;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Forward Message")
@Slf4j
public class ForwardMessage extends ApTask {

    private static final String FORWARDED_MESSAGE_TEXT = "This email has been forwarded by EasyRPA robot.";

    @Configuration(value = "forwarded.email.recipients")
    private String forwardedEmailRecipients;

    @Inject
    private EmailClient emailClient;

    @Inject
    private EmailSender emailSender;

    @Input("messageId")
    private String messageId;

    @Override
    public void execute() {
        log.info("Forwarding of message with id '{}'.", messageId);
        EmailMessage message = emailClient.fetchMessage(messageId);

        if (message != null) {
            log.info("Forward the message to '{}'.", forwardedEmailRecipients);
            emailSender.send(message.forwardMessage(true).recipients(forwardedEmailRecipients).html(FORWARDED_MESSAGE_TEXT));

        } else {
            log.error("Message with id '{}' is not found.", messageId);
        }
    }
}
