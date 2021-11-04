package eu.ibagroup.easyrpa.openframework.email.utils;

import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.service.javax.MimeMessageConverter;
import org.apache.commons.io.FilenameUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EmailParsingUtils {

    public static EmailMessage parseEmlFile(String path) throws FileNotFoundException {
        String extension = FilenameUtils.getExtension(path);
        if (extension.equalsIgnoreCase("eml")) {
            return parseEmlFile(new FileInputStream(new File(path)));
        } else {
            throw new IllegalArgumentException("Wrong file extension: " + extension + ". Should be a 'eml' file");
        }
    }

    public static EmailMessage parseEmlFile(InputStream source) {
        Session session = Session.getDefaultInstance(System.getProperties(), null);
        try {
            MimeMessage message = new MimeMessage(session, source);
            MimeMessageConverter converter = new MimeMessageConverter(session);
            return converter.convertToEmailMessage(message);
        } catch (MessagingException e) {
            throw new EmailMessagingException(e);
        }
    }
}
