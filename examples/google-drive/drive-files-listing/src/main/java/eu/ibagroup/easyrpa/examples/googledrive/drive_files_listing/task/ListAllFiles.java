package eu.ibagroup.easyrpa.examples.googledrive.drive_files_listing.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.model.GFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "List All Files")
public class ListAllFiles extends ApTask {

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting the list of all files");
        List<GFileInfo> files = drive.listFiles();

        files.forEach(file -> log.info("Name: '{}' id: '{}'", file.getName(), file.getId()));
    }
}