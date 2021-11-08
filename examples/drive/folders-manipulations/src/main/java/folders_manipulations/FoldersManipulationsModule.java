package folders_manipulations;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import folders_manipulations.task.CreateFolder;
import folders_manipulations.task.DeleteFolder;
import folders_manipulations.task.RenameFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApModuleEntry(
        name = "Files manipulations"
)
public class FoldersManipulationsModule extends ApModule {
    private static final Logger log = LoggerFactory.getLogger(FoldersManipulationsModule.class);

    public FoldersManipulationsModule() {
    }

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateFolder.class)
                .thenCompose(execute(RenameFolder.class))
                .thenCompose(execute(DeleteFolder.class))
                .get();
    }

}