package eu.ibagroup.easyrpa.examples.excel.working_with_rows;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Columns")
public class WorkingWithColumnsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadColumnCells.class)
                .thenCompose(execute(AddInsertColumns.class))
                .thenCompose(execute(MoveColumns.class))
                .thenCompose(execute(DeleteColumns.class))
                .thenCompose(execute(SortTableColumns.class))
                .thenCompose(execute(FilterTableColumns.class))
                .get();
    }
}
