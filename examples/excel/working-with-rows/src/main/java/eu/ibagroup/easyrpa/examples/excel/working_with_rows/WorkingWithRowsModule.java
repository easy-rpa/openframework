package eu.ibagroup.easyrpa.examples.excel.working_with_rows;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks.*;
import eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Rows")
public class WorkingWithRowsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), LookupAndEditRows.class)
                .thenCompose(execute(AddInsertRows.class))
                .thenCompose(execute(DeleteRows.class))
                .get();
    }
}
