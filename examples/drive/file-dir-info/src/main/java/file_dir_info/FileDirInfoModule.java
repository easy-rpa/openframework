package file_dir_info;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import file_dir_info.task.GetFileDirInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "File Dir Info")
public class FileDirInfoModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), GetFileDirInfo.class).get();
    }
}