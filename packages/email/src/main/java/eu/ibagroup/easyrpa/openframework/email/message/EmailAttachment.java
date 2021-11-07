package eu.ibagroup.easyrpa.openframework.email.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.ibagroup.easyrpa.openframework.email.utils.EmailUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class EmailAttachment {

    public static final Pattern INLINE_IMAGE_PLACEHOLDER_RE = Pattern.compile("\\[image\\s+\"([\\w.]+)\"\\s+width=(\\d+)\\s+height=(\\d+)]");
    public static final Pattern INLINE_ATTACHMENT_PLACEHOLDER_RE = Pattern.compile("\\[attachment\\s+\"([\\w.]+)\"]");

    public static String getImagePlaceholder(String fileName, int width, int height) {
        return String.format("[image \"%s\" width=%s height=%s]", fileName, width, height);
    }

    public static String getAttachmentPlaceholder(String fileName) {
        return String.format("[attachment \"%s\"]", fileName);
    }

    private String fileName;

    private String mimeType;

    private byte[] content;

    public EmailAttachment(String fileName, InputStream contentSource, String mimeType) {
        this.fileName = fileName;
        try {
            this.content = IOUtils.toByteArray(contentSource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mimeType = mimeType;
    }

    public EmailAttachment(Path path) throws IOException {
        Path file = path.getFileName();
        String fileName = file != null ? file.toString() : "";
        this.fileName = fileName;
        this.content = Files.readAllBytes(path);
        this.mimeType = EmailUtils.detectMimeType(fileName);
    }

    @JsonCreator
    public EmailAttachment(@JsonProperty("fileName") String fileName,
                           @JsonProperty("content") byte[] content,
                           @JsonProperty("mimeType") String mimeType) {
        this.fileName = fileName;
        this.content = content;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return this.fileName;
    }

    @JsonIgnore
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public byte[] getContent() {
        return this.content;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String toString() {
        return "EmailAttachment [name=" + this.fileName + ", content size=" + this.content.length + ", mimeType=" + this.mimeType + ']';
    }
}