package file_moving.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Move File")
public class MoveFile extends ApTask {

    private final String fileName = "test";

    private final String newFolderName = "newDirectory";

    @Inject
    private GoogleDrive drive;

    public void execute() {

        log.info("Creating File");
        Optional<File> file = drive.createFile(fileName, FileType.DOCUMENT);

        Optional<File> directoryFile = drive.getFileByName(newFolderName);

        if(file.isPresent() && directoryFile.isPresent()) {
            log.info("file created in root directory ");

            drive.moveFileToAnotherFolder(file.get(),directoryFile.get());

            log.info("now file in directory '{}'", newFolderName);
        }else{
            log.info("file wasn't created or directory not found");
        }
    }
}