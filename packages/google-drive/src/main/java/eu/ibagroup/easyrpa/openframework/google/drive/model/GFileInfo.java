package eu.ibagroup.easyrpa.openframework.google.drive.model;

import com.google.api.services.drive.model.File;

import java.util.List;

public class GFileInfo {

    protected File file;

    public GFileInfo(File file) {
        this.file = file;
    }

    public String getId() {
        return file.getId();
    }

    public GFileId getFileId() {
        return new GFileId(file.getId());
    }

    public String getName() {
        return file.getName();
    }

    public void setName(String name) {
        file.setName(name);
    }

    public String getDescription() {
        return file.getDescription();
    }

    public void setDescription(String description) {
        file.setDescription(description);
    }

    public List<String> getParents() {
        return file.getParents();
    }

    public void setParents(List<String> list) {
        file.setParents(list);
    }

    public Long getSize() {
        return file.getSize();
    }

    public GFileType getFileType() {
        return GFileType.getValue(file.getMimeType());
    }

    public void setType(GFileType type) {
        file.setMimeType(type.getMimeType());
    }

    public File getGoogleFile() {
        return file;
    }
}
