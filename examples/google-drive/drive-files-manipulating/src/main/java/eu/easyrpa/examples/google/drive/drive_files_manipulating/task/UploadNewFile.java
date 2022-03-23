package eu.easyrpa.examples.google.drive.drive_files_manipulating.task;

import eu.easyrpa.openframework.core.utils.FilePathUtils;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFile;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Upload New File")
public class UploadNewFile extends ApTask {

    private static final String FILE_NAME = "RobotTest";
    private static final String FILE_PATH = "test.docx";

    @Inject
    private GoogleDrive drive;

    @Output
    private GFileId testFileId;

    public void execute() throws FileNotFoundException {

        log.info("Upload file located at '{}' with name '{}'", FILE_PATH, FILE_NAME);
        Optional<GFile> file = drive.createFile(FILE_NAME, new FileInputStream(FilePathUtils.getFile(FILE_PATH)));

        if (file.isPresent()) {
            testFileId = file.get().getFileId();
            log.info("File was created successfully. Id of created file: '{}'", testFileId.getId());
        }
    }
}