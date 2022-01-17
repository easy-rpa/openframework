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

/**
 * Utility for serialization/deserialization of email messages into text files of different formats.
 */
public class EmailParsingUtils {

    /**
     * Reads given file and parse it as EML file.
     *
     * @param path the string with path to EML file.
     * @return the {@link EmailMessage} representing email message contained in the given file.
     * @throws FileNotFoundException if given file is not found.
     */
    public static EmailMessage parseEmlFile(String path) throws FileNotFoundException {
        String extension = FilenameUtils.getExtension(path);
        if (extension.equalsIgnoreCase("eml")) {
            return parseEmlFile(new FileInputStream(new File(path)));
        } else {
            throw new IllegalArgumentException("Wrong file extension: " + extension + ". Should be a 'eml' file");
        }
    }

    /**
     * Reads and parse provided data as EML file.
     *
     * @param source the {@link InputStream} that provides data to read and parse.
     * @return the {@link EmailMessage} representing email message contained in provided data.
     */
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
