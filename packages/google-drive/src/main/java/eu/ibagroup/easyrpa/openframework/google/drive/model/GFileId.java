package eu.ibagroup.easyrpa.openframework.google.drive.model;

/**
 * GFileId has ID string value for @see {@link com.google.api.services.drive.model.File} object
 */
public class GFileId {

    /**
     * File id of a google entity.
     */
    private String id;

    /**
     * Construct a new GFileId object from the given id
     *
     * @param id string value of Google file model
     * @see com.google.api.services.drive.model.File
     */
    public GFileId(String id) {
        this.id = id;
    }

    /**
     * The Google file id.
     *
     * @return id string value
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Convert file id into GFileId object.
     *
     * @param id string value of Google file model
     * @return GFileId object with given id
     */
    public static GFileId of(String id) {
        return new GFileId(id);
    }
}
