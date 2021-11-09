package getting_files.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Get files")
public class GetFiles extends ApTask {

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting list of all files");
        List<File> files = drive.getFiles();

        log.info("List of Files:");
        files.forEach(file -> log.info("name: '{}' id: '{}'",file.getName(),file.getId()));
    }
}