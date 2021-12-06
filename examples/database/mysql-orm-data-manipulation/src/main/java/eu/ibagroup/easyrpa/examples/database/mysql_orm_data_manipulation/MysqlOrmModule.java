package eu.ibagroup.easyrpa.examples.database.mysql_orm_data_manipulation;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.database.mysql_orm_data_manipulation.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "MySQL ORM Sample Process", description = "This process provides an example of ORM data manipulation technique")
public class MysqlOrmModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateTableIfNotExists.class)
                .thenCompose(execute(SaveDataToDB.class))
                .thenCompose(execute(FindObsoleteRecords.class))
                .thenCompose(execute(RenewObsoleteRecords.class))
                .thenCompose(execute(PrintTableContent.class))
                .thenCompose(execute(DropTable.class))
                .get();
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(MysqlOrmModule.class);
    }
}