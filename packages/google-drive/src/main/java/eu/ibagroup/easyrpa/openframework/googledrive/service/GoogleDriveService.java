package eu.ibagroup.easyrpa.openframework.googledrive.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.openframework.googledrive.exceptions.GoogleDriveException;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFile;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileType;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class GoogleDriveService {

    private static final String DEFAULT_GOOGLE_FILE_FIELDS = "id, name, mimeType, description, size, parents, permissions";

//    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private Drive service;

    private String fileFields = DEFAULT_GOOGLE_FILE_FIELDS;

    public GoogleDriveService(String secret) {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            this.service = new Drive.Builder(httpTransport, jsonFactory,
                    getCredentials(httpTransport, jsonFactory, secret)).build();
        } catch (IOException | GeneralSecurityException e) {
            throw new GoogleDriveException("Google Drive service initialization has failed.", e);
        }
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

    public List<GFileInfo> listFiles(String parentFolderId, GFileType fileType) {
        try {
            StringBuilder qParam = new StringBuilder();
            if (parentFolderId != null) {
                qParam.append(String.format("'%s' in parents and ", parentFolderId));
            }
            if (fileType == GFileType.FILE) {
                qParam.append(String.format("mimeType != '%s' ", GFileType.FOLDER.toString()));
            } else if (fileType != null) {
                qParam.append(String.format("mimeType = '%s' ", fileType.toString()));
            }
            return service.files().list()
                    .setQ(qParam.toString())
                    .setFields(String.format("files(%s)", fileFields))
                    .execute()
                    .getFiles().stream().filter(Objects::nonNull).map(GFileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new GoogleDriveException(String.format("Getting the list of %s%s has failed.",
                    fileType == GFileType.FOLDER ? "sub-folders" : "files",
                    parentFolderId != null ? String.format(" in '%s' folder", parentFolderId) : ""), e);
        }
    }

    public Optional<GFileInfo> getFileInfo(GFileId fileId) {
        if (fileId == null) {
            return Optional.empty();
        }
        try {
            File result = service.files().get(fileId.getId())
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
                return Optional.of(new GFileInfo(service.files().create(file, content).execute()));
            }
            return Optional.of(new GFileInfo(service.files().create(file).execute()));
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Creating of file '%s' has failed.", fileName), e);
        }
    }

    public void renameFile(GFileInfo fileInfo, String newFileName) {
        try {
            service.files().update(fileInfo.getId(), new File().setName(newFileName)).execute();
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
            service.files().update(file.getId(), file.getGoogleFile(), content);
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Updating of file '%s' has failed.", file.getName()), e);
        }
    }

    public void deleteFile(GFileId fileId) {
        try {
            service.files().delete(fileId.getId()).execute();
        } catch (Exception e) {
            throw new GoogleDriveException(String.format("Deleting of file with ID '%s' has failed.", fileId.getId()), e);
        }
    }

    private Credential getCredentials(NetHttpTransport httpTransport, JsonFactory jsonFactory, String secret) throws IOException {

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new StringReader(secret));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(httpTransport, jsonFactory, clientSecrets, Collections.singletonList(DriveScopes.DRIVE))
                .setDataStoreFactory(new MemoryDataStoreFactory())
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
