package google_sheet_creating.task;

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
        name = "Google sheet document creation"
)
public class CreateGoogleSheet extends ApTask {
    private static final Logger log = LoggerFactory.getLogger(CreateGoogleSheet.class);

    @Configuration("drive.credentials.filepath")
    private String filePath;

    private static final String fileName = "testSheet";

    public CreateGoogleSheet() {
    }

    public void execute() {
        log.info("Initialize Google Drive Service instance");
        GoogleDriveService instance = new GoogleDriveServiceProvider().setCredentials(filePath).connect();

        log.info("Creating spreadsheet with the name '{}'", fileName);
        Optional<File> file = instance.createFile(fileName, FileType.SPREADSHEET);

        file.ifPresent(ob->log.info("Spreadsheet was created with the name '{}' id '{}'",ob.getName(),ob.getId()));
    }
}