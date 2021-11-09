package eu.ibagroup.easyrpa.openframework.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.googledrive.exceptions.GoogleDriveInstanceCreationException;
import eu.ibagroup.easyrpa.openframework.googledrive.exceptions.HttpTransportCreationException;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoogleDrive {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private NetHttpTransport HTTP_TRANSPORT;

    private String credString;

    private List<String> scopes;

    private Drive service;

    @Inject
    public GoogleDrive(RPAServicesAccessor rpaServices) {
        this.credString = rpaServices.getSecret("google.credentials", String.class);
        connect();
    }

    public GoogleDrive() {
    }

    public List<String> getScopes() {
        return scopes == null ? SCOPES : scopes;
    }

    public GoogleDrive setScopes(List<String> scopes) {
        service = null;
        this.scopes = scopes;
        return this;
    }

    public GoogleDrive setCredentials(String credentials) {
        service = null;
        this.credString = credentials;
        return this;
    }

    private void setHttpTransport() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException e) {
            throw new HttpTransportCreationException("HttpTransport initialization failed");
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        List<String> scopeList = scopes == null ? SCOPES : scopes;

        InputStream in = new ByteArrayInputStream(credString.getBytes());
        if (in == null) {
            throw new FileNotFoundException("Credentials not found: ");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopeList)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private void connect() {
        if (service == null) {
            setHttpTransport();
            try {
                this.service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).build();
            } catch (IOException e) {
                throw new GoogleDriveInstanceCreationException("creation failed");
            }
        }
    }

    public List<File> getFiles() {
        connect();
        try {
            return service.files().list().execute()
                    .getFiles();
        } catch (IOException e) {
            return null;
        }
    }

    public Optional<File> getFileByName(String fileName) {
        connect();
        try {
            return service.files().list().execute().getFiles()
                    .stream()
                    .filter(file -> fileName.equalsIgnoreCase(file.getName()))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<File> getFileById(String fileId) {
        connect();
        try {
            return Optional.of(service.files().get(fileId).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<File> getFullFileInfoById(String fileId) {
        connect();
        try {
            return Optional.of(service.files().get(fileId)
                    .setFields("id, name, mimeType, description, size, parents, permissions")
                    .execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<File> createFile(java.io.File file, FileType type) {
        connect();
        File fileMetadata = fileCreation(file.getName(), type);
        FileContent fileContent = new FileContent(null, file);
        try {
            return Optional.of(service.files().create(fileMetadata, fileContent).execute());
        } catch (IOException e) {
            return Optional.empty();
        }

    }

    public boolean createFile(File file) {
        connect();
        try {
            service.files().create(file).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<File> createFile(String filename, FileType type) {
        connect();
        File file = fileCreation(filename, type);
        try {
            return Optional.of(service.files().create(file).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<File> createFile(String filename, FileType type, String folderId) {
        connect();
        File file = fileCreation(filename, type);
        file.setParents(Collections.singletonList(folderId));
        try {
            return Optional.of(service.files().create(file).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean deleteFile(String fileId) {
        connect();
        try {
            service.files().delete(fileId).execute();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    public boolean renameFile(String fileId, String newFileName) {
        connect();
        Optional<File> file = getFileById(fileId);
        return file.filter(value -> renameFile(value, newFileName)).isPresent();
    }

    public boolean renameFile(File file, String newFileName) {
        connect();
        file.setName(newFileName);
        return updateFileMetadata(file);
    }

    public boolean updateFileMetadata(File file) {
        try {
            File newFile = createMetadataCopyFile(file);

            service.files().update(file.getId(), newFile).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateFileContent(java.io.File file) throws FileNotFoundException {
        connect();
        Optional<File> fileMetadata = getFileByName(file.getName());
        if (!fileMetadata.isPresent()) {
            throw new FileNotFoundException("file with this name not found on drive");
        }
        File newMetadataFile = createMetadataCopyFile(fileMetadata.get());
        FileContent content = new FileContent(null, file);
        try {
            service.files().update(fileMetadata.get().getId(), newMetadataFile, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updateFileContent(String filepath) throws FileNotFoundException {
        java.io.File file = new java.io.File(filepath);
        if(!file.exists()){
            throw new FileNotFoundException("file with this name not found in file explorer");
        }
        return updateFileContent(file);
    }

    public void moveFileToAnotherFolder(File file, File folder) {
        moveFileToAnotherFolder(file.getId(), folder.getId());
    }

    public void moveFileToAnotherFolder(String fileId, String folderId) {
        connect();
        try {
            File file = service.files().get(fileId)
                    .setFields("parents")
                    .execute();
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }
            service.files().update(fileId, null)
                    .setAddParents(folderId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();
        } catch (IOException e) {
            //do nothing
        }
    }

    private File fileCreation(String filename, FileType type) {
        File file = new File();
        file.setName(filename);
        file.setMimeType(FileType.toString(type));
        return file;
    }

    public OutputStream downloadFile(String fileId) {
        connect();
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
            return outputStream;
        } catch (IOException e) {
            return null;
        }
    }

    public OutputStream downloadFile(String fileId, String mimeType) {
        connect();
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().export(fileId, mimeType)
                    .executeMediaAndDownloadTo(outputStream);
            return outputStream;
        } catch (IOException e) {
            return null;
        }
    }

    private File createMetadataCopyFile(File file) {
        File newFile = new File();
        newFile.setName(file.getName());
        newFile.setMimeType(file.getMimeType());
        newFile.setParents(file.getParents());
        newFile.setDescription(file.getDescription());
        return newFile;
    }
}
