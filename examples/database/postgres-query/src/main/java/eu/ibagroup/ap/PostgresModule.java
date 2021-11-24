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
        return execute(getInput(), CreateTable.class)
                .thenCompose(execute(InsertFiveRecords.class))
                .thenCompose(execute(InsertFiveRecordsOrm.class))
                .thenCompose(execute(DeleteTwoOldestRecords.class))
                .thenCompose(execute(PrintTableContent.class))
                .thenCompose(execute(PrintTableContentOrm.class))
                .thenCompose(execute(DropTable.class))
                .get();
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(PostgresModule.class);
    }
}