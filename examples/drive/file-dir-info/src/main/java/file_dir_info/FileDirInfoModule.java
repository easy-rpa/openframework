package file_dir_info;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import file_dir_info.task.FileDirInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApModuleEntry(
        name = "Files manipulations"
)
public class FileDirInfoModule extends ApModule {
    private static final Logger log = LoggerFactory.getLogger(FileDirInfoModule.class);

    public FileDirInfoModule() {
    }

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), FileDirInfo.class).get();
    }

}