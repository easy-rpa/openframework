package eu.ibagroup.easyrpa.openframework.google.drive.model;

import eu.ibagroup.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GFile extends GFileInfo {

    private InputStream contentIS;
    private byte[] bytes;

    public GFile(GFileInfo file, InputStream contentIS) {
        super(file.file);
        this.contentIS = contentIS;
    }

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

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public InputStream getContent() {
        if(contentIS == null && bytes != null){
            return new ByteArrayInputStream(bytes);
        }
        return contentIS;
    }
}
