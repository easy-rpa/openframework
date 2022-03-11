package eu.easyrpa.openframework.google.drive.service;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;
import eu.easyrpa.openframework.google.drive.model.GFile;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wraps Google {@link Drive} service and provides functionality to work with files in a bit more convenient
 * way for using it within RPA.
 */
public class GoogleDriveService {

    /**
     * Default list of fields to extract for Google Service.
     *
     * @see <a href="https://developers.google.com/drive/api/v3/reference/files">API reference files</a>
     */
    private static final String DEFAULT_GOOGLE_FILE_FIELDS = "id, name, mimeType, description, size, parents, permissions";

    /**
     * Google Drive service.
     */
    private Drive service;

    /**
     * Whether Google Drive API requests should also lookup files and folders in Shared Drives.
     */
    private boolean supportsTeamDrives = true;

    /**
     * List of fields that should be present in responses of Drive API requests.
     * <p>
     * <b>DEFAULT FIELDS:</b> {@code id, name, mimeType, description, size, parents, permissions}
     */
    private String fileFields = DEFAULT_GOOGLE_FILE_FIELDS;

    /**
     * Constructs this service based on Google {@link Drive} service.
     *
     * @param service instance of native Google Drive service that should be wrapped.
     */
    public GoogleDriveService(Drive service) {
        this.service = service;
    }

    /**
     * Whether both My Drive and shared drive items should be included in results.
     *
     * @param supportsTeamDrives {@code true} to include shared drive in results, otherwise {@code false}.
     */
    public void setSupportsTeamDrives(boolean supportsTeamDrives) {
        this.supportsTeamDrives = supportsTeamDrives;
    }

    /**
     * Adds extra fields to include in results.
     *
     * @param extraFileFields list of fields to include.
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
     * Gets list of files by given query.
     * <p>
     * <b>NOTICE:</b> {@code query} parameter should contain same fields that are mentioned in {@link #fileFields}
     * property. To add some extra fields into this property use the method {@link #setExtraFileFields(List)}.
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
     * @return list of {@link GFileInfo} objects matching query condition.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Gets info of Google Drive file with given file ID.
     *
     * @param fileId {@link GFileId} with file ID of necessary file.
     * @return {@link Optional} object with necessary file info or empty if such file is not found.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Gets info of Google Drive file with given file name.
     *
     * @param name the name of necessary file.
     * @return {@link Optional} object with necessary file info or empty if such file is not found.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Gets file content for given file.
     * <p>
     * <b>NOTICE:</b> The exported content is limited to 10MB.
     *
     * @param fileInfo {@link GFileInfo} representing file to get.
     * @return {@link GFile} object wrapped with {@link Optional} and containing the content of requested file.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Creates a new file on Google Drive.
     * <p>
     * In order to create file under My Drive root directory set {@code parentId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create file under Shared Drives.
     * <p>
     * See Google Drive <a href="https://developers.google.com/drive/api/v3/create-file">Create files</a> article
     * for more details.
     *
     * @param fileName the name of file to create. This is not necessarily unique within a folder.
     * @param type     {@link GFileType} value representing the type of Google Drive file to create.
     * @param content  the media HTTP content or {@code null} if none.
     * @param parentId the id of parent folder, or {@code null} then the file will be placed directly
     *                 in the user's My Drive folder
     * @return {@link Optional} object with created file info.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Creates a copy of Google Drive file.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to create file under Shared Drives.
     *
     * @param source     {@link GFileInfo} representing source file.
     * @param nameOfCopy the name of copied file.
     * @return {@link Optional} object with copied file info.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Renames Google Drive file with given name.
     *
     * @param fileInfo    {@link GFileInfo} representing the file to rename.
     * @param newFileName the new name of the file. This is not necessarily unique within a folder.
     *                    Note that for immutable items such as the top level folders of shared drives,
     *                    My Drive root folder, and Application Data folder the name is constant.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Moves Google Drive file to specified folder.
     * <p>
     * In order to move file to My Drive root directory set {@code targetFolderId} to {@code null}.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} to move file under Shared Drives.
     *
     * @param fileInfo       {@link GFileInfo} representing the file to move.
     * @param targetFolderId {@link GFileId} with file ID of target folder, or {@code null} for root.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Updates content of Google Drive file.
     * <p>
     * {@code supportsTeamDrives} must be {@code true} if file is located under Shared Drives.
     *
     * @param file {@link GFileInfo} representing the file to update.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
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
     * Permanently deletes a Google Drive file owned by the user without moving it to the trash.
     * <p>
     * If the file belongs to a Team Drive the user must be an organizer on the parent and
     * {@code supportsTeamDrives} must be {@code true}.
     * <p>
     * If the target is a folder, all descendants owned by the user are also deleted.
     *
     * @param fileId {@link GFileId} object with file ID of folder or file to delete.
     * @throws GoogleDriveException in case of any error occurs in request. See
     *                              <a href="https://developers.google.com/drive/api/v3/handle-errors">Resolve errors</a>
     *                              for more details.
     */
    public void deleteFile(GFileId fileId) {
        try {
            service.files().delete(fileId.getId()).setSupportsTeamDrives(supportsTeamDrives).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Deleting of file with ID '%s' has failed.", fileId.getId()), e);
        }
    }

    /**
     * Gets wrapped Google Drive service for advanced usage.
     *
     * @return Google Drive service for advanced usage.
     */
    public Drive getDrive() {
        return service;
    }
}
