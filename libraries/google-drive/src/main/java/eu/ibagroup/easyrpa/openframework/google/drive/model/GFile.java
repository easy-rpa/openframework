package eu.ibagroup.easyrpa.openframework.google.drive.model;

import eu.ibagroup.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * Representing Google Drive file and keeps and its content with meta information.
 */
public class GFile extends GFileInfo {

    /**
     * Input stream to read the content of this file.
     */
    private InputStream contentIS;

    /**
     * Cached content of this file.
     */
    private byte[] bytes;

    /**
     * Construct {@code GFile} with given file info and content.
     *
     * @param file      {@link GFileInfo} with file's meta information.
     * @param contentIS input stream to get content of this file.
     */
    public GFile(GFileInfo file, InputStream contentIS) {
        super(file.file);
        this.contentIS = contentIS;
    }

    /**
     * Gets cached content of this file if present, or reads it from input stream.
     *
     * @return the byte array with content of this file.
     */
    public byte[] getBytes() {
        if (bytes == null) {
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = contentIS.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                bytes = buffer.toByteArray();

                if (contentIS.markSupported()) {
                    contentIS.reset();
                } else {
                    contentIS = null;
                }

            } catch (Exception e) {
                throw new GoogleDriveException("Reading of file content has failed.", e);
            }
        }
        return bytes;
    }

    /**
     * Sets the content of this file as byte array.
     *
     * @param bytes byte array with file content to set.
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Gets the content of this file as input stream.
     *
     * @return the input stream with file content.
     */
    public InputStream getContent() {
        if (contentIS == null && bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        return contentIS;
    }
}
