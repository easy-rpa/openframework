package google_sheet_creating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import google_sheet_creating.task.CreateGoogleSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApModuleEntry(
        name = "Files manipulations"
)
public class GoogleSheetCreatingModule extends ApModule {
    private static final Logger log = LoggerFactory.getLogger(GoogleSheetCreatingModule.class);

    public GoogleSheetCreatingModule() {
    }

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateGoogleSheet.class).get();
    }

}