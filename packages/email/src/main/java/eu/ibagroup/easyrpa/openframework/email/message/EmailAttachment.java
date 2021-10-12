package eu.ibagroup.easyrpa.openframework.email.message;

import eu.ibagroup.easyrpa.openframework.email.utils.EmailUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class EmailAttachment implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String fileName;

    private final String mimeType;

    private final byte[] content;

    public EmailAttachment(String name, InputStream contentSource, String mimeType) {
        this.fileName = name;

        try {
            this.content = EmailUtils.readAllBytes(contentSource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.mimeType = mimeType;
    }

    public EmailAttachment(String name, byte[] content, String mimeType) {
        this.fileName = name;
        this.content = content != null ? (byte[]) content.clone() : new byte[0];
        this.mimeType = mimeType;
    }

    public EmailAttachment(String name, InputStream contentSource) {
        this(name, contentSource, EmailUtils.detectMimeType(name));
    }

    public EmailAttachment(String name, byte[] content) {
        this(name, content, EmailUtils.detectMimeType(name));
    }

    public EmailAttachment(Path path) throws IOException {
        Path file = path.getFileName();
        String fileName = file != null ? file.toString() : "";
        this.fileName = fileName;
        this.content = Files.readAllBytes(path);
        this.mimeType = EmailUtils.detectMimeType(fileName);
    }

    public String getFileName() {
        return this.fileName;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public byte[] getContent() {
        return (byte[]) this.content.clone();
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EmailAttachment [name=").append(this.fileName).append(", content size=").append(this.content.length).append(", mimeType=").append(this.mimeType).append(']');
        return builder.toString();
    }
}