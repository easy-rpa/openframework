package eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Forward Message")
@Slf4j
public class ForwardMessage extends ApTask {

    private static final String FORWARDED_MESSAGE_TEXT = "This email has been forwarded by EasyRPA robot.";

    @Configuration(value = "forwarded.email.recipients")
    private String forwardedEmailRecipients;

    @Inject
    private EmailSender emailSender;

    @Input("message")
    private EmailMessage msg;

    @Override
    public void execute() {

        if (msg != null) {
            log.info("Forward the message to '{}'.", forwardedEmailRecipients);
//            emailSender.send(msg.forwardMessage().recipients(forwardedEmailRecipients).body(FORWARDED_MESSAGE_TEXT));
        } else {
            log.info("Message is not provided. Skip forwarding.");
        }
    }
}
