package eu.ibagroup.easyrpa.openframework.google.drive.model;

import com.google.api.services.drive.model.File;

import java.util.List;

/**
 * A GFileInfo object summarises info about @see {@link File}, like id, name, description, size, type and parent ids.
 */

public class GFileInfo {

    protected File file;

    /**
     * Construct a new Google File Info object from the given @see {@link File}
     *
     * @param file - the File instance
     */
    public GFileInfo(File file) {
        this.file = file;
    }

    /**
     * The ID of the file.
     *
     * @return value or {@code null} for none
     */
    public String getId() {
        return file.getId();
    }

    /**
     * Return GField instance for the file
     *
     * @return the GField instance of the file
     */
    public GFileId getFileId() {
        return new GFileId(file.getId());
    }

    /**
     * The name of the file.
     * <p></p>
     * This is not necessarily unique within a folder. Note that for immutable
     * items such as the top level folders of Team Drives, My Drive root folder, and Application Data
     * folder the name is constant.
     *
     * @return value or {@code null} for none
     */
    public String getName() {
        return file.getName();
    }

    /**
     * The name of the file.
     * <p>
     * This is not necessarily unique within a folder. Note that for immutable
     * items such as the top level folders of Team Drives, My Drive root folder, and Application Data
     * folder the name is constant.
     *
     * @param name name or {@code null} for none
     */
    public void setName(String name) {
        file.setName(name);
    }

    /**
     * A short description of the file.
     *
     * @return value or {@code null} for none
     */
    public String getDescription() {
        return file.getDescription();
    }

    /**
     * A short description of the file.
     *
     * @param description description or {@code null} for none
     */
    public void setDescription(String description) {
        file.setDescription(description);
    }

    /**
     * The IDs of the parent folders which contain the file.
     * <p>
     * If file is placed directly in the user's My Drive folder, then returns {@code null}
     */
    public List<String> getParents() {
        return file.getParents();
    }

    /**
     * The IDs of the parent folders which contain the file.
     * <p>
     * If not specified as part of a create request, the file will be placed directly in the user's My Drive folder.
     *
     * @param list list or {@code null} for none
     */
    public void setParents(List<String> list) {
        file.setParents(list);
    }

    /**
     * The size of the file's content in bytes.
     * <p>
     * This is only applicable to files with binary content in Drive.
     *
     * @return value or {@code null} for none
     */
    public Long getSize() {
        return file.getSize();
    }

    /**
     * Google File Type constant for the file MIME type.
     * <p>
     * Google Drive will attempt to automatically detect an appropriate value from uploaded content if no value is provided.
     *
     * @return enum constant for the file MIME type,
     * or {@code null} if @see {@link GFileType} enumeration has not the file MIME type.
     */
    public GFileType getFileType() {
        return GFileType.getValue(file.getMimeType());
    }

    /**
     * Google File Type constant for the file MIME type.
     * <p>
     * Google Drive will attempt to automatically detect an appropriate value from uploaded content if no value is provided
     *
     * @param type type or {@code null} for none
     */
    public void setType(GFileType type) {
        file.setMimeType(type.getMimeType());
    }

    /**
     * @return the file @see {@link File}
     */
    public File getGoogleFile() {
        return file;
    }
}
