package eu.easyrpa.examples.google.drive.drive_folders_manipulating.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Delete Folder")
public class DeleteFolder extends ApTask {

    @Inject
    private GoogleDrive drive;

    @Input
    private GFileId testFolderId;

    public void execute() {
        log.info("Delete folder with ID '{}' from Google Drive.", testFolderId);
        drive.deleteFolder(testFolderId);

        log.info("Folder deleted successfully.");
    }
}