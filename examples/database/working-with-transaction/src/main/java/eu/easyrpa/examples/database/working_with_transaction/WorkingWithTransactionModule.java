package eu.easyrpa.examples.database.working_with_transaction;

import eu.easyrpa.examples.database.working_with_transaction.tasks.AddRecordsUsingTransaction;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Transaction")
public class WorkingWithTransactionModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), AddRecordsUsingTransaction.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithTransactionModule.class);
    }
}
