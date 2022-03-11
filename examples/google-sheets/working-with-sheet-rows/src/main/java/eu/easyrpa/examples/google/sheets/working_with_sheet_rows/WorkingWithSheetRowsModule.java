package eu.easyrpa.examples.google.sheets.working_with_sheet_rows;

import eu.easyrpa.examples.google.sheets.working_with_sheet_rows.tasks.DeleteRows;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.easyrpa.examples.google.sheets.working_with_sheet_rows.tasks.InsertRows;
import eu.easyrpa.examples.google.sheets.working_with_sheet_rows.tasks.LookupAndEditRows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Sheet Rows")
public class WorkingWithSheetRowsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), LookupAndEditRows.class)
                .thenCompose(execute(InsertRows.class))
                .thenCompose(execute(DeleteRows.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithSheetRowsModule.class);
    }
}
