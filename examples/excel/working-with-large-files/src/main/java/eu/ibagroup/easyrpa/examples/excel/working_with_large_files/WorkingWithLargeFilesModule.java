package eu.ibagroup.easyrpa.examples.excel.working_with_large_files;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.working_with_large_files.tasks.ReadLargeFile;
import eu.ibagroup.easyrpa.openframework.core.utils.MemoryMonitor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Large Files")
public class WorkingWithLargeFilesModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadLargeFile.class)
//                .thenCompose(execute(WriteLargeFile.class))
                .get();
    }
}
