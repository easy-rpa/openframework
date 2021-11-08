package folders_manipulations.task;

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
        name = "Rename Folder"
)
public class RenameFolder extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(RenameFolder.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String folderName = "creationTestFolder";

    public RenameFolder() {
    }

    public void execute() {

        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Getting folder '{}' from google drive", folderName);
        Optional<File> file = instance.getFileByName(folderName);

        if (file.isPresent()) {
            boolean result = instance.renameFile(file.get(), "RenamedFolder");
            if (result) {
                log.info("Folder successfully renamed");
            } else{
                log.info("Error during renaming folder");
            }
        } else {
            log.info("There were some error during getting folder");
        }
    }
}