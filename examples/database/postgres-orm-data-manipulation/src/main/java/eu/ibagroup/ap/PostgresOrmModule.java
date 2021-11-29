package eu.ibagroup.ap;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Postgres ORM Sample Process", description = "This process provides an example of ORM data manipulation technique")
public class PostgresOrmModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), SaveDataToDB.class)
//                .thenCompose(execute(SaveDataToDB.class))
                .thenCompose(execute(FindObsoleteRecords.class))
                .thenCompose(execute(RenewObsoleteRecords.class))
                .thenCompose(execute(PrintTableContent.class))
//                .thenCompose(execute(DropTable.class))
                .get();
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(PostgresOrmModule.class);
    }
}