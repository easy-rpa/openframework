package eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "MS Sql-Server Sample Process", description = "This process provides an example to execute Postgres queries")
public class SqlServerModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateTable.class)
                .thenCompose(execute(InsertRecordsToDB.class))
                .thenCompose(execute(DeleteObsoleteRecords.class))
                .thenCompose(execute(PrintTableContent.class))
                .thenCompose(execute(DropTable.class))
                .get();
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(SqlServerModule.class);
    }
}