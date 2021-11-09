package folders_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Create Folder")
public class CreateFolder extends ApTask {

    private static final String folderName = "creationTestFolder";

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Creating folder with the name '{}'", folderName);
        Optional<File> file = drive.createFile(folderName, FileType.FOLDER);

        file.ifPresent(ob->log.info("Folder was created with the name '{}' id '{}'",ob.getName(),ob.getId()));
    }
}