package eu.easyrpa.examples.azure.services.onedrive_files_listing;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApTaskEntry(name = "test module")
public class OnedriveFilesListingModule extends ApModule {
    @Override
    public TaskOutput run() {
        return null;
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(OnedriveFilesListingModule.class);
    }
}
