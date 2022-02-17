package eu.ibagroup.easyrpa.openframework.google.drive.service;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import eu.ibagroup.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFile;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GoogleDriveService {

    /**
     * Default list of fields to extract for Google Service,
     *
     * @see <a href="https://developers.google.com/drive/api/v3/reference/files">API reference files</a>
     */
    private static final String DEFAULT_GOOGLE_FILE_FIELDS = "id, name, mimeType, description, size, parents, permissions";

    /**
     * Google Drive service.
     */
    private Drive service;

    private boolean supportsTeamDrives = true;

    private String fileFields = DEFAULT_GOOGLE_FILE_FIELDS;

    public GoogleDriveService(Drive service) {
        this.service = service;
    }

    /**
     * Whether both My Drive and shared drive items should be included in results.
     *
     * @param supportsTeamDrives true to include shared drive in results, otherwise false
     */
    public void setSupportsTeamDrives(boolean supportsTeamDrives) {
        this.supportsTeamDrives = supportsTeamDrives;
    }

    /**
     * Add extra fields for Google Service to include in results,
     *
     * @param extraFileFields list of fields.
     * @see <a href="https://developers.google.com/drive/api/v3/reference/files">API reference files</a>
     */
    public void setExtraFileFields(List<String> extraFileFields) {
        if (extraFileFields.size() > 0) {
            this.fileFields = String.join(",",
                    DEFAULT_GOOGLE_FILE_FIELDS,
                    String.join(",", extraFileFields));
        } else {
            fileFields = DEFAULT_GOOGLE_FILE_FIELDS;
        }
    }

    /**
     * Send request to get list of files by given query.
     * <p></p>
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
     * @return list of GFileInfo @see {@link GFileInfo} objects matching query condition
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public List<GFileInfo> listFiles(String query, Integer pageSize) {
        List<GFileInfo> files = new ArrayList<>();
        try {
            String pageToken = null;
            do {
                FileList result = service.files().list()
                        .setIncludeTeamDriveItems(supportsTeamDrives)
                        .setSupportsTeamDrives(supportsTeamDrives)
                        .setPageSize(pageSize != null ? pageSize : 100)
                        .setQ(query)
                        .setPageToken(pageToken)
                        .setFields(String.format("nextPageToken, files(%s)", fileFields))
                        .execute();
                files.addAll(result.getFiles().stream().filter(Objects::nonNull).map(GFileInfo::new).collect(Collectors.toList()));
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            throw new GoogleDriveException(String.format("Getting the list of files using query '%s' has failed.", query), e);
        }

        return files;
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
        if (fileId == null) {
            return Optional.empty();
        }
        try {
            File result = service.files().get(fileId.getId())
                    .setSupportsTeamDrives(supportsTeamDrives)
                    .setFields(fileFields)
                    .execute();
            return result != null ? Optional.of(new GFileInfo(result)) : Optional.empty();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Getting of file info or folder with id '%s' has failed.", fileId.getId()), e);
        }
    }

    /**
     * Send request to get file info @see {@link GFileInfo} of given file name.
     * <p></p>
     * If <code>name</code> is not specified or file not, then returns <code>Optional.empty()</code>.
     * Otherwise returns {@code Optional.of(GFileInfo)} if file is found.
     *
     * @param name string file name to get info.
     * @return wrapper <code>Optional.of(GFileInfo))</code> for found file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFileInfo> getFileInfo(String name) {
        if (name == null) {
            return Optional.empty();
        }
        try {
            return service.files().list()
                    .setIncludeTeamDriveItems(supportsTeamDrives)
                    .setSupportsTeamDrives(supportsTeamDrives)
                    .setQ(String.format("name = '%s'", name))
                    .setFields(String.format("files(%s)", fileFields))
                    .execute()
                    .getFiles().stream().filter(Objects::nonNull).map(GFileInfo::new).findFirst();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Getting of file info or folder with name '%s' has failed.", name), e);
        }
    }

    /**
     * Send request to get file @see {@link GFile} of given file info {@link GFileInfo}.
     * <p></p>
     * <b>NOTICE:</b> Please note that the exported content is limited to 10MB.
     * If <code>fileInfo</code> is not specified or file not found, then returns <code>Optional.empty()</code>.
     *
     * @param fileInfo object <code>GFileInfo</code> to get file.
     * @return wrapper <code>Optional.of(GFile))</code> for found file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public Optional<GFile> getFile(GFileInfo fileInfo) {
        if (fileInfo == null) {
            return Optional.empty();
        }
        try {
            InputStream is = service.files().export(fileInfo.getId(),
                    fileInfo.getFileType().getContentType()).executeMediaAsInputStream();

            return Optional.of(new GFile(fileInfo, is));
        } catch (GoogleDriveException e) {
            throw e;
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Getting of file with id '%s' has failed.", fileInfo.getId()), e);
        }
    }

    /**
     * Create a new file.
     * <p></p>
     * In order to create file under My Drive root directory set <code>parentId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to create file under Shared Drives.
     * <p></p>
     * <b>Usage Examples:</b>
     * Create file with name README.txt under some folder "folderId":
     * <pre>
     * FileContent fileContent = new FileContent(null, file);
     * this.createFile("README.txt" GFileType.FILE, fileContent,  folderId);
     * </pre>
     * Create folder with name "Documents" in root location of My Drive:
     * <pre>
     * this.createFile(Documents", GFileType.FOLDER, null, null);
     * </pre>
     *
     * @param fileName The name of the file. This is not necessarily unique within a folder.
     *                 Note that for immutable items such as the top level folders of shared drives,
     *                 My Drive root folder, and Application Data folder the name is constant.
     * @param type     The MIME type of the file.
     *                 Google Drive will attempt to automatically detect an appropriate value from uploaded content if no value is provided.
     * @param content  the media HTTP content or null if none.
     * @param parentId the id of parent folder, or null then the file will be placed directly in the user's My Drive folder
     * @return wrapper <code>Optional.of(GFileInfo))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFileInfo> createFile(String fileName, GFileType type, AbstractInputStreamContent content, GFileId parentId) {
        try {
            File file = new File();
            file.setName(fileName);
            file.setMimeType(type.getMimeType());
            if (parentId != null) {
                file.setParents(Collections.singletonList(parentId.getId()));
            }
            if (content != null) {
                return Optional.of(new GFileInfo(service.files().create(file, content)
                        .setSupportsTeamDrives(supportsTeamDrives).execute()));
            }
            return Optional.of(new GFileInfo(service.files().create(file)
                    .setSupportsTeamDrives(supportsTeamDrives).execute()));
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Creating of file '%s' has failed.", fileName), e);
        }
    }

    /**
     * Creates a copy of a file.
     * <p></p>
     * <code>supportsTeamDrives</code> must be <code>true</code> to create file under Shared Drives.
     *
     * @param source     the source file info.
     * @param nameOfCopy the name of copied file.
     * @return wrapper <code>Optional.of(GFileInfo))</code> for created file.
     * @throws GoogleDriveException in case of any error occurs in request, see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     * @see Drive API reference <a href="https://developers.google.com/drive/api/v3/create-file">Create file</a> for more details.
     */
    public Optional<GFileInfo> copyFile(GFileInfo source, String nameOfCopy) {
        try {
            return Optional.of(new GFileInfo(service.files().copy(source.getId(), new File().setName(nameOfCopy))
                    .setSupportsTeamDrives(supportsTeamDrives).execute()));
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Copying of file '%s' as '%s' has failed.",
                    source.getName(), nameOfCopy), e);
        }
    }

    /**
     * Rename a file with given name.
     *
     * @param fileInfo    object <code>GFileInfo</code> with file id.
     * @param newFileName The name of the file. This is not necessarily unique within a folder.
     *                    Note that for immutable items such as the top level folders of shared drives,
     *                    My Drive root folder, and Application Data folder the name is constant.
     *                    The MIME type of the file.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void renameFile(GFileInfo fileInfo, String newFileName) {
        try {
            service.files().update(fileInfo.getId(), new File().setName(newFileName))
                    .setSupportsTeamDrives(supportsTeamDrives).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Renaming of file '%s' to '%s' has failed.",
                    fileInfo.getName(), newFileName), e);
        }
    }

    /**
     * Move file to a target folder.
     * <p></p>
     * In order to move file to My Drive root directory set <code>targetFolderId</code> to <code>null</code>.
     * <code>supportsTeamDrives</code> must be <code>true</code> to move file under Shared Drives.
     *
     * @param fileInfo       object <code>GFileInfo</code> of file with specified file id.
     * @param targetFolderId object <code>GFileId</code> of target folder, or null for none.
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void moveFile(GFileInfo fileInfo, GFileId targetFolderId) {
        try {
            service.files().update(fileInfo.getId(), null)
                    .setAddParents(targetFolderId.getId())
                    .setRemoveParents(String.join(",", fileInfo.getParents()))
                    .setSupportsTeamDrives(supportsTeamDrives)
                    .setFields("id, parents")
                    .execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Moving of file '%s' to '%s' has failed.",
                    fileInfo.getName(), targetFolderId.getId()), e);
        }
    }

    /**
     * Updates a file's content.
     * <p></p>
     * <code>supportsTeamDrives</code> must be <code>true</code> if file is located under Shared Drives.
     *
     * @param file object <code>GFileInfo</code> of file with specified file id
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void updateFile(GFileInfo file) {
        InputStreamContent content = null;
        try {
            if (file instanceof GFile) {
                content = new InputStreamContent(file.getFileType().getContentType(), ((GFile) file).getContent());
            }
            service.files().update(file.getId(), file.getGoogleFile(), content)
                    .setSupportsTeamDrives(supportsTeamDrives).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Updating of file '%s' has failed.", file.getName()), e);
        }
    }

    /**
     * Permanently deletes a file owned by the user without moving it to the trash.
     * <p></p>
     * If the file belongs to a Team Drive the user must be an organizer on the parent and <code>supportsTeamDrives</code> must be <code>true</code>.
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param fileId GFileId object of the file
     * @throws GoogleDriveException in case of any error occurs in request, @see Drive API reference <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     */
    public void deleteFile(GFileId fileId) {
        try {
            service.files().delete(fileId.getId()).setSupportsTeamDrives(supportsTeamDrives).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Deleting of file with ID '%s' has failed.", fileId.getId()), e);
        }
    }

    /**
     * @return @see {@link Drive} service for advanced usage.
     */
    public Drive getDrive() {
        return service;
    }
}
