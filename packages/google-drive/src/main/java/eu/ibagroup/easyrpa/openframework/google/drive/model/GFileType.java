package eu.ibagroup.easyrpa.openframework.google.drive.model;

/**
 * Enumeration of Google file types.
 */

public enum GFileType {

    /**
     * File type of uknown file
     */
    FILE("", null),

    /**
     * File type of Google Drive folder
     */
    FOLDER("application/vnd.google-apps.folder", null),

    /**
     * File type of Google Docs
     */
    DOCUMENT("application/vnd.google-apps.document", "text/plain"),

    /**
     * File type of Google Sheets
     */
    SPREADSHEET("application/vnd.google-apps.spreadsheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    /**
     * File type of Google Slides
     */
    PRESENTATION("application/vnd.google-apps.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    /**
     * File type of Google Drawing
     */
    DRAWING("application/vnd.google-apps.drawing", "image/png"),

    /**
     * File type of Google Forms
     */
    FORM("application/vnd.google-apps.form", null),

    /**
     * File type of Google My Maps
     */
    MAP("application/vnd.google-apps.map", null),

    /**
     * File type of audio file
     */
    AUDIO("application/vnd.google-apps.audio", null),

    /**
     * File type of video file
     */
    VIDEO("application/vnd.google-apps.video", null),

    /**
     * File type of photo file
     */
    PHOTO("application/vnd.google-apps.photo", null),

    /**
     * File type of Google Fusion Tables
     */
    FUSIONTABLE("application/vnd.google-apps.fusiontable", null),

    /**
     * File type of Google Apps Scripts
     */
    SCRIPT("application/vnd.google-apps.script", null),

    /**
     * File type of Google Sites
     */
    SITE("application/vnd.google-apps.site", null);

    private final String mimeType;

    private final String contentType;

    /**
     * Construct a new Google File Type object from the given stings.
     *
     * @param mimeType    MIME type string.
     * @param contentType content type string.
     */
    GFileType(String mimeType, String contentType) {
        this.mimeType = mimeType;
        this.contentType = contentType;
    }

    /**
     * @return string value of MIME type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return string value of content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Return GFileType enum constant with specified string MIME value
     *
     * @param mimeType the string MIME value of the enum to be returned
     * @return enum constant with specified string MIME value,
     * or null if this enum has no constant for the specified string value
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
