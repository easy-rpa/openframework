package eu.ibagroup.easyrpa.openframework.googledrive.folder;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.openframework.googledrive.file.Id;

import java.util.List;

public class GoogleFolderInfo {
    protected File file;

    public GoogleFolderInfo(File file) {
        this.file = file;
    }

    public String getName() {
        return file.getName();
    }

    public String getDescription() {
        return file.getDescription();
    }

    public Long getSize() {
        return file.getSize();
    }

    public Id getId() {
        return new Id(file.getId());
    }

    public List<String> getParents() {
        return file.getParents();
    }

    public void setName(String name) {
        file.setName(name);
    }

    public void setDescription(String description) {
        file.setDescription(description);
    }

    public void setParents(List<String> list) {
        file.setParents(list);
    }

}
