package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MessageConverter<M> {
    public MessageConverter() {
    }

    public abstract M createNativeMessage(EmailMessage var1);

    public abstract EmailMessage convertToEmailMessage(M var1);

    public List<M> createNativeMessages(List<EmailMessage> emailMessages) {
        return emailMessages.stream().map((m) -> {
            return this.createNativeMessage(m);
        }).collect(Collectors.toList());
    }

    public List<M> createNativeMessages(EmailMessage[] emailMessages) {
        return this.createNativeMessages(Arrays.asList(emailMessages));
    }

    public List<EmailMessage> convertAllToEmailMessage(List<M> nativeMessages) {
        return nativeMessages.stream().map((m) -> {
            return this.convertToEmailMessage(m);
        }).collect(Collectors.toList());
    }

    public List<EmailMessage> convertAllToEmailMessage(M[] nativeMessages) {
        return this.convertAllToEmailMessage(Arrays.asList(nativeMessages));
    }
}