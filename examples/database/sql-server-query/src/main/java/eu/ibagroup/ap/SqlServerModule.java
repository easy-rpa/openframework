package eu.ibagroup.ap;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "MS Sql-Server Sample Process", description = "This process provides an example to execute SQL Server queries")
public class SqlServerModule extends ApModule {

    public TaskOutput run() throws Exception {
        TaskOutput output1;
        output1 = execute(getInput(), CreateTableTask.class).get();
        output1 = execute(getInput(), InsertFiveRecordsTask.class).get();
        output1 = execute(getInput(), DeleteTwoOldestRecordsTask.class).get();
        output1 = execute(getInput(), PrintTableContentTask.class).get();
        //output1 = execute(getInput(), DropTableTask.class).get();
        return output1;
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(SqlServerModule.class);
    }
}