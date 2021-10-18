package eu.ibagroup.easyrpa.examples.send_simple_email.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.email.Email;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Send Email Message")
@Slf4j
public class SendEmailMessage extends ApTask {

    private static final String SUBJECT = "Test email";
    private static final String BODY = "This message was sent by EasyRPA Bot.";

    @Configuration(value = "email.service.credentials")
    private String emailServiceCredentials;

    @Configuration(value = "email.recipients")
    private String simpleEmailRecipients;

    @Inject
    private RPAServicesAccessor rpaServices;

    @Override
    public void execute() {
        log.info("Send simple email to '{}' from email box specified at '{}' alias in secret vault.", simpleEmailRecipients, emailServiceCredentials);
        Email.create(rpaServices).subject(SUBJECT).body(BODY).send();
    }
}
