package eu.easyrpa.openframework.google.drive.model;

/**
 * Enumeration of Google file types.
 */

public enum GFileType {

    /**
     * Unknown file type.
     */
    FILE("", null),

    /**
     * Google Drive folder.
     */
    FOLDER("application/vnd.google-apps.folder", null),

    /**
     * Google Doc file.
     */
    DOCUMENT("application/vnd.google-apps.document", "text/plain"),

    /**
     * Google Spreadsheet file.
     */
    SPREADSHEET("application/vnd.google-apps.spreadsheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    /**
     * Google Slides file.
     */
    PRESENTATION("application/vnd.google-apps.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    /**
     * Google Drawing file.
     */
    DRAWING("application/vnd.google-apps.drawing", "image/png"),

    /**
     * Google Forms file.
     */
    FORM("application/vnd.google-apps.form", null),

    /**
     * Google My Maps file.
     */
    MAP("application/vnd.google-apps.map", null),

    /**
     * Audio file.
     */
    AUDIO("application/vnd.google-apps.audio", null),

    /**
     * Video file.
     */
    VIDEO("application/vnd.google-apps.video", null),

    /**
     * Photo file.
     */
    PHOTO("application/vnd.google-apps.photo", null),

    /**
     * Google Fusion Tables.
     */
    FUSIONTABLE("application/vnd.google-apps.fusiontable", null),

    /**
     * Google Apps Script file.
     */
    SCRIPT("application/vnd.google-apps.script", null),

    /**
     * Google Sites file.
     */
    SITE("application/vnd.google-apps.site", null);

    private final String mimeType;

    private final String contentType;

    /**
     * Constructs a new {@code GFileType} object.
     *
     * @param mimeType    string with name of related MIME type.
     * @param contentType string with name of related content type.
     */
    GFileType(String mimeType, String contentType) {
        this.mimeType = mimeType;
        this.contentType = contentType;
    }

    /**
     * Gets related MIME type.
     *
     * @return string with related MIME type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets related content type.
     *
     * @return string with related content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the {@code GFileType} based on specified MIME
     *
     * @param mimeType the string with necessary MIME type to lookup.
     * @return @code GFileType} constant related to specified MIME type or {@code null} if there are no related
     * constants.
     */
    public static GFileType getValue(String mimeType) {
        for (GFileType fileType : values()) {
            if (fileType.mimeType.equals(mimeType)) {
                return fileType;
            }
        }
        return null;
    }
}
