package file_dir_info.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Get File/Dir Info")
public class GetFileDirInfo extends ApTask {

    private static final String fileName = "newTest";

    @Inject
    private GoogleDrive drive;

    public void execute() {

        log.info("Getting file with the name '{}'", fileName);
        Optional<GoogleFileInfo> file = drive.getFileInfo(fileName);

        file.ifPresent(ob -> {
            log.info("File has name '{}' id '{}'", ob.getName(), ob.getId());
            log.info("File has type '{}' and size '{}' bytes", ob.getFileType(), ob.getSize());
            log.info("File has parents: ");
            ob.getParents().forEach(parent -> log.info("Parent id: {} ", parent));
        });
    }
}