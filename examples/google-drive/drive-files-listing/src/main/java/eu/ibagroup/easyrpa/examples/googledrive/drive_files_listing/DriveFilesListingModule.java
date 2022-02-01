package eu.ibagroup.easyrpa.examples.googledrive.drive_files_listing;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.googledrive.drive_files_listing.task.ListAllFiles;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Drive Files Listing")
public class DriveFilesListingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), ListAllFiles.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(DriveFilesListingModule.class);
    }
}