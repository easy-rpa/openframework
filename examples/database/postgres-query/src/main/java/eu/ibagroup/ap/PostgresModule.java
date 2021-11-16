package eu.ibagroup.ap;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Postgres Sample Process", description = "This process provides an example to execute Postgres queries")
public class PostgresModule extends ApModule {

    public TaskOutput run() throws Exception {
        TaskOutput output = null;
        output = execute(getInput(), CreateTable.class).get();
        output = execute(getInput(), InsertFiveRecords.class).get();
        output = execute(getInput(), InsertFiveRecordsOrm.class).get();
        output = execute(getInput(), DeleteTwoOldestRecords.class).get();
        output = execute(getInput(), PrintTableContentOrm.class).get();
        output = execute(getInput(), PrintTableContent.class).get();
        output = execute(getInput(), DropTable.class).get();
        return output;
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(PostgresModule.class);
    }
}