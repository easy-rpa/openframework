package eu.ibagroup.easyrpa.examples.excel.sheets_copying;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.sheets_copying.tasks.CopySheetBetweenSpreadsheets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Sheets Copying")
public class SheetsCopyingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CopySheetBetweenSpreadsheets.class).get();
    }
}
