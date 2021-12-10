package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Sheets Manipulating")
public class SheetsManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ListExistingSheets.class)
                //.thenCompose(execute(ActivateSpecificSheet.class))
                //.thenCompose(execute(RenameSheet.class))
                .thenCompose(execute(CloneSheet.class))
                //.thenCompose(execute(DeleteSheet.class))
                .get();
    }
}
