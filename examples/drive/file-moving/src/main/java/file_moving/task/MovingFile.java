package file_moving.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApTaskEntry(
        name = "moving file from one directory to another"
)
public class MovingFile extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(MovingFile.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private final String fileName = "test";

    private final String newFolderName = "newDirectory";

    public MovingFile() {
    }

    public void execute() {

        log.info("Getting list of files from google drive, creds from '{}'", this.filePath);

        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Creating File");
        Optional<File> file = instance.createFile(fileName, FileType.DOCUMENT);

        Optional<File> directoryFile = instance.getFileByName(newFolderName);

        if(file.isPresent() && directoryFile.isPresent()) {
            log.info("file created in root directory ");

            instance.moveFileToAnotherFolder(file.get(),directoryFile.get());

            log.info("now file in directory '{}'", newFolderName);
        }else{
            log.info("file wasn't created or directory not found");
        }
    }
}