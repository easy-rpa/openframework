package folders_manipulations;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import folders_manipulations.task.CreateFolder;
import folders_manipulations.task.DeleteFolder;
import folders_manipulations.task.RenameFolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Folders Manipulations")
public class FoldersManipulationsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateFolder.class)
                .thenCompose(execute(RenameFolder.class))
                .thenCompose(execute(DeleteFolder.class))
                .get();
    }
}