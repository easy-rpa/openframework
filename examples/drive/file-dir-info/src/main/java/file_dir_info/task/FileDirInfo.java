package file_dir_info.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApTaskEntry(
        name = "Google sheet document creation"
)
public class FileDirInfo extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(FileDirInfo.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String fileName = "newTest";

    public FileDirInfo() {
    }

    public void execute() {
        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Getting file with the name '{}'", fileName);
        Optional<File> file = instance.getFileByName(fileName);

        if (file.isPresent()) {
            file = instance.getFullFileInfoById(file.get().getId());
        }

        file.ifPresent(ob -> {
            log.info("File has name '{}' id '{}'", ob.getName(), ob.getId());
            log.info("File has type '{}' and size '{}' bytes", ob.getMimeType(), ob.getSize());
            log.info("File has parents: ");
            ob.getParents().forEach(parent -> log.info("parent id: {} ", parent));
        });
    }
}