package eu.easyrpa.examples.google.sheets.export_spreadsheet_to_xlsx;

import eu.easyrpa.examples.google.sheets.export_spreadsheet_to_xlsx.tasks.ExportSpreadsheet;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Export Spreadsheet to XLSX")
public class ExportSpreadsheetToXlsxModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ExportSpreadsheet.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(ExportSpreadsheetToXlsxModule.class);
    }
}
