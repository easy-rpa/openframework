package eu.ibagroup.easyrpa.openframework.google.drive.model;

/**
 * GFileId has ID string value for @see {@link com.google.api.services.drive.model.File} object
 */
public class GFileId {

    /** File id of a google entity. */
    private String id;

    /**
     * Construct a new GFileId object from the given @see {@link com.google.api.services.drive.model.File} id
     *
     * @param id string value of @see {@link com.google.api.services.drive.model.File} id
     */
    public GFileId(String id) {
        this.id = id;
    }

    /**
     * The file id @see {@link com.google.api.services.drive.model.File}
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
}
