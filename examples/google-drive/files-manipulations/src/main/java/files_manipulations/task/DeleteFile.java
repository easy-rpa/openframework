package files_manipulations.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFile;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Delete File")
public class DeleteFile extends ApTask {

    private static final String fileName = "creationTest";

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting file from Google Drive");
        Optional<GoogleFile> file = drive.getFile(fileName);

        file.ifPresent(value -> {
            log.info("Trying to delete file with the name: '{}' , id: '{}'", value.getName(), value.getId());
            if (drive.deleteFile(value.getId())) {
                log.info("File was successfully deleted");
            } else {
                log.info("There wew some problems during deletion");
            }
        });
    }
}