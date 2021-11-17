package file_moving.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFile;
import eu.ibagroup.easyrpa.openframework.googledrive.folder.GoogleFolderInfo;
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

        log.info("Creating file");
        Optional<GoogleFile> file = drive.createFile(fileName);

        Optional<GoogleFolderInfo> folder = drive.getFolder(newFolderName);

        if (file.isPresent() && folder.isPresent()) {
            log.info("File created in root directory ");

            drive.moveFile(file.get(), folder.get());

            log.info("Now file in directory '{}'", newFolderName);
        } else {
            log.info("File wasn't created or directory not found");
        }
    }
}