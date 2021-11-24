package eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables.tasks.CreatePivotTable;
import eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables.tasks.ReadPivotTable;
import eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables.tasks.UpdatePivotTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Pivot Tables")
public class WorkingWithPivotTablesModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreatePivotTable.class)
                .thenCompose(execute(ReadPivotTable.class))
                .thenCompose(execute(UpdatePivotTable.class))
                .get();
    }
}
