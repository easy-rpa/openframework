package google_sheet_creating.task;

import com.google.api.services.drive.model.File;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.FileType;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Create Google Sheet")
public class CreateGoogleSheet extends ApTask {

    private static final String fileName = "testSheet";

    @Inject
    GoogleDrive drive;

    public void execute() {
        log.info("Creating spreadsheet with the name '{}'", fileName);
        Optional<File> file = drive.createFile(fileName, FileType.SPREADSHEET);

        file.ifPresent(ob->log.info("Spreadsheet was created with the name '{}' id '{}'",ob.getName(),ob.getId()));
    }
}