package eu.easyrpa.examples.database.working_with_raw_sql;

import eu.easyrpa.examples.database.working_with_raw_sql.tasks.*;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Raw SQL")
public class WorkingWithRawSqlModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateInvoicesTable.class)
                .thenCompose(execute(InsertInvoiceRecords.class))
                .thenCompose(execute(SelectInvoicesFromTable.class))
                .thenCompose(execute(UpdateOutdatedInvoices.class))
                .thenCompose(execute(DeleteOutdatedInvoices.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithRawSqlModule.class);
    }
}
