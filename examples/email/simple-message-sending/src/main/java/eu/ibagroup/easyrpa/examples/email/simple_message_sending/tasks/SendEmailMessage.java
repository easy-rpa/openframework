package eu.ibagroup.easyrpa.examples.email.simple_message_sending.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Send Email Message")
@Slf4j
public class SendEmailMessage extends ApTask {

    private static final String SUBJECT = "Test email";
    private static final String BODY = "This message was sent by EasyRPA Bot.";

    @Configuration(value = "outbound.email.server")
    private String outboundEmailServer;

    @Configuration(value = "outbound.email.protocol")
    private String outboundEmailProtocol;

    @Configuration(value = "email.recipients")
    private String simpleEmailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private EmailSender emailSender;

    @Override
    public void execute() {

        log.info("Send simple email message to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                simpleEmailRecipients, outboundEmailServer, outboundEmailProtocol, emailUserCredentials.getUser());

        log.info("Create message with Email Sender and then send it.");
        new EmailMessage(emailSender).subject(SUBJECT).text(BODY).send();

        log.info("Create message and then send it using Email Sender in the end.");
        new EmailMessage().subject(SUBJECT).text(BODY).send(emailSender);

        log.info("One more way to send the message using Email Sender.");
        emailSender.send(new EmailMessage().subject(SUBJECT).text(BODY));

        log.info("Messages have been sent successfully");
    }
}
