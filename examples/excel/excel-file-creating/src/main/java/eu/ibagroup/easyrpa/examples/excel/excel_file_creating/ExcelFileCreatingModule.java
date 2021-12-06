package eu.ibagroup.easyrpa.examples.excel.excel_file_creating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.excel.excel_file_creating.tasks.CreateNewSpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Excel File Creating")
public class ExcelFileCreatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateNewSpreadsheetDocument.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(ExcelFileCreatingModule.class);
    }
}
