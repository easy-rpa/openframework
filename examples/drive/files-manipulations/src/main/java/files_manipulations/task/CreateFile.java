package files_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Create File")
public class CreateFile extends ApTask {

    private static final String fileName = "creationTest";

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Creating file with the name '{}'", fileName);
        Optional<File> file = drive.createFile(fileName, FileType.DOCUMENT);

        file.ifPresent(ob->log.info("file was created with the name '{}' id '{}'",ob.getName(),ob.getId()));
    }
}