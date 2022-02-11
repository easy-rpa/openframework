package eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task.UploadNewFile;
import eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task.DeleteFile;
import eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task.RenameFile;
import eu.ibagroup.easyrpa.examples.googledrive.drive_files_manipulating.task.MoveFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Drive Files Manipulating")
public class DriveFilesManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), UploadNewFile.class)
                .thenCompose(execute(RenameFile.class))
                .thenCompose(execute(MoveFile.class))
                .thenCompose(execute(DeleteFile.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(DriveFilesManipulatingModule.class);
    }
}