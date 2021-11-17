package eu.ibagroup.easyrpa.openframework.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
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
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFile;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFileInfo;
import eu.ibagroup.easyrpa.openframework.googledrive.file.Id;
import eu.ibagroup.easyrpa.openframework.googledrive.folder.GoogleFolderInfo;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleUtils;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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

    public GoogleDrive setSecret(String secret) {
        service = null;
        this.credString = secret;
        return this;
    }

    public List<GoogleFolderInfo> listFolders(Id folderId) {
        connect();
        try {
            List<GoogleFolderInfo> list = new ArrayList<>();
            getFilesList(FileType.FOLDER, folderId)
                    .forEach(folder -> list.add(new GoogleFolderInfo(folder)));
            return list;
        } catch (IOException e) {
            return null;
        }
    }

    public List<GoogleFileInfo> listFiles(Id folderId) {
        connect();
        try {
            List<GoogleFileInfo> list = new ArrayList<>();
            getFilesList(FileType.FILE, folderId)
                    .forEach(file -> list.add(new GoogleFileInfo(file)));
            return list;
        } catch (IOException e) {
            return null;
        }
    }

    public List<GoogleFileInfo> listFiles() {
        connect();
        try {
            List<GoogleFileInfo> list = new ArrayList<>();
            getFilesList(FileType.FILE, null)
                    .forEach(file -> list.add(new GoogleFileInfo(file)));
            return list;
        } catch (IOException e) {
            return null;
        }
    }

    public List<GoogleFolderInfo> listFolders() {
        connect();
        try {
            List<GoogleFolderInfo> list = new ArrayList<>();
            getFilesList(FileType.FOLDER, null)
                    .forEach(folder -> list.add(new GoogleFolderInfo(folder)));
            return list;
        } catch (IOException e) {
            return null;
        }
    }

    public Optional<GoogleFile> getFile(String fileName) {
        connect();
        Optional<GoogleFileInfo> fileInfo = getFileInfo(fileName);

        if (fileInfo.isPresent()) {
            File metaData = createMetadataCopyFile(fileInfo.get());
            return Optional.of(new GoogleFile(metaData, downloadFile(metaData)));
        }
        return Optional.empty();
    }

    public Optional<GoogleFolderInfo> getFolder(String folderName) {
        connect();
        try {
            Optional<File> folder = getFilesList(FileType.FOLDER, null)
                    .stream()
                    .filter(file -> folderName.equalsIgnoreCase(file.getName()))
                    .findFirst();
            if (folder.isPresent()) {
                return Optional.of(new GoogleFolderInfo(folder.get()));
            }
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    public Optional<GoogleFolderInfo> getFolder(Id folderId) {
        connect();
        try {
            File file = service.files().get(folderId.toString())
                    .setFields("id, name, mimeType, description, size, parents, permissions")
                    .execute();
            if (file != null && file.getMimeType().equalsIgnoreCase(FileType.toString(FileType.FOLDER))) {
                return Optional.of(new GoogleFolderInfo(file));
            }
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    public Optional<GoogleFile> getFile(Id fileId) {
        connect();
        Optional<GoogleFileInfo> fileInfo = getFileInfo(fileId);

        if (fileInfo.isPresent()) {
            File metaData = createMetadataCopyFile(fileInfo.get());
            return Optional.of(new GoogleFile(metaData, downloadFile(metaData)));
        }
        return Optional.empty();
    }

    public Optional<GoogleFileInfo> getFileInfo(Id fileId) {
        connect();
        try {
            Optional<File> fileMetadata = Optional.of(service.files().get(fileId.toString())
                    .setFields("id, name, mimeType, description, size, parents, permissions")
                    .execute());

            return Optional.of(new GoogleFileInfo(fileMetadata.get()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<GoogleFileInfo> getFileInfo(String fileName) {
        connect();
        try {
            Optional<File> fileMetadata = getFilesList(FileType.FILE, null)
                    .stream()
                    .filter(file -> fileName.equalsIgnoreCase(file.getName()))
                    .findFirst();

            if (fileMetadata.isPresent()) {
                return Optional.of(new GoogleFileInfo(fileMetadata.get()));
            }
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    public Optional<GoogleFile> createFile(java.io.File file) {
        return create(file);
    }

    public Optional<GoogleFile> createFile(InputStream stream, String fileName) {
        InputStreamContent content = new InputStreamContent(null, stream);
        return create(content, fileCreation(fileName, FileType.FILE));
    }

    public Optional<GoogleFile> createFile(String filename) {
        Optional<File> file = create(filename, FileType.FILE);
        return file.map(value -> new GoogleFile(value, null));
    }

    public Optional<GoogleFolderInfo> createFolder(String folderName) {
        Optional<File> file = create(folderName, FileType.FOLDER);
        return file.map(GoogleFolderInfo::new);
    }

    public Optional<GoogleFile> createFile(String filename, Id folderId) {
        Optional<File> file = create(filename, FileType.FILE, folderId);
        return file.map(value -> new GoogleFile(value, null));
    }

    public Optional<GoogleFolderInfo> createFolder(String foldername, Id folderId) {
        Optional<File> file = create(foldername, FileType.FOLDER, folderId);
        return file.map(GoogleFolderInfo::new);
    }

    public Optional<GoogleFile> createGoogleSheet(String sheetName) {
        Optional<File> file = create(sheetName, FileType.SPREADSHEET);
        return file.map(value -> new GoogleFile(value, null));
    }

    public Optional<GoogleFile> createGoogleSheet(String sheetName, Id folderId) {
        Optional<File> file = create(sheetName, FileType.FILE, folderId);
        return file.map(value -> new GoogleFile(value, null));
    }

    public boolean deleteFolder(Id folderId) {
        return deleteFile(folderId);
    }

    public boolean deleteFile(Id fileId) {
        connect();
        try {
            service.files().delete(fileId.toString()).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean renameFolder(GoogleFolderInfo folder, String newFolderName) {
        return renameFile(createMetadataCopyFile(folder), newFolderName);
    }


    public boolean renameFile(GoogleFile file, String newFileName) {
        return renameFile(createMetadataCopyFile(file), newFileName);
    }

    public boolean updateFolderInfo(GoogleFolderInfo file) {
        try {
            File newFile = createMetadataCopyFile(file);

            service.files().update(file.getId().toString(), newFile).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateFileInfo(GoogleFileInfo file) {
        return updateFolderInfo(file);
    }

    public boolean updateFile(GoogleFile file) {
        connect();
        File metadataFile = createMetadataCopyFile(file);

        InputStreamContent content = new InputStreamContent(null, file.getContent());
        try {
            service.files().update(file.getId().toString(), metadataFile, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void moveFolder(GoogleFolderInfo folderFrom, GoogleFolderInfo folderTo) {
        moveFile(folderFrom.getId().toString(), folderFrom.getId().toString());
    }

    public void moveFile(GoogleFileInfo file, GoogleFolderInfo folder) {
        moveFile(file.getId().toString(), folder.getId().toString());
    }

    public void moveFile(String fileId, String folderId) {
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

    private OutputStream downloadFile(Id fileId) {
        connect();
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().get(fileId.toString())
                    .executeMediaAndDownloadTo(outputStream);
            return outputStream;
        } catch (IOException e) {
            return null;
        }
    }

    private File createMetadataCopyFile(GoogleFile file) {
        File newFile = new File();
        newFile.setId(file.getId().toString());
        newFile.setName(file.getName());
        newFile.setMimeType(FileType.toString(file.getFileType()));
        newFile.setParents(file.getParents());
        newFile.setDescription(file.getDescription());
        return newFile;
    }

    private File createMetadataCopyFile(File file) {
        File newFile = new File();
        newFile.setId(file.getId());
        newFile.setName(file.getName());
        newFile.setMimeType(file.getMimeType());
        newFile.setParents(file.getParents());
        newFile.setDescription(file.getDescription());
        return newFile;
    }

    private File createMetadataCopyFile(GoogleFolderInfo file) {
        File newFile = new File();
        newFile.setId(file.getId().toString());
        newFile.setName(file.getName());
        newFile.setMimeType(FileType.toString(FileType.FOLDER));
        newFile.setParents(file.getParents());
        newFile.setDescription(file.getDescription());
        return newFile;
    }

    private File createMetadataCopyFile(GoogleFileInfo file) {
        File newFile = new File();
        newFile.setId(file.getId().toString());
        newFile.setName(file.getName());
        newFile.setMimeType(FileType.toString(file.getFileType()));
        newFile.setParents(file.getParents());
        newFile.setDescription(file.getDescription());
        return newFile;
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

    private File fileCreation(String filename, FileType type) {
        File file = new File();
        file.setName(filename);
        file.setMimeType(FileType.toString(type));
        return file;
    }

    private Optional<GoogleFile> create(java.io.File file) {
        File fileMetadata = fileCreation(file.getName(), FileType.FILE);
        FileContent fileContent = new FileContent(null, file);
        return create(fileContent, fileMetadata);
    }

    private Optional<GoogleFile> create(InputStream stream, String fileName) {
        connect();
        File fileMetadata = fileCreation(fileName, FileType.FILE);
        InputStreamContent fileContent = new InputStreamContent(null, stream);
        return create(fileContent, fileMetadata);
    }

    private Optional<GoogleFile> create(AbstractInputStreamContent content, File metaData) {
        connect();
        try {
            File uploadedFile = service.files().create(metaData, content).execute();
            ByteArrayOutputStream os = GoogleUtils.isToOs(content.getInputStream());
            return Optional.of(new GoogleFile(uploadedFile, os));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<File> create(String filename, FileType type) {
        connect();
        File file = fileCreation(filename, type);
        try {
            return Optional.of(service.files().create(file).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<File> create(String filename, FileType type, Id folderId) {
        connect();
        File file = fileCreation(filename, type);
        file.setParents(Collections.singletonList(folderId.toString()));
        try {
            return Optional.of(service.files().create(file).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private boolean renameFile(File file, String newFileName) {
        connect();
        file.setName(newFileName);
        try {
            File newFile = fileCreation(file.getName(), FileType.getValue(file.getMimeType()));
            service.files().update(file.getId(), newFile).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ByteArrayOutputStream downloadFile(File file) {
        connect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            FileType fileType = FileType.getValue(file.getMimeType());
            service.files().export(file.getId(), GoogleUtils.getDownloadType(fileType))
                    .executeMediaAndDownloadTo(outputStream);
            return outputStream;
        } catch (IOException e) {
            return null;
        }
    }

    private List<File> getFilesList(FileType type, Id folderId) throws IOException {

        String qParam = "";
        String mimeType = " != ";
        if (folderId != null) {
            qParam = "'" + folderId.toString() + "' in parents and ";
        }
        if (type.compareTo(FileType.FOLDER) == 0) {
            mimeType = " = ";
        }
        return service.files().list()
                .setQ(qParam + "mimeType" + mimeType + "'" + FileType.toString(FileType.FOLDER) + "' ")
                .setFields("files(id, name, mimeType, description, size, parents, permissions)")
                .execute()
                .getFiles();
    }
}
