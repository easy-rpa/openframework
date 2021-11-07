package eu.ibagroup.easyrpa.examples.email.folders_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Create New Folder")
@Slf4j
public class CreateNewFolder extends ApTask {

    private static final String CUSTOM_FOLDER_NAME = "EasyRPA Folder";

    @Inject
    private EmailClient emailClient;

    @Output("folderName")
    private String folderName;

    @Override
    public void execute() {
        log.info("Creating of new folder with name '{}'", CUSTOM_FOLDER_NAME);

        if (emailClient.createFolder(CUSTOM_FOLDER_NAME)) {
            log.info("Folder is created successfully.");
            folderName = CUSTOM_FOLDER_NAME;
        } else {
            log.warn("Something went wrong. Folder is not created.");
        }
    }
}
