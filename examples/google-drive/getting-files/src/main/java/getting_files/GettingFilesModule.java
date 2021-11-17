package getting_files;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import getting_files.task.GetFiles;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Getting Files")
public class GettingFilesModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), GetFiles.class).get();
    }
}