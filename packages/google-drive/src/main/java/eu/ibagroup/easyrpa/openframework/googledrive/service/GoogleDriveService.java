package eu.ibagroup.easyrpa.openframework.googledrive.service;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import eu.ibagroup.easyrpa.openframework.googleauth.GoogleAuthorizationService;
import eu.ibagroup.easyrpa.openframework.googledrive.exceptions.GoogleDriveException;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFile;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GoogleDriveService {

    private static final String DEFAULT_GOOGLE_FILE_FIELDS = "id, name, mimeType, description, size, parents, permissions";

    private Drive service;

    private boolean supportsTeamDrives = true;

    private String fileFields = DEFAULT_GOOGLE_FILE_FIELDS;

    public GoogleDriveService(GoogleAuthorizationService authorizationService) {
        this.service = new Drive.Builder(
                authorizationService.getHttpTransport(),
                authorizationService.getJsonFactory(),
                authorizationService.getCredentials()
        ).build();
    }

    public void setSupportsTeamDrives(boolean supportsTeamDrives) {
        this.supportsTeamDrives = supportsTeamDrives;
    }

    public void setExtraFileFields(List<String> extraFileFields) {
        if (extraFileFields.size() > 0) {
            this.fileFields = String.join(",",
                    DEFAULT_GOOGLE_FILE_FIELDS,
                    String.join(",", extraFileFields));
        } else {
            fileFields = DEFAULT_GOOGLE_FILE_FIELDS;
        }
    }

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

            System.out.println(files.size());

        } catch (IOException e) {
            throw new GoogleDriveException(String.format("Getting the list of files using query '%s' has failed.", query), e);
        }

        return files;
    }

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

    public void renameFile(GFileInfo fileInfo, String newFileName) {
        try {
            service.files().update(fileInfo.getId(), new File().setName(newFileName))
                    .setSupportsTeamDrives(supportsTeamDrives).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Renaming of file '%s' to '%s' has failed.",
                    fileInfo.getName(), newFileName), e);
        }
    }

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

    public void deleteFile(GFileId fileId) {
        try {
            service.files().delete(fileId.getId()).setSupportsTeamDrives(supportsTeamDrives).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Deleting of file with ID '%s' has failed.", fileId.getId()), e);
        }
    }

    public Drive getDrive() {
        return service;
    }
}
