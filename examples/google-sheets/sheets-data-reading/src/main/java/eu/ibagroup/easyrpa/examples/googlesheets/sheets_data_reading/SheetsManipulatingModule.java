package eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading.tasks.ReadSheetData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Sheets Manipulating")
public class SheetsManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadSheetData.class).get();
    }
}
