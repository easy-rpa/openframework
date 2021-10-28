package eu.ibagroup.easyrpa.openframework.googledrive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GoogleDriveService {

    private final Drive service;

    public GoogleDriveService(Drive service) {
        this.service = service;
    }

    public List<File> getFiles() throws IOException {
        return service.files().list().execute()
                .getFiles();
    }

    public Optional<File> getFileByName(String fileName) throws IOException {
        return service.files().list().execute().getFiles()
                .stream()
                .filter(file -> fileName.equalsIgnoreCase(file.getName()))
                .findFirst();
    }

    public Optional<File> getFileById(String fileId) throws IOException {
        File file = service.files().get(fileId).execute();
            return file==null?  Optional.empty() : Optional.of(file);
    }

    public Drive getService(){
        return service;
    }

    //setq searching params https://developers.google.com/drive/api/v3/search-files

}
