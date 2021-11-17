package files_manipulations.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFile;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Upload File")
public class UploadFile extends ApTask {

    private static final String fileName = "/myFile.txt";

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Creation file instance of '{}'", fileName);
        java.io.File file = new java.io.File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            //do nothing
        }

        log.info("Uploading file to google drive");
        Optional<GoogleFile> googleFile = drive.createFile(file);

        if (googleFile.isPresent()) {
            log.info("File successfully uploaded");
        } else {
            log.info("There were some error during uploading file");
        }
    }
}