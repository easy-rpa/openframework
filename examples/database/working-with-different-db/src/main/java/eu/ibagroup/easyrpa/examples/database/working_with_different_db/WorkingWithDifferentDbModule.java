package eu.ibagroup.easyrpa.examples.database.working_with_different_db;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.database.working_with_different_db.tasks.WorkWithSeveralDatabases;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Different DB")
public class WorkingWithDifferentDbModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), WorkWithSeveralDatabases.class)
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithDifferentDbModule.class);
    }
}
