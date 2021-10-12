package eu.ibagroup.easyrpa.examples.send_simple_email.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.openframework.email.RobotEmail;


@ApTaskEntry(name = "Send Email using EmailUtils")
public class SendSimpleEmail extends ApTask {

    private static final String RECIPIENTS = "abc@gmail.com";

    private static final String SUBJECT = "Test email";

    private static final String EMAIL_SERVICE = "smtp.gmail.com:587";

    private static final String EMAIL_SERVICE_PROTOCOL = "smtp_over_tsl";

    private static final String SENDER_NAME = "EasyRPA Bot";

    private static final String BODY = "This message was sent by EasyRPA Bot.";

    @Override
    public void execute() {

        SecretCredentials secret = new SecretCredentials("example@gmail.com", "password");

        RobotEmail emailSender = new RobotEmail();
        emailSender.credentials(secret.getUser(), secret.getPassword());
        emailSender.setEmailService(EMAIL_SERVICE);
        emailSender.setEmailServiceProtocol(EMAIL_SERVICE_PROTOCOL);
        emailSender.setSender(secret.getUser());

        emailSender.subject(SUBJECT).recipients(RECIPIENTS).body(BODY).setSenderName(SENDER_NAME);
        emailSender.send();
    }
}
