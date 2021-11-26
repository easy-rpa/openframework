package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Sheets Manipulating")
public class SheetsDocumentCreatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateNewGoogleSheetsDocument.class)
                .get();
    }
}
