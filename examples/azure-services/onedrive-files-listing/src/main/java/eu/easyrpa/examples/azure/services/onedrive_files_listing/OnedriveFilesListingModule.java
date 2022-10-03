package eu.easyrpa.examples.azure.services.onedrive_files_listing;

import eu.easyrpa.examples.azure.services.onedrive_files_listing.tasks.OneDriveFilesListing;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
@ApTaskEntry(name = "test module")
public class OnedriveFilesListingModule extends ApModule {
    @Override
    public TaskOutput run() throws ExecutionException, InterruptedException {
        return execute(getInput(), OneDriveFilesListing.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(OnedriveFilesListingModule.class);
    }
}
