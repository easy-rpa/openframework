package eu.ibagroup.ap;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "MySQL Sample Process", description = "This process provides an example to execute MySQL queries")
public class MysqlModule extends ApModule {

    public TaskOutput run() throws Exception {
        TaskOutput output1;
        output1 = execute(getInput(), CreateTable.class).get();
        output1 = execute(getInput(), InsertFiveRecords.class).get();
        output1 = execute(getInput(), DeleteTwoOldestRecords.class).get();
        output1 = execute(getInput(), PrintTableContent.class).get();
        output1 = execute(getInput(), PrintTableContentOrm.class).get();

        output1 = execute(getInput(), DropTable.class).get();
        return output1;
    }

    public static void main(String[] arg) {
        ApModuleRunner runner = new ApModuleRunner();
        runner.localLaunch(MysqlModule.class);
    }
}