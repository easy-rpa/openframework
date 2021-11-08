package files_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

@ApTaskEntry(
        name = "Upload File"
)
public class UploadFile extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(UploadFile.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String fileName = "/myFile.txt";

    public UploadFile() {
    }

    public void execute() {
        log.info("Creation file instance of '{}'",fileName);
        java.io.File file = new java.io.File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            //do nothing
        }
        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Uploading file to google drive");
        Optional<File> fileMetadata = instance.createFile(file, FileType.FILE);

        if(fileMetadata.isPresent()){
            log.info("File successfully uploaded");
        }else{
            log.info("There were some error during uploading file");
        }
    }
}