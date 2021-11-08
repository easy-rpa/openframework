package file_moving;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import file_moving.task.MovingFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApModuleEntry(
        name = "moving file from one directory to another"
)
public class FileMovingModule extends ApModule {
    private static final Logger log = LoggerFactory.getLogger(FileMovingModule.class);

    public FileMovingModule() {
    }

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), MovingFile.class).get();
    }

}