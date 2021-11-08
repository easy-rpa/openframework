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
        TaskOutput output1 = execute(getInput(), CreateTable.class).get();
        TaskOutput output2 = execute(output1, InsertFiveRecords.class).get();
        TaskOutput output3 = execute(output2, DeleteTwoOldestRecords.class).get();
        TaskOutput output4 = execute(output3, PrintTableContent.class).get();
        TaskOutput output5 = execute(output4, DropTable.class).get();

        return output4;
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(SqlServerModule.class);
    }
}