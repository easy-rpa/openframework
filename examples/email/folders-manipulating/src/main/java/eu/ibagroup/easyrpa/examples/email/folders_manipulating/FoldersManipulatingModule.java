package eu.ibagroup.easyrpa.examples.email.folders_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.email.folders_manipulating.tasks.CreateNewFolder;
import eu.ibagroup.easyrpa.examples.email.folders_manipulating.tasks.DeleteFolder;
import eu.ibagroup.easyrpa.examples.email.folders_manipulating.tasks.RenameFolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Folders Manipulating")
public class FoldersManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        TaskOutput taskOutput = execute(getInput(), CreateNewFolder.class).get();

        if (taskOutput.get("folderName") == null) {
            return taskOutput;
        } else {
            return execute(taskOutput, RenameFolder.class)
                    .thenCompose(execute(DeleteFolder.class))
                    .get();
        }
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(FoldersManipulatingModule.class);
    }
}
