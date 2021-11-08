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
        name = "Delete File"
)
public class DeleteFile extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(DeleteFile.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String fileName = "creationTest";

    public DeleteFile() {
    }

    public void execute() {
        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Getting File from Google Drive");
        Optional<File> file = instance.getFileByName(fileName);

        file.ifPresent(value -> {
            log.info("Trying to delete file with the name: '{}' , id: '{}'", value.getName(), value.getId());
            if (instance.deleteFile(value.getId())) {
                log.info("file was successfully deleted");
            } else {
                log.info("There wew some problems during deletion");
            }
        });
    }
}