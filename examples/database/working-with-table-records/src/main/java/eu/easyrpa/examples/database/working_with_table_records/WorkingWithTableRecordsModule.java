package eu.easyrpa.examples.database.working_with_table_records;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.easyrpa.examples.database.working_with_table_records.tasks.DeleteTableRecords;
import eu.easyrpa.examples.database.working_with_table_records.tasks.ReadTableRecords;
import eu.easyrpa.examples.database.working_with_table_records.tasks.AddNewTableRecords;
import eu.easyrpa.examples.database.working_with_table_records.tasks.UpdateTableRecords;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Table Records")
public class WorkingWithTableRecordsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), AddNewTableRecords.class)
                .thenCompose(execute(ReadTableRecords.class))
                .thenCompose(execute(UpdateTableRecords.class))
                .thenCompose(execute(DeleteTableRecords.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithTableRecordsModule.class);
    }
}
