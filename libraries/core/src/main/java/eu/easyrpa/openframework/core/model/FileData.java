package eu.easyrpa.openframework.core.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Keeps content of the file.
 */
public class FileData {

    /**
     * Name of the file.
     */
    protected String fileName;

    /**
     * The MIME type of file.
     */
    protected String mimeType;

    /**
     * The content of the file.
     */
    protected byte[] content;

    /**
     * Default constructor
     */
    protected FileData() {
    }

    /**
     * Constructs a mew FileData.
     *
     * @param fileName the name of file.
     * @param content  the byte content of file.
     * @param mimeType the MIME type of file.
     */
    public FileData(String fileName, byte[] content, String mimeType) {
        this.fileName = fileName;
        this.content = content;
        this.mimeType = mimeType;
    }

    /**
     * Gets name of the file.
     *
     * @return string with file name.
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Gets file content as stream.
     *
     * @return {@link ByteArrayInputStream} with file content.
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    /**
     * Gets file content.
     *
     * @return the byte array with file content.
     */
    public byte[] getContent() {
        return this.content;
    }

    /**
     * Gets the length of file content.
     *
     * @return length in bytes of file content.
     */
    public long getLength() {
        return this.content.length;
    }

    /**
     * Gets MIME type of file.
     *
     * @return string with MIME type of file.
     */
    public String getMimeType() {
        return this.mimeType;
    }

    public String toString() {
        return "FileData [name=" + this.fileName + ", content size=" + this.content.length + ", mimeType=" + this.mimeType + ']';
    }
}
