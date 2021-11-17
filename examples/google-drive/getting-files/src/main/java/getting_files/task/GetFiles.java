package getting_files.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Get Files")
public class GetFiles extends ApTask {

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting list of all files");
        List<GoogleFileInfo> files = drive.listFiles();

        log.info("List of files:");
        files.forEach(file -> log.info("Name: '{}' id: '{}'",file.getName(),file.getId()));
    }
}