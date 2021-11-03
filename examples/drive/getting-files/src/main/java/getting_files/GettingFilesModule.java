package getting_files;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import getting_files.task.GettingFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApModuleEntry(
        name = "Getting list of files"
)
public class GettingFilesModule extends ApModule {
    private static final Logger log = LoggerFactory.getLogger(GettingFilesModule.class);

    public GettingFilesModule() {
    }

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), GettingFiles.class).get();
    }

}