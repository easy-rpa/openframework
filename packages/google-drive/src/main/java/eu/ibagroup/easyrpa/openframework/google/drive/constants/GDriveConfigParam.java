package eu.ibagroup.easyrpa.openframework.google.drive.constants;

/**
 * The list of configuration parameter names which can be specified within RPA platform to provide necessary
 * for Google Drive service information.
 */
public class GDriveConfigParam {

    /**
     * Config parameter to provide list of fields for Google Service.
     *
     * @see <a href="https://developers.google.com/drive/api/v3/reference/files">API reference files</a>
     */
    public static final String EXTRA_FILE_FIELDS = "google.drive.extra.file.fields";

    /**
     * Config parameter to enable Google Service lookup through Team Drives too.
     *
     * @see <a href="https://developers.google.com/drive/api/v3/reference/files/list#includeTeamDriveItems">API reference files</a>
     */
    public static final String SUPPORT_TEAM_DRIVES = "google.drive.support.team.drives";

    private GDriveConfigParam() {
    }
}