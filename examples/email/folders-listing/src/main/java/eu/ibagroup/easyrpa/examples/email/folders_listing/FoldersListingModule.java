package eu.ibagroup.easyrpa.examples.email.folders_listing;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.folders_listing.tasks.GetMailboxFolders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Folders Listing")
public class FoldersListingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), GetMailboxFolders.class).get();
    }
}
