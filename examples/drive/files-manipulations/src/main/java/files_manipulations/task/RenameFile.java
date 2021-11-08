package files_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApTaskEntry(
        name = "Rename File"
)
public class RenameFile extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(RenameFile.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String fileName = "myFile";

    public RenameFile() {
    }

    public void execute() {

        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Getting file '{}' from google drive", fileName);
        Optional<File> file = instance.getFileByName(fileName);

        if (file.isPresent()) {
            boolean result = instance.renameFile(file.get(), "RenamedFile");
            if (result) {
                log.info("File successfully renamed");
            } else{
                log.info("Error during renaming file");
            }
        } else {
            log.info("There were some error during getting file");
        }
    }
}