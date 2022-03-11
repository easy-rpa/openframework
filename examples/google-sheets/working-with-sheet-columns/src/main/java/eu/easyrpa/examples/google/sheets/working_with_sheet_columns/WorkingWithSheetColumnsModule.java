package eu.easyrpa.examples.google.sheets.working_with_sheet_columns;

import eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks.DeleteColumns;
import eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks.MoveColumns;
import eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks.ReadColumnCells;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks.AddInsertColumns;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working with Sheet Columns")
public class WorkingWithSheetColumnsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadColumnCells.class)
                .thenCompose(execute(AddInsertColumns.class))
                .thenCompose(execute(MoveColumns.class))
                .thenCompose(execute(DeleteColumns.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithSheetColumnsModule.class);
    }
}
