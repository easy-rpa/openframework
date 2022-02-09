package eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.drive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Move File")
public class MoveFile extends ApTask {

    private static final String TARGET_FOLD_NAME = "RobotFiles";

    @Inject
    private GoogleDrive drive;

    @Input
    private GFileId testFileId;

    public void execute() {
        log.info("Get info regarding file with ID '{}' from Google Drive.", testFileId);
        Optional<GFileInfo> testFile = drive.getFileInfo(testFileId);

        if (testFile.isPresent()) {
            log.info("Check existence of folder with name '{}' and create it if absent.", TARGET_FOLD_NAME);
            GFileInfo targetFolder = drive.createFolderIfAbsent(TARGET_FOLD_NAME);

            log.info("Move file to folder with ID '{}'.", targetFolder.getId());
            drive.moveFile(testFile.get(), targetFolder.getFileId());

            log.info("File moved successfully.");
        } else {
            log.info("File with ID '{}' is not found.", testFileId.getId());
        }
    }
}