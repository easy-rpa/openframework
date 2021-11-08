package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.EmailMessage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MessageConverter<M> {

    public abstract M convertToNativeMessage(EmailMessage var1);

    public abstract EmailMessage convertToEmailMessage(M var1);

    public List<M> convertAllToNativeMessages(List<EmailMessage> emailMessages) {
        return emailMessages.stream().map(this::convertToNativeMessage).collect(Collectors.toList());
    }

    public List<M> convertAllToNativeMessages(EmailMessage[] emailMessages) {
        return this.convertAllToNativeMessages(Arrays.asList(emailMessages));
    }

    public List<EmailMessage> convertAllToEmailMessages(List<M> nativeMessages) {
        return nativeMessages.stream().map(this::convertToEmailMessage).collect(Collectors.toList());
    }

    public List<EmailMessage> convertAllToEmailMessages(M[] nativeMessages) {
        return this.convertAllToEmailMessages(Arrays.asList(nativeMessages));
    }
}