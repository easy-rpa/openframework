package eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileId;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Delete File")
public class DeleteFile extends ApTask {

    @Inject
    private GoogleDrive drive;

    @Input
    private GFileId testFileId;

    public void execute() {
        log.info("Delete file with ID '{}' from Google Drive", testFileId);
        drive.deleteFile(testFileId);

        log.info("File deleted successfully.");
    }
}