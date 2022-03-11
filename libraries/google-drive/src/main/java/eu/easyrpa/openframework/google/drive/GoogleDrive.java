package eu.easyrpa.openframework.google.drive;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.google.drive.constants.GDriveConfigParam;
import eu.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;
import eu.easyrpa.openframework.google.drive.model.GFile;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.drive.model.GFileType;
import eu.easyrpa.openframework.google.drive.service.GoogleDriveService;
import eu.easyrpa.openframework.google.services.AuthorizationPerformer;
import eu.easyrpa.openframework.google.services.GoogleServicesProvider;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service that provides convenient way to work with Google Drive API.
 */
public class GoogleDrive {

    /**
     * List of extra fields that should be present in responses of Drive API requests.
     */
    private List<String> extraFileFields;

    /**
     * Whether Google Drive API requests should also lookup files and folders in Shared Drives.
     */
    private Boolean supportsTeamDrives;

    /**
     * Helper to perform authorization for using of Google Drive API on behalf of specific user and instantiation
     * of Google Drive service.
     */
    private GoogleServicesProvider googleServicesProvider;

    /**
     * Wrapped Google Drive service
     */
    private GoogleDriveService service;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Default constructor for GoogleDrive.
     * <p>
     * This constructor should be used in case of manual providing of secret information necessary for authorization
     * and instantiation Google Drive service. E.g.:
     * <pre>
     *  String secretJson = new String(Files.readAllBytes(Paths.get("secret.json")), StandardCharsets.UTF_8);
     *
     *  GoogleDrive googleDrive = new GoogleDrive().secret("user1", secretJson);
     * {@code List<GFileInfo> file = googleDrive.listFiles();}
     *  ...
     * </pre>
     */
    public GoogleDrive() {
        googleServicesProvider = new GoogleServicesProvider();
    }

    /**
     * Constructs GoogleDrive with provided {@link RPAServicesAccessor}.
     * <p>
     * This constructor is used in case of injecting of this GoogleDrive using {@link Inject} annotation.
     * This is preferable way of working with this class. E.g.:
     * <pre>
     * {@code @Inject}
     *  private GoogleDrive googleDrive;
     *
     *  public void execute() {
     *      ...
     *     {@code List<GFileInfo> file = googleDrive.listFiles();}
     *      ...
     *  }
     * </pre>
     *
     * @param rpaServices instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     */
    @Inject
    public GoogleDrive(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
        googleServicesProvider = new GoogleServicesProvider(rpaServices);
    }

    /**
     * Allows to override the way how this code informs the user that it wishes to act on his behalf and obtain
     * corresponding access token from Google.
     * <p>
     * By default it opens a browser on machine where this code is running and locates to OAuth consent page where
     * user should authorize performing of necessary operations. If this code is running on robot's machine performing
     * of authorization by this way is not possible since user won't able to see the browser page.
     * <p>
     * Using this method is possible to overrides this behavior and specify, lets say, sending of notification email
     * with link to OAuth consent page to administrator, who is able to perform authorization on behalf of robot's
     * Google account. In this case robot will be able to access Google services on behalf of his account. Any time
     * when access token is invalid administrator will get such email and let robot to continue his work. E.g.:
     * <pre>
     * {@code @Inject}
     * SomeAuthorizationRequiredEmail authorizationRequiredEmail;
     * ...
     *
     * googleDrive.onAuthorization(url->{
     *    authorizationRequiredEmail.setConsentPage(url).send();
     * });
     *
     * ...
     * </pre>
     *
     * @param authorizationPerformer lambda expression or instance of {@link AuthorizationPerformer} that defines
     *                               specific behavior of authorization step.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleDrive onAuthorization(AuthorizationPerformer authorizationPerformer) {
        googleServicesProvider.onAuthorization(authorizationPerformer);
        service = null;
        return this;
    }

    /**
     * Sets explicitly the alias of secret vault entry with OAuth 2.0 Client JSON necessary for authentication on the
     * Google server.
     * <p>
     * For information regarding how to configure OAuth 2.0 Client see
     * <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a>
     *
     * @param vaultAlias the alias of secret vault entry with OAuth 2.0 Client JSON to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleDrive secret(String vaultAlias) {
        googleServicesProvider.secret(vaultAlias);
        return this;
    }

    /**
     * Sets explicitly the secret OAuth 2.0 Client JSON necessary for authentication on the Google server.
     * <p>
     * The OAuth 2.0 Client JSON look like the following:
     * <pre>
     * {
     *     "installed": {
     *       "client_id": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com",
     *       "project_id": "XXXXXXX-XXXXXX",
     *       "auth_uri": "https://accounts.google.com/o/oauth2/auth",
     *       "token_uri": "https://oauth2.googleapis.com/token",
     *       "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
     *       "client_secret": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
     *       "redirect_uris": [
     *           "urn:ietf:wg:oauth:2.0:oob",
     *           "http://localhost"
     *       ]
     *     }
     * }
     * </pre>
     * For information regarding how to configure OAuth 2.0 Client see
     * <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a><br>
     *
     * @param userId user unique identifier that will be associated with secret information. This is is used as key
     *               to store access token in StoredCredentials file.
     * @param secret JSON string with secret information to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleDrive secret(String userId, String secret) {
        googleServicesProvider.secret(userId, secret);
        service = null;
        return this;
    }

    /**
     * Gets the list of extra fields that should be present in responses of Google Drive API requests.
     * <p>
     * If the list is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b>{@code "google.drive.extra.file.fields"}</b>. Fields in value of this
     * configuration parameter should be split by {@code ";"} delimiter.
     *
     * @return the list of field names that should also be present in responses of Google Drive API requests or empty
     * if such fields are not specified.
     */
    public List<String> getExtraFileFields() {
        if (extraFileFields == null) {
            String extraFields = getConfigParam(GDriveConfigParam.EXTRA_FILE_FIELDS);
            if (extraFields != null) {
                extraFileFields = Arrays.stream(extraFields.split(";")).collect(Collectors.toList());
            } else {
                extraFileFields = new ArrayList<>();
            }
        }
        return extraFileFields;
    }

    /**
     * Adds extra fields that should be present in responses of Google Drive API requests.
     * <p>
     * See <a href="https://developers.google.com/drive/api/v3/reference/files">Drive API reference files</a> for
     * the full list of possible fields.
     *
     * @param extraFileFields list of fields that should be present in responses of Google Drive API requests.
     */
    public void setExtraFileFields(List<String> extraFileFields) {
        this.extraFileFields = extraFileFields;
        if (service != null) {
            service.setExtraFileFields(this.extraFileFields);
        }
    }

    /**
     * Adds extra fields that should be present in responses of Google Drive API requests.
     * <p>
     * See <a href="https://developers.google.com/drive/api/v3/reference/files">Drive API reference files</a> for
     * the full list of possible fields.
     *
     * @param fields name of fields that should be present in responses of Google Drive API requests.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleDrive extraFields(String... fields) {
        setExtraFileFields(Arrays.stream(fields).collect(Collectors.toList()));
        return this;
    }

    /**
     * Gets whether Google Drive API requests should also lookup files and folders in Shared Drives.
     * <p>
     * If this parameter is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b>{@code "google.drive.support.team.drives"}</b>.
     *
     * @return {@code true} if Shared Drives also be looked up and {@code false} otherwise.
     */
    public boolean isSupportsTeamDrives() {
        if (supportsTeamDrives == null) {
            String supportsTeamDrivesStr = getConfigParam(GDriveConfigParam.SUPPORT_TEAM_DRIVES);
            supportsTeamDrives = supportsTeamDrivesStr == null || Boolean.getBoolean(supportsTeamDrivesStr);
        }
        return supportsTeamDrives;
    }

    /**
     * Sets whether Google Drive API requests should also lookup files and folders in Shared Drives.
     *
     * @param supportsTeamDrives set {@code true} to looked up file and folders in Shared Drives
     *                           and {@code false} otherwise.
     */
    public void setSupportsTeamDrives(boolean supportsTeamDrives) {
        this.supportsTeamDrives = supportsTeamDrives;
        if (service != null) {
            service.setSupportsTeamDrives(this.supportsTeamDrives);
        }
    }

    /**
     * Sets whether Google Drive API requests should also lookup files and folders in Shared Drives.
     *
     * @param supportsTeamDrives set {@code true} to looked up file and folders in Shared Drives
     *                           and {@code false} otherwise.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleDrive supportsTeamDrives(boolean supportsTeamDrives) {
        setSupportsTeamDrives(supportsTeamDrives);
        return this;
    }

    /**
     * Gets the list of all files available in Google Drive.
     * <p>
     * In order to get files from Shared Drives, set correspondent flag {@code setSupportsTeamDrives(true)}.
     *
     * @return list of {@link GFileInfo} objects representing files present in Google Drive.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public List<GFileInfo> listFiles() {
        initService();
        return this.service.listFiles(String.format("mimeType != '%s' ", GFileType.FOLDER.toString()), null);
    }

    /**
     * Gets the list of files matching by given file type.
     * <p>
     * In order to get files from Shared Drives, set correspondent flag {@code setSupportsTeamDrives(true)}
     *
     * @param fileType the file type to match.
     * @return list of {@link GFileInfo} objects representing found files.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public List<GFileInfo> listFiles(GFileType fileType) {
        initService();
        return this.service.listFiles(String.format("mimeType = '%s' ", fileType.getMimeType()), null);
    }

    /**
     * Gets the list of files under by given folder ID.
     * <p>
     * In order to get files from Shared Drives, set correspondent flag {@code setSupportsTeamDrives(true)}
     *
     * @param parentId the parent folder ID to lookup.
     * @return list of {@link GFileInfo} objects representing found files.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public List<GFileInfo> listFiles(GFileId parentId) {
        initService();
        return this.service.listFiles(
                String.format("'%s' in parents and mimeType != '%s' ", parentId.getId(), GFileType.FOLDER.toString()),
                null);
    }

    /**
     * Gets the list of files of given type under the given folder ID.
     * <p>
     * In order to get files from Shared Drives, set correspondent flag {@code setSupportsTeamDrives(true)}
     *
     * @param parentId the parent folder ID to lookup.
     * @param fileType the file type to match.
     * @return list of {@link GFileInfo} objects representing found files.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public List<GFileInfo> listFiles(GFileId parentId, GFileType fileType) {
        initService();
        return this.service.listFiles(
                String.format("'%s' in parents and mimeType = '%s' ", parentId.getId(), fileType.getMimeType()),
                null);
    }

    /**
     * Gets the list of files matching the given query.
     * <p>
     * In order to get files from Shared Drives, set correspondent flag {@code setSupportsTeamDrives(true)}
     * <p>
     * <b>NOTICE:</b> {@code query} parameter should contain same fields that are mentioned in {@code fileFields}
     * property. To add some extra fields into this property use the method {@link #extraFields(String...)}.
     * <p>
     * Query examples:
     * <pre>
     *      // list "jpeg" files only
     *     "mimeType='image/jpeg'"
     *
     *     // list files with a name containing the words "hello" and "world"
     *     "name contains 'hello' and name contains 'world'"
     * </pre>
     * <p>
     * See Drive API for more query details
     * <a href="https://developers.google.com/drive/api/v3/search-files#query_string_examples">Query string examples</a>
     *
     * @param query query string to match.
     * @return list of {@link GFileInfo} objects representing found files.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public List<GFileInfo> listFiles(String query) {
        return listFiles(query, null);
    }

    /**
     * Gets the list of files matching the given query.
     * <p>
     * In order to get files from Shared Drives, set correspondent flag {@code setSupportsTeamDrives(true)}
     * <p>
     * <b>NOTICE:</b> {@code query} parameter should contain same fields that are mentioned in {@code fileFields}
     * property. To add some extra fields into this property use the method {@link #extraFields(String...)}.
     * <p>
     * Query examples:
     * <pre>
     *      // list "jpeg" files only
     *     "mimeType='image/jpeg'"
     *
     *     // list files with a name containing the words "hello" and "world"
     *     "name contains 'hello' and name contains 'world'"
     * </pre>
     * <p>
     * See Drive API for more query details
     * <a href="https://developers.google.com/drive/api/v3/search-files#query_string_examples">Query string examples</a>
     *
     * @param query    query string to match.
     * @param pageSize the page size for single request, default value is {@code 100}.
     * @return list of {@link GFileInfo} objects representing found files.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public List<GFileInfo> listFiles(String query, Integer pageSize) {
        initService();
        return this.service.listFiles(query, pageSize);
    }

    /**
     * Gets Google Drive file and it's content by given file name.
     * <p>
     * <b>NOTICE:</b> Please note that the exported content is limited to 10MB.
     *
     * @param fileName the file name to get.
     * @return {@link GFile} object wrapped with {@link Optional} and representing requested file with it's content.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFile> getFile(String fileName) {
        initService();
        return this.service.getFile(this.service.getFileInfo(fileName).orElse(null));
    }

    /**
     * Gets Google Drive file and it's content by given file ID.
     * <p>
     * <b>NOTICE:</b> Please note that the exported content is limited to 10MB.
     *
     * @param fileId {@link GFileId} with file ID of file to get.
     * @return {@link GFile} object wrapped with {@link Optional} and representing requested file with it's content.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFile> getFile(GFileId fileId) {
        initService();
        return this.service.getFile(this.service.getFileInfo(fileId).orElse(null));
    }

    /**
     * Gets info of folder with given folder name.
     *
     * @param folderName the name of folder to get.
     * @return {@link Optional} object with {@link GFileInfo} representing found folder or empty if such folder
     * is not found.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> getFolder(String folderName) {
        initService();
        return this.service.getFileInfo(folderName).filter(f -> f.getFileType() == GFileType.FOLDER);
    }

    /**
     * Gets info of folder with given folder ID.
     *
     * @param folderId {@link GFileId} with file ID of folder to get.
     * @return {@link Optional} object with {@link GFileInfo} representing found folder info or empty if such folder
     * is not found.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> getFolder(GFileId folderId) {
        initService();
        return this.service.getFileInfo(folderId).filter(f -> f.getFileType() == GFileType.FOLDER);
    }

    /**
     * Gets info of file with given file ID.
     *
     * @param fileId {@link GFileId} with file ID of file to get.
     * @return {@link Optional} object with {@link GFileInfo} representing found file info or empty if such file
     * is not found.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> getFileInfo(GFileId fileId) {
        initService();
        return this.service.getFileInfo(fileId);
    }

    /**
     * Gets info of file with given file name.
     *
     * @param fileName the name of file name to get.
     * @return {@link Optional} object with {@link GFileInfo} representing found file info or empty if such file
     * is not found.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> getFileInfo(String fileName) {
        initService();
        return this.service.getFileInfo(fileName);
    }

    /**
     * Creates a blank file with given name and type under root folder.
     * <p>
     * <b>Usage Examples:</b>
     * Create file with name "README.txt":
     * <pre>
     * googleDrive.createFile("README.txt", GFileType.FILE);
     * </pre>
     * Create folder with name "Documents":
     * <pre>
     * googleDrive.createFile(Documents", GFileType.FOLDER);
     * </pre>
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param name the name of Google Drive file to create. This is not necessarily unique within a folder.
     * @param type the type of Google Drive file to create.
     * @return {@link Optional} object with {@link GFileInfo} representing created file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> create(String name, GFileType type) {
        return this.create(name, type, null);
    }

    /**
     * Creates a blank file with given name and type under specified folder.
     * <p>
     * In order to create file under My Drive root directory set {@code folderId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create file under Shared Drives.
     * <p>
     * <b>Usage Examples:</b>
     * Create file with name "README.txt" under some folder with ID "folderId":
     * <pre>
     * googleDrive.createFile("README.txt", GFileType.FILE, folderId);
     * </pre>
     * Create folder with name "Documents" in root location of My Drive:
     * <pre>
     * googleDrive.createFile("Documents", GFileType.FOLDER, null);
     * </pre>
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param name     the name of Google Drive file to create. This is not necessarily unique within a folder.
     * @param type     {@link GFileType} value representing the type of Google Drive file to create.
     * @param folderId the id of parent folder, or {@code null} then the file will be placed directly
     *                 in the user's My Drive folder
     * @return {@link Optional} object with {@link GFileInfo} representing created file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> create(String name, GFileType type, GFileId folderId) {
        if (name != null) {
            initService();
            return this.service.createFile(name, type, null, folderId);
        }
        return Optional.empty();
    }

    /**
     * Creates a copy of Google Drive file.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create file under Shared Drives.
     *
     * @param source     the source file info.
     * @param nameOfCopy the name of copied file.
     * @return {@link Optional} object with {@link GFileInfo} representing copied file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> copy(GFileInfo source, String nameOfCopy) {
        if (source != null && nameOfCopy != null) {
            initService();
            return this.service.copyFile(source, nameOfCopy);
        }
        return Optional.empty();
    }

    /**
     * Uploads given file to the root folder.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param file the {@link File} object representing file to upload.
     * @return {@link Optional} object with {@link GFile} representing uploaded file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFile> createFile(File file) {
        return createFile(file, null);
    }

    /**
     * Uploads given file to given folder.
     * <p>
     * In order to create file under My Drive root folder set {@code folderId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create file under Shared Drives.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param file     the {@link File} object representing file to upload.
     * @param folderId {@link GFileId} with file ID of destination folder where the file should be uploaded.
     * @return {@link Optional} object with {@link GFile} representing uploaded file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFile> createFile(File file, GFileId folderId) {
        if (file != null) {
            try {
                initService();
                FileContent fileContent = new FileContent(null, file);
                Optional<GFileInfo> newFile = this.service.createFile(file.getName(), GFileType.FILE, fileContent,
                        folderId);
                if (newFile.isPresent()) {
                    return Optional.of(new GFile(newFile.get(), fileContent.getInputStream()));
                }
            } catch (GoogleDriveException e) {
                throw e;
            } catch (Exception e) {
                throw new GoogleDriveException(String.format("Creating of file '%s' has failed.", file.getName()), e);
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a file with given name and content under the root folder.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param fileName the name of file to create. This is not necessarily unique within a folder.
     * @param content  the input stream to read the content of creating file.
     * @return {@link Optional} object with {@link GFile} representing created file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFile> createFile(String fileName, InputStream content) {
        return createFile(fileName, content, null);
    }

    /**
     * Creates a file with given name and content under specified folder id.
     * <p>
     * In order to create file under My Drive root folder set {@code folderId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create file under Shared Drives.
     * <p>
     * <b>Usage Examples:</b>
     * Create file with name "README.txt" under the root folder:
     * <pre>
     * try (FileInputStream fis = new FileInputStream(file)) {
     *   googleDrive.createFile("README.txt", fis, null);
     * }
     * </pre>
     * Create file with name "README.txt" under folder "folderId":
     * <pre>
     * googleDrive.supportsTeamDrives(true)
     * try (FileInputStream fis = new FileInputStream(file)) {
     *   googleDrive.createFile("README.txt", fis, folderId);
     * }
     * </pre>
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param fileName the name of file to create. This is not necessarily unique within a folder.
     * @param content  the input stream to read the content of creating file.
     * @param folderId {@link GFileId} with file ID of target folder where the file should be created.
     * @return {@link Optional} object with {@link GFile} representing created file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFile> createFile(String fileName, InputStream content, GFileId folderId) {
        if (fileName != null && content != null) {
            initService();
            InputStreamContent fileContent = new InputStreamContent(null, content);
            Optional<GFileInfo> newFile = this.service.createFile(fileName, GFileType.FILE, fileContent, folderId);
            if (newFile.isPresent()) {
                return Optional.of(new GFile(newFile.get(), fileContent.getInputStream()));
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a folder with given name under the root folder.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param folderName the name of folder to create. This is not necessarily unique within a folder.
     * @return {@link Optional} object with {@link GFileInfo} representing created folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> createFolder(String folderName) {
        return createFolder(folderName, null);
    }

    /**
     * Creates a folder with given name under the root folder if it is absent.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param folderName the name of folder to create. This is not necessarily unique within a folder.
     * @return {@link GFileInfo} representing created or existed folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public GFileInfo createFolderIfAbsent(String folderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        return folder.orElseGet(() -> createFolder(folderName).orElse(null));
    }

    /**
     * Creates a folder with given name under specified folder.
     * <p>
     * In order to create folder under the root folder use {@link #createFolderIfAbsent(String)}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create folder under Shared Drives.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param folderName the name of folder to create. This is not necessarily unique within a folder.
     * @param parentId   {@link GFileId} with file ID of target folder where the folder should be created.
     * @return {@link Optional} object with {@link GFileInfo} representing created folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public Optional<GFileInfo> createFolder(String folderName, GFileId parentId) {
        if (folderName != null) {
            initService();
            return this.service.createFile(folderName, GFileType.FOLDER, null, parentId);
        }
        return Optional.empty();
    }

    /**
     * Creates a folder with given name under specified folder if it is absent.
     * <p>
     * In order to create folder under the root folder use {@link #createFolderIfAbsent(String)}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create folder under Shared Drives.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param folderName the name of folder to create. This is not necessarily unique within a folder.
     * @param parentId   {@link GFileId} with file ID of target folder where the folder should be created.
     * @return {@link GFileInfo} representing created or existed folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public GFileInfo createFolderIfAbsent(String folderName, GFileId parentId) {
        Optional<GFileInfo> folder = getFolder(folderName);
        return folder.orElseGet(() -> createFolder(folderName, parentId).orElse(null));
    }

    /**
     * Renames the folder.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create folder under Shared Drives.
     *
     * @param folderName    the name of target folder that should be renamed.
     * @param newFolderName new name of folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void renameFolder(String folderName, String newFolderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(fileInfo -> this.service.renameFile(fileInfo, newFolderName));
    }

    /**
     * Renames the folder.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to rename folder under Shared Drive.
     *
     * @param folder        the name of target folder that should be renamed.
     * @param newFolderName new name of folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void renameFolder(GFileInfo folder, String newFolderName) {
        renameFile(folder, newFolderName);
    }

    /**
     * Renames the file.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to rename file under Shared Drive.
     *
     * @param file        {@link GFileInfo} representing file to rename.
     * @param newFileName new name of file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void renameFile(GFileInfo file, String newFileName) {
        initService();
        this.service.renameFile(file, newFileName);
    }

    /**
     * Updates a file's info.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} if file is located under Shared Drives.
     *
     * @param file {@link GFileInfo} to update on Google server.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void updateFile(GFileInfo file) {
        initService();
        this.service.updateFile(file);
    }

    /**
     * Moves folder with given name to another folder.
     * <p>
     * In order to move folder to the root folder set {@code targetFolderName} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to move folder under Shared Drives.
     *
     * @param folderName       the name of folder to move.
     * @param targetFolderName the name of destination folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void moveFolder(String folderName, String targetFolderName) {
        Optional<GFileInfo> targetFolder = getFolder(targetFolderName);
        targetFolder.ifPresent(targetFolderInfo -> moveFolder(folderName, targetFolderInfo.getFileId()));
    }

    /**
     * Moves folder with given name to another folder.
     * <p>
     * In order to move folder to the root folder set {@code targetFolderId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to move folder under Shared Drives.
     *
     * @param folderName     the name of folder to move.
     * @param targetFolderId {@link GFileId} with file ID of destination folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void moveFolder(String folderName, GFileId targetFolderId) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(folderInfo -> this.service.moveFile(folderInfo, targetFolderId));
    }

    /**
     * Moves file to another folder.
     * <p>
     * In order to move file to the root folder set {@code targetFolderId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to move file under Shared Drives.
     *
     * @param file           {@link GFileInfo} representing file to move.
     * @param targetFolderId {@link GFileId} with file ID of destination folder.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void moveFile(GFileInfo file, GFileId targetFolderId) {
        initService();
        this.service.moveFile(file, targetFolderId);
    }

    /**
     * Permanently deletes a file owned by the user without moving it to the trash.
     * <p>
     * If the file belongs to a Shared Drive the user must be an organizer on the parent and {@code supportsTeamDrives}
     * must be {@code true}.
     * <p>
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param fileId {@link GFileId} with file ID of file to delete.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void deleteFile(GFileId fileId) {
        initService();
        this.service.deleteFile(fileId);
    }

    /**
     * Permanently deletes a file owned by the user without moving it to the trash.
     * <p>
     * If the file belongs to a Shared Drive the user must be an organizer on the parent and {@code supportsTeamDrives}
     * must be {@code true}.
     * <p>
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param file {@link GFileInfo} representing file to delete.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void deleteFile(GFileInfo file) {
        initService();
        this.service.deleteFile(file.getFileId());
    }

    /**
     * Permanently deletes a folder and all descendants owned by the user are also deleted without moving it to the trash.
     * <p>
     * If the file belongs to a Shared Drive the user must be an organizer on the parent and {@code supportsTeamDrives}
     * must be {@code true}.
     * <p>
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param folderId {@link GFileId} with file ID of folder to delete.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void deleteFolder(GFileId folderId) {
        initService();
        this.service.deleteFile(folderId);
    }

    /**
     * Permanently deletes a folder and all descendants owned by the user are also deleted without moving it to the trash.
     * <p>
     * If the file belongs to a Shared Drive the user must be an organizer on the parent and {@code supportsTeamDrives}
     * must be {@code true}.
     * <p>
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param folderName the name of folder to delete.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void deleteFolder(String folderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(f -> this.service.deleteFile(f.getFileId()));
    }

    /**
     * Gets underlie Google Drive service for advanced usage.
     *
     * @return Google Drive service for advanced usage.
     */
    public Drive getDrive() {
        initService();
        return service.getDrive();
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns {@code null} if parameter is
     * not found or accessor is not defined.
     */
    protected String getConfigParam(String key) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getConfigParam(key);
        } catch (Exception e) {
            //do nothing
        }

        return result;
    }

    /**
     * Initialize google service by reading configuration parameters.
     */
    private void initService() {
        if (service == null) {
            service = new GoogleDriveService(googleServicesProvider.getService(Drive.class, DriveScopes.DRIVE));
            service.setExtraFileFields(getExtraFileFields());
            service.setSupportsTeamDrives(isSupportsTeamDrives());
        }
    }
}
