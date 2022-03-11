package eu.easyrpa.examples.email.folders_listing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.easyrpa.openframework.email.EmailClient;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Get Mailbox Folders")
@Slf4j
public class GetMailboxFolders extends ApTask {

    @Configuration(value = "mailbox")
    private SecretCredentials mailboxCredentials;

    @Inject
    private EmailClient emailClient;

    @Override
    public void execute() {

        log.info("Getting all available folders of '{}' mailbox.", mailboxCredentials.getUser());

        log.info("Fetch folder names using email client.");
        List<String> folders = emailClient.listFolders();

        log.info("Fetched folders:");
        folders.forEach(log::info);
    }
}
