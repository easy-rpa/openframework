package eu.ibagroup.easyrpa.examples.email.simple_message_sending.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.Email;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Send Email Message")
@Slf4j
public class SendEmailMessage extends ApTask {

    private static final String SUBJECT = "Test email";
    private static final String BODY = "This message was sent by EasyRPA Bot.";

    @Configuration(value = "email.service")
    private String emailService;

    @Configuration(value = "email.service.protocol")
    private String emailServiceProtocol;

    @Configuration(value = "email.recipients")
    private String simpleEmailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private RPAServicesAccessor rpaServices;

    @Override
    public void execute() {

        log.info("Send simple email message to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                simpleEmailRecipients, emailService, emailServiceProtocol, emailUserCredentials.getUser());

        Email.create().service(emailService).serviceProtocol(emailServiceProtocol)
                .credentials(emailUserCredentials.getUser(), emailUserCredentials.getPassword())
                .recipients(simpleEmailRecipients)
                .subject(SUBJECT).body(BODY).send();

        log.info("Send the same message using RPA services accessor.");

        Email.create(rpaServices).subject(SUBJECT).body(BODY).send();

        log.info("Messages have been sent successfully");
    }
}
