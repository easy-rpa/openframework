package eu.easyrpa.openframework.email.message.tnef;

import net.freeutils.tnef.MAPIProp;
import net.freeutils.tnef.Message;
import net.freeutils.tnef.mime.MessageConverter;
import net.freeutils.tnef.mime.TNEFMimeMessage;

import javax.mail.MessagingException;
import java.io.IOException;

public class TNEFMailMessageConverter extends MessageConverter {

    @Override
    public TNEFMimeMessage convert(Message message, TNEFMimeMessage mime)
            throws IOException, MessagingException {

        TNEFMimeMessage result = super.convert(message, mime);
        if (result.getSubject() == null) {
            result.setSubject((String) message.getMAPIProps().getPropValue(MAPIProp.PR_CONVERSATION_TOPIC));
        }
        return result;
    }
}
