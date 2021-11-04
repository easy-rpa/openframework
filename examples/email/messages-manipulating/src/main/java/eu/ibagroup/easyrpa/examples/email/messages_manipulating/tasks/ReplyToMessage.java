package eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Reply to Message")
@Slf4j
public class ReplyToMessage extends ApTask {

    private static final String REPLY_MESSAGE_TEXT = "EasyRPA robot replied to this email.";
    private static final String REPLY_ALL_MESSAGE_TEXT = "EasyRPA robot replied to all participants of this email.";

    @Inject
    private EmailSender emailSender;

    @Input("message")
    private EmailMessage msg;

    @Override
    public void execute() {

        if (msg != null) {
            log.info("Reply to sender of input message.");
//            emailSender.send(msg.replyMessage().body(REPLY_MESSAGE_TEXT));

            log.info("Reply to all participants of input message.");
//            msg.replyAllMessage().body(REPLY_ALL_MESSAGE_TEXT).send(emailSender);
        } else {
            log.info("Message is not provided. Skip replying.");
        }
    }
}
