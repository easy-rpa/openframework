package eu.easyrpa.openframework.google.drive.model;

import com.google.api.services.drive.model.File;

/**
 * Keeps and representing unique identifier of Google Drive file.
 *
 * @see File#getId()
 */
public class GFileId {

    /**
     * Raw unique identifier of Google Drive file.
     */
    private String id;

    /**
     * Constructs a new {@code GFileId}.
     *
     * @param id string value of Google Drive file ID.
     */
    public GFileId(String id) {
        this.id = id;
    }

    /**
     * Gets Google Drive file ID.
     *
     * @return string with Google Drive file ID.
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Convert string presentation of Google Drive file ID into {@code GFileId} object.
     *
     * @param id string value of Google Drive file ID.
     * @return {@code GFileId} object with given ID;
     */
    public static GFileId of(String id) {
        return new GFileId(id);
    }
}
