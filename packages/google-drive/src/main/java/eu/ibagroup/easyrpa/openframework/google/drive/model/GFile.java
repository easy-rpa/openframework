package eu.ibagroup.easyrpa.openframework.google.drive.model;

import eu.ibagroup.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * A GFile object provides access to file and file info through @see {@link GFileInfo}, like id, name, description, size, type and parent ids.
 */
public class GFile extends GFileInfo {

    /**
     * Input stream with content of a file.
     */
    private InputStream contentIS;

    /**
     * Byte array content of a file.
     */
    private byte[] bytes;

    /**
     * Construct GFile object with given file info and file contents.
     *
     * @param file      file info
     * @param contentIS input stream contents
     * @see GFileInfo
     */
    public GFile(GFileInfo file, InputStream contentIS) {
        super(file.file);
        this.contentIS = contentIS;
    }

    /**
     * Gets the bytes if present, or reads the contents of input stream as a byte[].
     *
     * @return the requested array.
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
     * Byte array representation of file contents.
     *
     * @param bytes byte array.
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Converts the bytes to an input stream, encoded as bytes using the default character encoding of the platform.
     *
     * @return the input stream.
     */
    public InputStream getContent() {
        if (contentIS == null && bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        return contentIS;
    }
}
