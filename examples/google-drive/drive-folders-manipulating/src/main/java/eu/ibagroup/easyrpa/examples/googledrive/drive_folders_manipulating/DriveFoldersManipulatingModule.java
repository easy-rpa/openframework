package eu.ibagroup.easyrpa.examples.googledrive.drive_folders_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.googledrive.drive_folders_manipulating.task.CreateFolder;
import eu.ibagroup.easyrpa.examples.googledrive.drive_folders_manipulating.task.DeleteFolder;
import eu.ibagroup.easyrpa.examples.googledrive.drive_folders_manipulating.task.RenameFolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Drive Folders Manipulating")
public class DriveFoldersManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateFolder.class)
                .thenCompose(execute(RenameFolder.class))
                .thenCompose(execute(DeleteFolder.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(DriveFoldersManipulatingModule.class);
    }
}