package eu.easyrpa.examples.excel.working_with_rows;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.easyrpa.examples.excel.working_with_rows.tasks.DeleteRows;
import eu.easyrpa.examples.excel.working_with_rows.tasks.InsertRows;
import eu.easyrpa.examples.excel.working_with_rows.tasks.LookupAndEditRows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Rows")
public class WorkingWithRowsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), LookupAndEditRows.class)
                .thenCompose(execute(InsertRows.class))
                .thenCompose(execute(DeleteRows.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithRowsModule.class);
    }
}
