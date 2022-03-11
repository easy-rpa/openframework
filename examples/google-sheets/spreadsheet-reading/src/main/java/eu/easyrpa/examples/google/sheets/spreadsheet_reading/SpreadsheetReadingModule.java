package eu.easyrpa.examples.google.sheets.spreadsheet_reading;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.easyrpa.examples.google.sheets.spreadsheet_reading.tasks.ReadListOfTypedRecords;
import eu.easyrpa.examples.google.sheets.spreadsheet_reading.tasks.ReadRangeOfData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Spreadsheet Reading")
public class SpreadsheetReadingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadListOfTypedRecords.class)
                .thenCompose(execute(ReadRangeOfData.class))
                .get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SpreadsheetReadingModule.class);
    }
}
