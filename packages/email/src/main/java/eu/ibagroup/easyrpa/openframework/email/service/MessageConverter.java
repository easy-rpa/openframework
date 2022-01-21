package eu.ibagroup.easyrpa.openframework.email.service;

import eu.ibagroup.easyrpa.openframework.email.EmailMessage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basis class for converters that converts {@link EmailMessage} to the message supported by specific email
 * service implementation (native message). And vice versa.
 *
 * @param <M> type of the native message.
 */
public abstract class MessageConverter<M> {

    /**
     * Converts {@link EmailMessage} message to the native message.
     *
     * @param emailMessage the source {@link EmailMessage} to convert.
     * @return the instance of native message corresponding to the source {@link EmailMessage} message.
     */
    public abstract M convertToNativeMessage(EmailMessage emailMessage);

    /**
     * Converts the native message to {@link EmailMessage} message.
     *
     * @param nativeMessage the source native message to convert.
     * @return the {@link EmailMessage} corresponding to the source native message.
     */
    public abstract EmailMessage convertToEmailMessage(M nativeMessage);

    /**
     * Converts the list of {@link EmailMessage} messages to corresponding native messages.
     *
     * @param emailMessages the list of source {@link EmailMessage} messages to convert.
     * @return the list of native messages corresponding to source {@link EmailMessage} messages.
     */
    public List<M> convertAllToNativeMessages(List<EmailMessage> emailMessages) {
        return emailMessages.stream().map(this::convertToNativeMessage).collect(Collectors.toList());
    }

    /**
     * Converts array of {@link EmailMessage} messages to corresponding native messages.
     *
     * @param emailMessages the array of source {@link EmailMessage} messages to convert.
     * @return the list of native messages corresponding to source {@link EmailMessage} messages.
     */
    public List<M> convertAllToNativeMessages(EmailMessage[] emailMessages) {
        return this.convertAllToNativeMessages(Arrays.asList(emailMessages));
    }

    /**
     * Converts the list of native messages to corresponding {@link EmailMessage} messages.
     *
     * @param nativeMessages the list of source native messages to convert.
     * @return the list of {@link EmailMessage} messages corresponding to source native messages.
     */
    public List<EmailMessage> convertAllToEmailMessages(List<M> nativeMessages) {
        return nativeMessages.stream().map(this::convertToEmailMessage).collect(Collectors.toList());
    }

    /**
     * Converts the array of native messages to corresponding {@link EmailMessage} messages.
     *
     * @param nativeMessages the array of source native messages to convert.
     * @return the list of {@link EmailMessage} messages corresponding to source native messages.
     */
    public List<EmailMessage> convertAllToEmailMessages(M[] nativeMessages) {
        return this.convertAllToEmailMessages(Arrays.asList(nativeMessages));
    }
}