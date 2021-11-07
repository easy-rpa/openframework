package eu.ibagroup.easyrpa.examples.email.folders_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailClient;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Rename Folder")
@Slf4j
public class RenameFolder extends ApTask {

    private static final String NEW_FOLDER_NAME = "EasyRPA Renamed";

    @Inject
    private EmailClient emailClient;

    @Input("folderName")
    @Output
    private String folderName;

    @Override
    public void execute() {
        log.info("Renaming of folder '{}' to '{}'", folderName, NEW_FOLDER_NAME);

        if (emailClient.renameFolder(folderName, NEW_FOLDER_NAME)) {
            log.info("Folder is renamed successfully.");
            folderName = NEW_FOLDER_NAME;
        } else {
            log.warn("Something went wrong. Folder is not renamed.");
        }
    }
}
