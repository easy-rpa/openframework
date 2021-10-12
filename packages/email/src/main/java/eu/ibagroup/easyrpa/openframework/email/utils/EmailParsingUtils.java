package eu.ibagroup.easyrpa.openframework.email.utils;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailAddress;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.ews.MAPIMessageConverter;
import eu.ibagroup.easyrpa.openframework.email.service.javax.JavaxMimeMessageConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hsmf.MAPIMessage;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.List;
import java.util.Properties;

public class EmailParsingUtils {
    public EmailParsingUtils() {
    }

    public static EmailMessage parseEmlFile(String path) throws FileNotFoundException {
        String extension = FilenameUtils.getExtension(path);
        if (extension.equalsIgnoreCase("eml")) {
            File eml = new File(path);
            InputStream source = new FileInputStream(eml);
            return parseEmlFile(source, extension);
        } else {
            throw new IllegalArgumentException("Wrong file extension: " + extension + ". Should be a " + "eml" + " file");
        }
    }

    public static EmailMessage parseEmlFile(InputStream source, String extension) {
        if (extension.equalsIgnoreCase("eml")) {
            Properties props = System.getProperties();
            Session session = Session.getDefaultInstance(props, (Authenticator) null);

            try {
                MimeMessage message = new MimeMessage(session, source);
                JavaxMimeMessageConverter converter = new JavaxMimeMessageConverter(session);
                return converter.convertToEmailMessage(message);
            } catch (MessagingException e) {
                throw new EmailMessagingException(e);
            }
        } else {
            throw new IllegalArgumentException("Wrong file extension: " + extension + ". Should be a " + "eml" + " file");
        }
    }

    public static EmailMessage parseMsgFile(String path) throws IOException {
        String extension = FilenameUtils.getExtension(path);
        if (extension.equalsIgnoreCase("msg")) {
            MAPIMessage message = new MAPIMessage(path);
            MAPIMessageConverter converter = new MAPIMessageConverter();
            return converter.convertToEmailMessage(message);
        } else {
            throw new IllegalArgumentException("Wrong file extension " + extension + ". Should be a " + "msg" + " file");
        }
    }

    public static EmailMessage parseMsgFile(InputStream is, String extension) {
        if (extension.equalsIgnoreCase("msg")) {
            MAPIMessage message = null;

            try {
                message = new MAPIMessage(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            MAPIMessageConverter converter = new MAPIMessageConverter();
            return converter.convertToEmailMessage(message);
        } else {
            throw new IllegalArgumentException("Wrong file extension: " + extension + ". Should be a " + "msg" + " file");
        }
    }

    public static String emailsToString(List<EmailAddress> emails) {
        return emails == null || emails.isEmpty() ? "" : StringUtils.join(emails, ",");
    }
}
