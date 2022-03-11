package eu.easyrpa.openframework.google.drive.model;

import com.google.api.services.drive.model.File;

import java.util.List;

/**
 * Wraps Google Drive {@link File} object provides its parameters.
 */
public class GFileInfo {

    /**
     * Wrapped Google Drive file object.
     */
    protected File file;

    /**
     * Constructs a new {@code GFileInfo}.
     *
     * @param file Google Drive {@link File} object.
     */
    public GFileInfo(File file) {
        this.file = file;
    }

    /**
     * Gets file ID of this file as string.
     *
     * @return string with file ID of this file.
     */
    public String getId() {
        return file.getId();
    }

    /**
     * Gets file ID of this file as {@link GFileId} object.
     *
     * @return {@link GFileId} object with file ID of this file.
     */
    public GFileId getFileId() {
        return new GFileId(file.getId());
    }

    /**
     * Gets the name of this file.
     * <p>
     * This is not necessarily unique within a folder. Note that for immutable
     * items such as the top level folders of Team Drives, My Drive root folder, and Application Data
     * folder the name is constant.
     *
     * @return string with name of this file.
     */
    public String getName() {
        return file.getName();
    }

    /**
     * Sets new name for this file.
     * <p>
     * This is not necessarily unique within a folder. Note that for immutable
     * items such as the top level folders of Team Drives, My Drive root folder, and Application Data
     * folder the name is constant.
     *
     * @param name new name to set.
     */
    public void setName(String name) {
        file.setName(name);
    }

    /**
     * Gets a short description of this file.
     *
     * @return string with short description of this file or {@code null} if it's absent.
     */
    public String getDescription() {
        return file.getDescription();
    }

    /**
     * Sets new short description of this file.
     *
     * @param description string with new description to set.
     */
    public void setDescription(String description) {
        file.setDescription(description);
    }

    /**
     * Gets file IDs of the parent folders which contain the file.
     * <p>
     * If file is placed directly in the user's My Drive folder, then returns {@code null}
     *
     * @return list with file IDs of parent folders.
     */
    public List<String> getParents() {
        return file.getParents();
    }

    /**
     * Sets file IDs of the parent folders which contain the file.
     * <p>
     * If not specified as part of a create request, the file will be placed directly in the user's My Drive root
     * folder.
     *
     * @param list list with file IDs to set.
     */
    public void setParents(List<String> list) {
        file.setParents(list);
    }

    /**
     * Gets size of this file content in bytes.
     * <p>
     * This is only applicable to files with binary content in Drive.
     *
     * @return size of this file content in bytes.
     */
    public Long getSize() {
        return file.getSize();
    }

    /**
     * Gets the type of this file.
     * <p>
     * Google Drive will attempt to automatically detect an appropriate value from uploaded content if
     * no value is provided.
     *
     * @return {@link GFileType} constant related to the type of this file.
     */
    public GFileType getFileType() {
        return GFileType.getValue(file.getMimeType());
    }

    /**
     * Sets a new type for this file.
     * <p>
     * Google Drive will attempt to automatically detect an appropriate value from uploaded content if
     * no value is provided
     *
     * @param type {@link GFileType} constant representing necessary type to set.
     */
    public void setType(GFileType type) {
        file.setMimeType(type.getMimeType());
    }

    /**
     * Gets underlying Google Drive {@link File} object for advanced usage.
     *
     * @return underlying Google Drive {@link File} object for advanced usage.
     */
    public File getGoogleFile() {
        return file;
    }
}
