package getting_files.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.googledrive.utils.GoogleDriveServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApTaskEntry(
        name = "Getting list of all files"
)
public class GettingFiles extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(GettingFiles.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    public GettingFiles() {
    }

    public void execute() {

        log.info("Getting list of files from google drive, creds from '{}'", this.filePath);

        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Getting list of all files");
        List<File> files = instance.getFiles();

        log.info("List of Files:");
        files.forEach(file -> log.info("name: '{}' id: '{}'",file.getName(),file.getId()));
    }
}