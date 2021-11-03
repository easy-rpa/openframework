package eu.ibagroup.easyrpa.openframework.googledrive.utils;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoogleDriveService {

    private final Drive service;

    public GoogleDriveService(Drive service) {
        this.service = service;
    }

    public List<File> getFiles() {
        try {
            return service.files().list().execute()
                    .getFiles();
        } catch (IOException e) {
            return null;
        }
    }

    public Optional<File> getFileByName(String fileName) {
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
        try {
            return Optional.of(service.files().get(fileId).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<File> createFile(java.io.File file, FileType type) {
        File fileMetadata = fileCreation(file.getName(), type);
        FileContent fileContent = new FileContent(null, file);
        try {
            return Optional.of(service.files().create(fileMetadata, fileContent).execute());
        } catch (IOException e) {
            return Optional.empty();
        }

    }

    public boolean createFile(File file) {
        try {
            service.files().create(file).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<File> createFile(String filename, FileType type) {
        File file = fileCreation(filename, type);
        try {
            return Optional.of(service.files().create(file).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<File> createFile(String filename, FileType type, String folderId) {
        File file = fileCreation(filename, type);
        file.setParents(Collections.singletonList(folderId));
        try {
            return Optional.of(service.files().create(file).execute());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean deleteFile(String fileId) {
        try {
            service.files().delete(fileId).execute();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    public boolean renameFile(String fileId, String newFileName) {
        Optional<File> file = getFileById(fileId);
        return file.filter(value -> renameFile(value, newFileName)).isPresent();
    }

    public boolean renameFile(File file, String newFileName) {
        file.setName(newFileName);
        return updateFileMetadata(file);
    }

    public boolean updateFileMetadata(File file) {
        try {
            service.files().update(file.getId(), file).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateFileContent(java.io.File file) throws FileNotFoundException {
        Optional<File> fileMetadata = getFileByName(file.getName());
        if (!fileMetadata.isPresent()) {
            throw new FileNotFoundException("file with this name not found on drive");
        }
        FileContent content = new FileContent(null, file);
        try {
            service.files().update(fileMetadata.get().getId(), fileMetadata.get(), content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void moveFileToAnotherFolder(File file, File folder) {
        moveFileToAnotherFolder(file.getId(),folder.getId());
    }

    public void moveFileToAnotherFolder(String fileId, String folderId) {
        try {
            File file = service.files().get(fileId)
                    .setFields("parents")
                    .execute();
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }
            file = service.files().update(fileId, null)
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

    public OutputStream downloadFile(String fileId){
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
            return outputStream;
        } catch (IOException e) {
            return null;
        }
    }

    public OutputStream downloadFile(String fileId, String mimeType){
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            service.files().export(fileId, mimeType)
                    .executeMediaAndDownloadTo(outputStream);
            return outputStream;
        } catch (IOException e) {
            return null;
        }
    }
    //setq searching params https://developers.google.com/drive/api/v3/search-files

}
