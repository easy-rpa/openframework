package eu.ibagroup.easyrpa.examples.googlesheets.sheets_file_creating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_file_creating.tasks.CreateNewGoogleSheet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Sheets Creating Module")
public class SheetsCreatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateNewGoogleSheet.class).get();
    }

    public static void main(String[] args) {

        ApModuleRunner.localLaunch(SheetsCreatingModule.class);
    }
}
