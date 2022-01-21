package eu.ibagroup.easyrpa.examples.email.inbox_messages_listing;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.email.inbox_messages_listing.tasks.GetInboxMessages;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Inbox Messages Listing")
public class InboxMessagesListingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), GetInboxMessages.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(InboxMessagesListingModule.class);
    }
}
