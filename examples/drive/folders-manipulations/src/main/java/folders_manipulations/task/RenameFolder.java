package folders_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Rename Folder")
public class RenameFolder extends ApTask {

    private static final String folderName = "creationTestFolder";

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting folder '{}' from google drive", folderName);
        Optional<File> file = drive.getFileByName(folderName);

        if (file.isPresent()) {
            boolean result = drive.renameFile(file.get(), "RenamedFolder");
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