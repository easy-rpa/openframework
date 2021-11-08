package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;

public interface OutboundEmailService {

    void send(EmailMessage message) throws EmailMessagingException;

}