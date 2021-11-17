package files_manipulations.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFile;
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
        Optional<GoogleFile> file = drive.createFile(fileName);

        file.ifPresent(ob -> log.info("File was created with the name '{}' id '{}'", ob.getName(), ob.getId()));
    }
}