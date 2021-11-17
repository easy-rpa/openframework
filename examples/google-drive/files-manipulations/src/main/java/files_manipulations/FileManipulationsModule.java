package files_manipulations;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import files_manipulations.task.CreateFile;
import files_manipulations.task.DeleteFile;
import files_manipulations.task.RenameFile;
import files_manipulations.task.UploadFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "File Manipulations")
public class FileManipulationsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateFile.class)
                .thenCompose(execute(DeleteFile.class))
                .thenCompose(execute(UploadFile.class))
                .thenCompose(execute((RenameFile.class)))
                .get();
    }
}