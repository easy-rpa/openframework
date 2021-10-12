package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;

import java.util.List;

public interface EmailSender {
    void sendEmail(EmailMessage message) throws EmailMessagingException;

    void sendEmail(String from, String to, String subject, String body) throws EmailMessagingException;

    void sendEmail(String from, List<String> to, String subject, String body) throws EmailMessagingException;
}