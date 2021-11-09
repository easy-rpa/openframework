package folders_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Delete Folder")
public class DeleteFolder extends ApTask {

    private static final String folderName = "RenamedFolder";

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting Folder from Google Drive");
        Optional<File> file = drive.getFileByName(folderName);

        file.ifPresent(value -> {
            log.info("Trying to delete folder with the name: '{}' , id: '{}'", value.getName(), value.getId());
            if (drive.deleteFile(value.getId())) {
                log.info("folder was successfully deleted");
            } else {
                log.info("There wew some problems during deletion");
            }
        });
    }
}