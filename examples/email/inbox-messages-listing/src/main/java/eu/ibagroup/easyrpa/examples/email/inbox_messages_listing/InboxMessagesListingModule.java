package eu.ibagroup.easyrpa.examples.email.inbox_messages_listing;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.inbox_messages_listing.tasks.GetInboxMessages;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Inbox Messages Listing")
public class InboxMessagesListingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), GetInboxMessages.class).get();
    }
}
