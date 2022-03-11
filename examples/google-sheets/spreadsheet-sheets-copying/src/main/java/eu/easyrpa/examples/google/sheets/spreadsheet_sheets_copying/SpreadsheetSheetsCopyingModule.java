package eu.easyrpa.examples.google.sheets.spreadsheet_sheets_copying;

import eu.easyrpa.examples.google.sheets.spreadsheet_sheets_copying.tasks.CopySheetBetweenSpreadsheets;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Spreadsheet Sheets Copying")
public class SpreadsheetSheetsCopyingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CopySheetBetweenSpreadsheets.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SpreadsheetSheetsCopyingModule.class);
    }
}
