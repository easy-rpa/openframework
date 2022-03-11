package eu.easyrpa.examples.google.drive.drive_folders_manipulating.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Create Folder")
public class CreateFolder extends ApTask {

    private static final String FOLDER_NAME = "RobotTest";

    @Inject
    private GoogleDrive drive;

    @Output
    private GFileId testFolderId;

    public void execute() {
        log.info("Creating folder with the name '{}'", FOLDER_NAME);
        Optional<GFileInfo> folder = drive.createFolder(FOLDER_NAME);

        if (folder.isPresent()) {
            testFolderId = folder.get().getFileId();
            log.info("Folder was created successfully. ID of created folder: '{}'", testFolderId.getId());
        }
    }
}