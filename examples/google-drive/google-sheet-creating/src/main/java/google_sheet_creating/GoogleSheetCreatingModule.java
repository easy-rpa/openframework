package google_sheet_creating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import google_sheet_creating.task.CreateGoogleSheet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Google Sheet Creating")
public class GoogleSheetCreatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateGoogleSheet.class).get();
    }
}