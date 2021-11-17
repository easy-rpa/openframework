package eu.ibagroup.easyrpa.openframework.googledrive.file;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.openframework.googledrive.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.folder.GoogleFolderInfo;

public class GoogleFileInfo extends GoogleFolderInfo {

    public GoogleFileInfo(File file) {
        super(file);
    }

    public FileType getFileType() {
        return FileType.getValue(file.getMimeType());
    }

    public void setType(FileType type) {
        file.setMimeType(FileType.toString(type));
    }
}
