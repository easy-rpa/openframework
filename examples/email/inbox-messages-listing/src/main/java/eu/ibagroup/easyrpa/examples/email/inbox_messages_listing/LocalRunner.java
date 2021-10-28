package eu.ibagroup.easyrpa.examples.email.inbox_messages_listing;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(InboxMessagesListingModule.class);
    }
}