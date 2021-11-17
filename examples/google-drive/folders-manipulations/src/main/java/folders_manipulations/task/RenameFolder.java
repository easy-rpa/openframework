package folders_manipulations.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.folder.GoogleFolderInfo;
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
        Optional<GoogleFolderInfo> file = drive.getFolder(folderName);

        if (file.isPresent()) {
            if (drive.renameFolder(file.get(), "RenamedFolder")) {
                log.info("Folder successfully renamed");
            } else {
                log.info("Error during renaming folder");
            }
        } else {
            log.info("There were some error during getting folder");
        }
    }
}