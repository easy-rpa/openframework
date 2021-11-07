package eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
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
    private EmailClient emailClient;

    @Inject
    private EmailSender emailSender;

    @Input("messageId")
    private String messageId;

    @Override
    public void execute() {
        log.info("Replying on message with id '{}'.", messageId);
        EmailMessage message = emailClient.fetchMessage(messageId);

        if (message != null) {
            log.info("Reply to sender of message.");
            emailSender.send(message.replyMessage(true).html(REPLY_MESSAGE_TEXT));

            log.info("Reply to all participants of message.");
            message.replyAllMessage(true).html(REPLY_ALL_MESSAGE_TEXT).send(emailSender);

        } else {
            log.error("Message with id '{}' is not found.", messageId);
        }
    }
}
