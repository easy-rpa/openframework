package eu.ibagroup.easyrpa.openframework.googledrive.file;

import com.google.api.services.drive.model.File;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GoogleFile extends GoogleFileInfo {

    private ByteArrayOutputStream content;

    public GoogleFile(File file, ByteArrayOutputStream content) {
        super(file);
        this.content = content;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(content.toByteArray());
    }

    public void setContent(ByteArrayOutputStream content) {
        this.content = content;
    }
}
