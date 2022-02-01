package eu.ibagroup.easyrpa.examples.googledrive.drive_folders_manipulating.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Rename Folder")
public class RenameFolder extends ApTask {

    private static final String NEW_FOLDER_NAME = "RobotTest Renamed";

    @Inject
    private GoogleDrive drive;

    @Input
    private GFileId testFolderId;

    public void execute() {
        log.info("Get folder with ID '{}' from Google Drive.", testFolderId);
        Optional<GFileInfo> folder = drive.getFolder(testFolderId);

        if (folder.isPresent()) {
            log.info("Rename folder to '{}'.", NEW_FOLDER_NAME);
            drive.renameFolder(folder.get(), NEW_FOLDER_NAME);

            log.info("Folder renamed successfully.");
        } else {
            log.info("Folder with ID '{}' is not found", testFolderId.getId());
        }
    }
}