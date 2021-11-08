package eu.ibagroup.easyrpa.examples.email.folders_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Delete Folder")
@Slf4j
public class DeleteFolder extends ApTask {

    @Inject
    private EmailClient emailClient;

    @Input("folderName")
    private String folderName;

    @Override
    public void execute() {
        log.info("Deleting of folder '{}'.", folderName);

        if (emailClient.deleteFolder(folderName)) {
            log.info("Folder is deleted successfully.");
        } else {
            if (!emailClient.listFolders().contains(folderName)) {
                log.warn("Cannot delete folder. Folder is not exist.");
            } else {
                log.warn("Something went wrong. Folder is not deleted.");
            }
        }
    }
}
