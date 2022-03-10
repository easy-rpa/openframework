package eu.ibagroup.easyrpa.openframework.email.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Represents email attachment. It keeps data of file attached to specific email message.
 */
public class EmailAttachment {

    /**
     * Regex that helps to locate places in the text of email message body with inline images.
     */
    public static final Pattern INLINE_IMAGE_PLACEHOLDER_RE = Pattern.compile("\\[image\\s+\"([\\w.]+)\"\\s+width=(\\d+)\\s+height=(\\d+)]");

    /**
     * Regex that helps to locate places in the text of email message body with inline files.
     */
    public static final Pattern INLINE_ATTACHMENT_PLACEHOLDER_RE = Pattern.compile("\\[attachment\\s+\"([\\w.]+)\"]");

    /**
     * Generates the string that indicates a place in the text of email message body where the attached image with
     * given name and specific size should be inserted.
     * .
     *
     * @param fileName the name of attached to the email message image that should be inline in the text of this
     *                 message body.
     * @param width    the necessary width of the image in pixels.
     * @param height   the necessary height of the image in pixels.
     * @return the string that can be inserted into the text of email message body to indicate the place where the
     * image with given name should be inserted.
     */
    public static String getImagePlaceholder(String fileName, int width, int height) {
        return String.format("[image \"%s\" width=%s height=%s]", fileName, width, height);
    }

    /**
     * Generates the string that indicates a place in the text of email message body where the attached file with
     * given name should be inserted.
     *
     * @param fileName the name of attached to the email message file that should be inline in the text of this
     *                 message body.
     * @return the string that can be inserted into the text of email message body to indicate the place where the
     * file with given name should be inserted.
     */
    public static String getAttachmentPlaceholder(String fileName) {
        return String.format("[attachment \"%s\"]", fileName);
    }

    /**
     * The file name of this attachment.
     */
    private String fileName;

    /**
     * The MIME type of this attachment.
     */
    private String mimeType;

    /**
     * The file content of this attachment.
     */
    private byte[] content;

    /**
     * Constructs a mew EmailAttachment with provided file data.
     *
     * @param fileName      the name of file.
     * @param contentSource the {@link InputStream} that provides file content.
     * @param mimeType      the MIME type of file.
     */
    public EmailAttachment(String fileName, InputStream contentSource, String mimeType) {
        this.fileName = fileName;
        try {
            this.content = IOUtils.toByteArray(contentSource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mimeType = mimeType;
    }

    /**
     * Constructs a mew EmailAttachment with given file.
     *
     * @param path the {@link Path} to file.
     * @throws IOException in case of some errors during reading of file.
     */
    public EmailAttachment(Path path) throws IOException {
        Path file = path.getFileName();
        String fileName = file != null ? file.toString() : "";
        this.fileName = fileName;
        this.content = Files.readAllBytes(path);
        try {
            this.mimeType = Files.probeContentType(Paths.get(fileName.toLowerCase()));
            if (this.mimeType == null) {
                this.mimeType = "text/plain";
            }
        } catch (IOException e) {
            this.mimeType = "text/plain";
        }
    }

    /**
     * Constructs a mew EmailAttachment with provided file data.
     *
     * @param fileName the name of file.
     * @param content  the byte content of file.
     * @param mimeType the MIME type of file.
     */
    @JsonCreator
    public EmailAttachment(@JsonProperty("fileName") String fileName,
                           @JsonProperty("content") byte[] content,
                           @JsonProperty("mimeType") String mimeType) {
        this.fileName = fileName;
        this.content = content;
        this.mimeType = mimeType;
    }

    /**
     * Gets the file name of this attachment.
     *
     * @return string with file name.
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Gets the file content of this attachment as stream.
     *
     * @return {@link ByteArrayInputStream} with file content of this attachment.
     */
    @JsonIgnore
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    /**
     * Gets the file content of this attachment.
     *
     * @return the byte array with file content of this attachment.
     */
    public byte[] getContent() {
        return this.content;
    }

    /**
     * Gets MIME type of this attachment.
     *
     * @return string with MIME type of this attachment.
     */
    public String getMimeType() {
        return this.mimeType;
    }

    public String toString() {
        return "EmailAttachment [name=" + this.fileName + ", content size=" + this.content.length + ", mimeType=" + this.mimeType + ']';
    }
}