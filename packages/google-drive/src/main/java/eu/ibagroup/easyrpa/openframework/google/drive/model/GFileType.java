package eu.ibagroup.easyrpa.openframework.google.drive.model;

public enum GFileType {

    FILE("", null),

    FOLDER("application/vnd.google-apps.folder", null),

    DOCUMENT("application/vnd.google-apps.document", "text/plain"),

    SPREADSHEET("application/vnd.google-apps.spreadsheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    PRESENTATION("application/vnd.google-apps.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    DRAWING("application/vnd.google-apps.drawing", "image/png"),

    FORM("application/vnd.google-apps.form", null),

    MAP("application/vnd.google-apps.map", null),

    AUDIO("application/vnd.google-apps.audio", null),

    VIDEO("application/vnd.google-apps.video", null),

    PHOTO("application/vnd.google-apps.photo", null),

    FUSIONTABLE("application/vnd.google-apps.fusiontable", null),

    SCRIPT("application/vnd.google-apps.script", null),

    SITE("application/vnd.google-apps.site", null);

    private String mimeType;

    private String contentType;

    GFileType(String mimeType, String contentType) {
        this.mimeType = mimeType;
        this.contentType = contentType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getContentType() {
        return contentType;
    }

    public static GFileType getValue(String mimeType) {
        for (GFileType fileType : values()) {
            if (fileType.mimeType.equals(mimeType)) {
                return fileType;
            }
        }
        return null;
    }
}
