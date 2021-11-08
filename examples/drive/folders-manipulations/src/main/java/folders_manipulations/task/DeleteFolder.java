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
        name = "Delete Folder"
)
public class DeleteFolder extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(DeleteFolder.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String folderName = "RenamedFolder";

    public DeleteFolder() {
    }

    public void execute() {
        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Getting Folder from Google Drive");
        Optional<File> file = instance.getFileByName(folderName);

        file.ifPresent(value -> {
            log.info("Trying to delete folder with the name: '{}' , id: '{}'", value.getName(), value.getId());
            if (instance.deleteFile(value.getId())) {
                log.info("folder was successfully deleted");
            } else {
                log.info("There wew some problems during deletion");
            }
        });
    }
}