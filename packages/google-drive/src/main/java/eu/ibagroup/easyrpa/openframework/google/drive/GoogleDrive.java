package eu.ibagroup.easyrpa.openframework.google.drive;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.google.drive.constants.GDriveConfigParam;
import eu.ibagroup.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFile;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileType;
import eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.google.services.AuthorizationPerformer;
import eu.ibagroup.easyrpa.openframework.google.services.GoogleAuth;
import eu.ibagroup.easyrpa.openframework.google.services.GoogleServicesProvider;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GoogleDrive {

    /** List of extra fields for query in API requests */
    private List<String> extraFileFields;

    /** Enable support team drives in API requests */
    private Boolean supportsTeamDrives;

    /** Google Service Provider */
    private GoogleServicesProvider googleServicesProvider;

    /** Google Drive Service*/
    private GoogleDriveService service;

    /** Access to configuration parameters */
    private RPAServicesAccessor rpaServices;

    /**
     * Construct GoogleDrive without predefined configuration paramateres.
     */
    public GoogleDrive() {
        googleServicesProvider = new GoogleServicesProvider();
    }

    /**
     * Construct GoogleDrive instance using @see {@link RPAServicesAccessor} accessor
     *
     * @param rpaServices instance of RPAServicesAccessor
     */
    @Inject
    public GoogleDrive(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
        googleServicesProvider = new GoogleServicesProvider(rpaServices);
    }

    /**
     * Set authorization performer.
     * @param authorizationPerformer an authorization performer.
     * @return a self reference.
     */
    public GoogleDrive onAuthorization(AuthorizationPerformer authorizationPerformer) {
        googleServicesProvider.onAuthorization(authorizationPerformer);
        service = null;
        return this;
    }

    /**
     * Set secret vault alias.
     * @param vaultAlias new secret vault alias
     * @return a self reference.
     */
    public GoogleDrive secret(String vaultAlias) {
        googleServicesProvider.secret(vaultAlias);
        return this;
    }

    /**
     * Set secret by given user id and secret.
     * @param userId new user id.
     * @param secret new secret.
     * @return a self reference.
     */
    public GoogleDrive secret(String userId, String secret) {
        googleServicesProvider.secret(userId, secret);
        service = null;
        return this;
    }

    /**
     * Return list of extra fields if present or take from configuration parameter split by ";" delimiter.
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
     * Add extra fields for Google Service to include in results.
     *
     * @param extraFileFields list of fields.
     * @see Drive <a href="https://developers.google.com/drive/api/v3/reference/files">API reference files</a>
     */
    public void setExtraFileFields(List<String> extraFileFields) {
        this.extraFileFields = extraFileFields;
        if (service != null) {
            service.setExtraFileFields(this.extraFileFields);
        }
    }

    /**
     * Add extra fields for Google Service to include in results.
     *
     * @param fields array of fields.
     * @see Drive <a href="https://developers.google.com/drive/api/v3/reference/files">API reference files</a>
     * @return a self reference.
     */
    public GoogleDrive extraFields(String... fields) {
        setExtraFileFields(Arrays.stream(fields).collect(Collectors.toList()));
        return this;
    }

    /**
     * @return true if <code>supportsTeamDrives</code> is enabled, otherwise false.
     */
    public boolean isSupportsTeamDrives() {
        if (supportsTeamDrives == null) {
            String supportsTeamDrivesStr = getConfigParam(GDriveConfigParam.SUPPORT_TEAM_DRIVES);
            supportsTeamDrives = supportsTeamDrivesStr == null || Boolean.getBoolean(supportsTeamDrivesStr);
        }
        return supportsTeamDrives;
    }

    /**
     * Set <code>supportsTeamDrives</code> value.
     * @param supportsTeamDrives set true if files / folders located on Shared Drives, otherwise false.
     */
    public void setSupportsTeamDrives(boolean supportsTeamDrives) {
        this.supportsTeamDrives = supportsTeamDrives;
        if (service != null) {
            service.setSupportsTeamDrives(this.supportsTeamDrives);
        }
    }

    /**
     * Set <code>supportsTeamDrives</code> value.
     * @param supportsTeamDrives set true if files / folders located on Shared Drives, otherwise false.
     * @return a self reference
     */
    public GoogleDrive supportsTeamDrives(boolean supportsTeamDrives) {
        setSupportsTeamDrives(supportsTeamDrives);
        return this;
    }

    /**
     * Send request to get list of all files.
     * <p></p>
     * In order to get files from Shared Drives, set correspondent flag <code>setSupportsTeamDrives(true)</code>
     *
     * @return list of GFileInfo @see {@link GFileInfo} objects.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles() {
        initService();
        return this.service.listFiles(String.format("mimeType != '%s' ", GFileType.FOLDER.toString()), null);
    }

    /**
     * Send request to get list of files matching by given file type.
     * <p></p>
     * In order to get files from Shared Drives, set correspondent flag <code>setSupportsTeamDrives(true)</code>
     *
     * @param fileType the file's file type @see {@link GFileType}
     * @return list of found files.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles(GFileType fileType) {
        initService();
        return this.service.listFiles(String.format("mimeType = '%s' ", fileType.getMimeType()), null);
    }

    /**
     * Send request to get list of files under by given folder id.
     * <p></p>
     * In order to get files from Shared Drives, set correspondent flag <code>setSupportsTeamDrives(true)</code>
     *
     * @param parentId the folder's id @see {@link GFileId}
     * @return list of found files.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles(GFileId parentId) {
        initService();
        return this.service.listFiles(
                String.format("'%s' in parents and mimeType != '%s' ", parentId.getId(), GFileType.FOLDER.toString()),
                null);
    }

    /**
     * Send request to get list of files of given type under the given folder id.
     * <p></p>
     * In order to get files from Shared Drives, set correspondent flag <code>setSupportsTeamDrives(true)</code>
     *
     * @param parentId the folder's id @see {@link GFileId}
     * @param fileType the file's file type @see {@link GFileType}
     * @return list of found files.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles(GFileId parentId, GFileType fileType) {
        initService();
        return this.service.listFiles(
                String.format("'%s' in parents and mimeType = '%s' ", parentId.getId(), fileType.getMimeType()),
                null);
    }

    /**
     * Send request to get list of files matching the given query.
     * <p></p>
     * In order to get files from Shared Drives, set correspondent flag <code>setSupportsTeamDrives(true)</code>
     * <b>NOTICE:</b> <code>query</code> parameter should contain same list of fields as <code>fileFields</code> property.
     * Query examples:
     * <pre>
     *     <code>query = "mimeType='image/jpeg'"</code>> // list "jpeg" files only
     *     <code>query = "name contains 'hello' and name contains 'goodbye'"</code>// list files with a name containing the words "hello" and "goodbye"
     * </pre>
     * See Drive API for more query details <a href="https://developers.google.com/drive/api/v3/search-files#query_string_examples">Query string examples</a>
     *
     * @param query string query
     * @return list of found files.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles(String query) {
        return listFiles(query, null);
    }

    /**
     * Send request to get list of files matching the given query.
     * <p></p>
     * In order to get files from Shared Drives, set correspondent flag <code>setSupportsTeamDrives(true)</code>
     * <b>NOTICE:</b> <code>query</code> parameter should contain same list of fields as <code>fileFields</code> property.
     * Query examples:
     * <pre>
     *     <code>query = "mimeType='image/jpeg'"</code>> // list "jpeg" files only
     *     <code>query = "name contains 'hello' and name contains 'goodbye'"</code>// list files with a name containing the words "hello" and "goodbye"
     * </pre>
     * See Drive API for more query details <a href="https://developers.google.com/drive/api/v3/search-files#query_string_examples">Query string examples</a>
     *
     * @param query    string query
     * @param pageSize Integer page size for single request, default value is <code>100</code>
     * @return list of found files.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles(String query, Integer pageSize) {
        initService();
        return this.service.listFiles(query, pageSize);
    }

    /**
     * Send request to get file @see {@link GFile} of given file name.
     * <p></p>
     * <b>NOTICE:</b> Please note that the exported content is limited to 10MB.
     * If <code>fileName</code> is not specified or file not found, then returns <code>Optional.empty()</code>.
     *
     * @return wrapper <code>Optional.of(GFile))</code> for found file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFile> getFile(String fileName) {
        initService();
        return this.service.getFile(this.service.getFileInfo(fileName).orElse(null));
    }

    /**
     * Send request to get file @see {@link GFile} of given file id {@link GFileId}.
     * <p></p>
     * <b>NOTICE:</b> Please note that the exported content is limited to 10MB.
     * If <code>fileId</code> is not specified or file not found, then returns <code>Optional.empty()</code>.
     *
     * @param fileId the file's id object <code>GFileId</code> to get.
     * @return wrapper <code>Optional.of(GFile))</code> for found file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFile> getFile(GFileId fileId) {
        initService();
        return this.service.getFile(this.service.getFileInfo(fileId).orElse(null));
    }

    /**
     * Send request to get folder info @see {@link GFileInfo} of given folder name.
     * <p></p>
     * If <code>folderName</code> is not specified or folder not found, then returns <code>Optional.empty()</code>.
     *
     * @param folderName the folder's name to get.
     * @return wrapper <code>Optional.of(GFileInfo))</code> for found folder.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFileInfo> getFolder(String folderName) {
        initService();
        return this.service.getFileInfo(folderName).filter(f -> f.getFileType() == GFileType.FOLDER);
    }

    /**
     * Send request to get folder info @see {@link GFileInfo} of given folder id @see {@link GFileId}.
     * <p></p>
     * If <code>folderId</code> is not specified or folder not found, then returns <code>Optional.empty()</code>.
     *
     * @param folderId the folder's id to get.
     * @return wrapper <code>Optional.of(GFileInfo))</code> for found folder.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFileInfo> getFolder(GFileId folderId) {
        initService();
        return this.service.getFileInfo(folderId).filter(f -> f.getFileType() == GFileType.FOLDER);
    }

    /**
     * Send request to get file info @see {@link GFileInfo} of given fileId @see {@link GFileId}.
     * <p></p>
     * If <code>fileId</code> is not specified or file not found, then returns <code>Optional.empty()</code>.
     * Otherwise returns {@code Optional.of(GFileInfo)} if file is found.
     *
     * @param fileId GFileId object of the file.
     * @return wrapper <code>Optional.of(GFileInfo)</code> for found file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFileInfo> getFileInfo(GFileId fileId) {
        initService();
        return this.service.getFileInfo(fileId);
    }

    /**
     * Send request to get file info @see {@link GFileInfo} of given file name.
     * <p></p>
     * If <code>fileName</code> is not specified or file not found, then returns <code>Optional.empty()</code>.
     * Otherwise returns {@code Optional.of(GFileInfo)} if file is found.
     *
     * @param fileName the file's name to get.
     * @return wrapper <code>Optional.of(GFileInfo)</code> for found file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFileInfo> getFileInfo(String fileName) {
        initService();
        return this.service.getFileInfo(fileName);
    }

    /**
     * Create a blank file with given name and type under root directory My Drive.
     * <p></p>
     * <b>Usage Examples:</b>
     * Create file with name README.txt:
     * <pre>
     * this.createFile("README.txt" GFileType.FILE);
     * </pre>
     * Create folder with name "Documents":
     * <pre>
     * this.createFile(Documents", GFileType.FOLDER);
     * </pre>
     *
     * @param name The name of the file. This is not necessarily unique within a folder.
     *             Note that for immutable items such as the top level folders of shared drives,
     *             My Drive root folder, and Application Data folder the name is constant.
     * @param type The MIME type of the file.
     *             Google Drive will attempt to automatically detect an appropriate value from uploaded content if no value is provided.
     * @return wrapper <code>Optional.of(GFileInfo))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFileInfo> create(String name, GFileType type) {
        return this.create(name, type, null);
    }

    /**
     * Create a blank file with given name and type under specified folder id.
     * <p></p>
     * In order to create file under My Drive root directory set <code>folderId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to create file under Shared Drives.
     * <p></p>
     * <b>Usage Examples:</b>
     * Create file with name README.txt under some folder "folderId":
     * <pre>
     * this.createFile("README.txt" GFileType.FILE, folderId);
     * </pre>
     * Create folder with name "Documents" in root location of My Drive:
     * <pre>
     * this.createFile(Documents", GFileType.FOLDER, null);
     * </pre>
     *
     * @param name     The name of the file. This is not necessarily unique within a folder.
     *                 Note that for immutable items such as the top level folders of shared drives,
     *                 My Drive root folder, and Application Data folder the name is constant.
     * @param type     The MIME type of the file.
     *                 Google Drive will attempt to automatically detect an appropriate value from uploaded content if no value is provided.
     * @param folderId the id of parent folder, or null then the file will be placed directly in the user's My Drive folder
     * @return wrapper <code>Optional.of(GFileInfo))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFileInfo> create(String name, GFileType type, GFileId folderId) {
        if (name != null) {
            initService();
            return this.service.createFile(name, type, null, folderId);
        }
        return Optional.empty();
    }

    public Optional<GFileInfo> copy(GFileInfo source, String nameOfCopy) {
        if (source != null && nameOfCopy != null) {
            initService();
            return this.service.copyFile(source, nameOfCopy);
        }
        return Optional.empty();
    }

    /**
     * Upload given file to My Drive.
     *
     * @param file the abstract pathname for the named file.
     * @return wrapper <code>Optional.of(GFile))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFile> createFile(File file) {
        return createFile(file, null);
    }

    /**
     * Upload given file to given folder.
     * <p></p>
     * In order to create file under My Drive root directory set <code>folderId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to create file under Shared Drives.
     *
     * @param file     the abstract pathname for the named file.
     * @param folderId the folder's id to upload under.
     * @return wrapper <code>Optional.of(GFile))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
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
     * Create a file with given name and content under My Drive directory.
     * <p></p>
     * <b>Usage Examples:</b>
     * Create file with name README.txt:
     * <pre>
     *     try (FileInputStream fis = new FileInputStream(file)) {
     *          this.createFile("README.txt", fis);
     *     }
     * </pre>
     * Create folder with name "Documents" in root location of My Drive:
     * <pre>
     * this.createFile(Documents", GFileType.FOLDER, null);
     * </pre>
     *
     * @param fileName The name of the file. This is not necessarily unique within a folder.
     *                 Note that for immutable items such as the top level folders of shared drives,
     *                 My Drive root folder, and Application Data folder the name is constant.
     * @param content  the input stream to read from.
     * @return wrapper <code>Optional.of(GFile))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFile> createFile(String fileName, InputStream content) {
        return createFile(fileName, content, null);
    }

    /**
     * Create a file with given name and content under specified folder id.
     * <p></p>
     * In order to create file under My Drive root directory set <code>folderId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to create file under Shared Drives.
     * <b>Usage Examples:</b>
     * Create file with name README.txt under My Drive:
     * <pre>
     *     try (FileInputStream fis = new FileInputStream(file)) {
     *          this.createFile("README.txt", fis, null);
     *     }
     * </pre>
     * Create file with name "README.txt" under folder "folderId":
     * <pre>
     *     this.supportsTeamDrives(true)
     *     try (FileInputStream fis = new FileInputStream(file)) {
     *          this.createFile("README.txt", fis, folderId);
     *     }
     * </pre>
     *
     * @param fileName the name of the file. This is not necessarily unique within a folder.
     *                 Note that for immutable items such as the top level folders of shared drives,
     *                 My Drive root folder, and Application Data folder the name is constant.
     * @param content  the input stream to read from.
     * @param folderId folderId the folder's id to upload under.
     * @return wrapper <code>Optional.of(GFile))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
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
     * Create a folder with given name under My Drive directory.
     *
     * @param folderName the name of the folder. This is not necessarily unique within a folder.
     *                   Note that for immutable items such as the top level folders of shared drives,
     *                   My Drive root folder, and Application Data folder the name is constant.
     * @return wrapper <code>Optional.of(GFileInfo))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFileInfo> createFolder(String folderName) {
        return createFolder(folderName, null);
    }

    /**
     * Create a folder with given name under My Drive directory if it is absent.
     *
     * @param folderName the name of the folder. This is not necessarily unique within a folder.
     *                   Note that for immutable items such as the top level folders of shared drives,
     *                   My Drive root folder, and Application Data folder the name is constant.
     * @return created folder info, or <code>null</code> if folder with such <code>folderName</code> is present.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public GFileInfo createFolderIfAbsent(String folderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        return folder.orElseGet(() -> createFolder(folderName).orElse(null));
    }

    /**
     * Create a folder with given name under specified folder.
     * <p></p>
     * In order to create folder under My Drive root directory use <code>this.createFolderIfAbsent(folderName)</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to create folder under Shared Drives.
     *
     * @param folderName the name of the folder. This is not necessarily unique within a folder.
     *                   Note that for immutable items such as the top level folders of shared drives,
     *                   My Drive root folder, and Application Data folder the name is constant.
     * @param parentId   the folder's ID of parent.
     * @return wrapper <code>Optional.of(GFileInfo)</code> of created folder info.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFileInfo> createFolder(String folderName, GFileId parentId) {
        if (folderName != null) {
            initService();
            return this.service.createFile(folderName, GFileType.FOLDER, null, parentId);
        }
        return Optional.empty();
    }

    /**
     * Create a folder with given name under specified folder if it is absent.
     * <p></p>
     * In order to create folder under My Drive root directory use <code>this.createFolderIfAbsent(folderName)</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to create folder under Shared Drives.
     *
     * @param folderName the name of the folder. This is not necessarily unique within a folder.
     *                   Note that for immutable items such as the top level folders of shared drives,
     *                   My Drive root folder, and Application Data folder the name is constant.
     * @param parentId   the folder's ID of parent.
     * @return created folder info, or <code>null</code> if folder with such <code>folderName</code> is present.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public GFileInfo createFolderIfAbsent(String folderName, GFileId parentId) {
        Optional<GFileInfo> folder = getFolder(folderName);
        return folder.orElseGet(() -> createFolder(folderName, parentId).orElse(null));
    }

    /**
     * Change name of the folder to given one.
     * <p></p>
     * <code>supportsTeamDrives</code> must be <code>true</code> to rename folder under Shared Drive.
     * If original <code>folderName</code> is absent, than nothing changes.
     *
     * @param folderName    the name of the folder. This is not necessarily unique within a folder.
     *                      Note that for immutable items such as the top level folders of shared drives,
     *                      My Drive root folder, and Application Data folder the name is constant.
     * @param newFolderName the folder's name change to.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/reference/files/update">Files: update</a> for more details.
     */
    public void renameFolder(String folderName, String newFolderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(fileInfo -> this.service.renameFile(fileInfo, newFolderName));
    }

    /**
     * Change name of the folder to given one.
     * <p></p>
     * <code>supportsTeamDrives</code> must be <code>true</code> to rename folder under Shared Drive.
     *
     * @param folder        the folder's info.
     * @param newFolderName the folder's name change to.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/reference/files/update">Files: update</a> for more details.
     */
    public void renameFolder(GFileInfo folder, String newFolderName) {
        renameFile(folder, newFolderName);
    }

    /**
     * Change name of the file to given one.
     * <p></p>
     * <code>supportsTeamDrives</code> must be <code>true</code> to rename file under Shared Drive.
     *
     * @param file        the file's info.
     * @param newFileName the file's name change to.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/reference/files/update">Files: update</a> for more details.
     */
    public void renameFile(GFileInfo file, String newFileName) {
        initService();
        this.service.renameFile(file, newFileName);
    }

    /**
     * Updates a file's content.
     * <p></p>
     * <code>supportsTeamDrives</code> must be <code>true</code> if file is located under Shared Drives.
     *
     * @param file object <code>GFileInfo</code> of file with specified file id.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void updateFile(GFileInfo file) {
        initService();
        this.service.updateFile(file);
    }

    /**
     * Move folder by given name  to a target folder.
     * <p></p>
     * In order to move file to My Drive root directory set <code>targetFolderName</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to move folder under Shared Drives.
     *
     * @param folderName       the folder's name of source.
     * @param targetFolderName the folder's name of target.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void moveFolder(String folderName, String targetFolderName) {
        Optional<GFileInfo> targetFolder = getFolder(targetFolderName);
        targetFolder.ifPresent(targetFolderInfo -> moveFolder(folderName, targetFolderInfo.getFileId()));
    }

    /**
     * Move folder by given name  to a target folder.
     * <p></p>
     * In order to move file to My Drive root directory set <code>targetFolderId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to move folder under Shared Drives.
     *
     * @param folderName     the folder's name of source.
     * @param targetFolderId the folder's ID of target.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void moveFolder(String folderName, GFileId targetFolderId) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(folderInfo -> this.service.moveFile(folderInfo, targetFolderId));
    }

    /**
     * Move file to a target folder.
     * <p></p>
     * In order to move file to My Drive root directory set <code>targetFolderId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to move file under Shared Drives.
     *
     * @param file           object <code>GFileInfo</code> of file with specified file id.
     * @param targetFolderId object <code>GFileId</code> of target folder, or null for none.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void moveFile(GFileInfo file, GFileId targetFolderId) {
        initService();
        this.service.moveFile(file, targetFolderId);
    }

    /**
     * Permanently deletes a file owned by the user without moving it to the trash.
     * <p></p>
     * If the file belongs to a Team Drive the user must be an organizer on the parent and <code>supportsTeamDrives</code> must be <code>true</code>.
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param fileId GFileId object of the file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void deleteFile(GFileId fileId) {
        initService();
        this.service.deleteFile(fileId);
    }

    /**
     * Permanently deletes a file owned by the user without moving it to the trash.
     * <p></p>
     * If the file belongs to a Team Drive the user must be an organizer on the parent and <code>supportsTeamDrives</code> must be <code>true</code>.
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param file the file's info object.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void deleteFile(GFileInfo file) {
        initService();
        this.service.deleteFile(file.getFileId());
    }

    /**
     * Permanently deletes a folder and all descendants owned by the user are also deleted without moving it to the trash.
     * <p></p>
     * If the file belongs to a Team Drive the user must be an organizer on the parent and <code>supportsTeamDrives</code> must be <code>true</code>.
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param folderId the folder's ID object.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void deleteFolder(GFileId folderId) {
        initService();
        this.service.deleteFile(folderId);
    }

    /**
     * Permanently deletes a folder and all descendants owned by the user are also deleted without moving it to the trash.
     * <p></p>
     * If the file belongs to a Team Drive the user must be an organizer on the parent and <code>supportsTeamDrives</code> must be <code>true</code>.
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param folderName the folder's name.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void deleteFolder(String folderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(f -> this.service.deleteFile(f.getFileId()));
    }

    /**
     * @return @see {@link Drive} service for advanced usage.
     */
    public Drive getDrive() {
        initService();
        return service.getDrive();
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found or {@link RPAServicesAccessor} is not defined.
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
