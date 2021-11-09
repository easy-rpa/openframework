package file_moving;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import file_moving.task.MoveFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "File Moving")
public class FileMovingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), MoveFile.class).get();
    }
}