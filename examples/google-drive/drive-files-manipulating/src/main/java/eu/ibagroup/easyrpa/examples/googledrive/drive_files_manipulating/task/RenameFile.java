package eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task;

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
@ApTaskEntry(name = "Rename File")
public class RenameFile extends ApTask {

    private static final String NEW_FILE_NAME = "RobotTestRenamed";

    @Inject
    private GoogleDrive drive;

    @Input
    private GFileId testFileId;

    public void execute() {
        log.info("Get info regarding file with ID '{}' from Google Drive.", testFileId);
        Optional<GFileInfo> testFile = drive.getFileInfo(testFileId);

        if (testFile.isPresent()) {
            log.info("Rename file to '{}'.", NEW_FILE_NAME);
            drive.renameFile(testFile.get(), NEW_FILE_NAME);

            log.info("File renamed successfully.");
        } else {
            log.info("File with ID '{}' is not found.", testFileId.getId());
        }
    }
}