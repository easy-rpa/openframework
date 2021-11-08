package folders_manipulations.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApTaskEntry(
        name = "Folder creation"
)
public class CreateFolder extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(CreateFolder.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String folderName = "creationTestFolder";

    public CreateFolder() {
    }

    public void execute() {
        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Creating folder with the name '{}'", folderName);
        Optional<File> file = instance.createFile(folderName, FileType.FOLDER);

        file.ifPresent(ob->log.info("Folder was created with the name '{}' id '{}'",ob.getName(),ob.getId()));
    }
}