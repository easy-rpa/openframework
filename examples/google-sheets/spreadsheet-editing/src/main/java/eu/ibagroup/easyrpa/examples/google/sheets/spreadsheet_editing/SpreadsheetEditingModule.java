package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.tasks.EditCellsOnSheet;
import eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.tasks.EditRecordsOnSheet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Spreadsheet Editing")
public class SpreadsheetEditingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), EditRecordsOnSheet.class)
                .thenCompose(execute(EditCellsOnSheet.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SpreadsheetEditingModule.class);
    }
}
