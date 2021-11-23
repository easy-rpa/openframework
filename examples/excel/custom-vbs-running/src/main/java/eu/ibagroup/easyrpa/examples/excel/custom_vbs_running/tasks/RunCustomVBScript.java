package eu.ibagroup.easyrpa.examples.excel.custom_vbs_running.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Run Custom VB Script")
@Slf4j
public class RunCustomVBScript extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "vb.script.file")
    private String vbScriptFile;

    @Configuration(value = "output.spreadsheet.file")
    private String outputSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Run VB script '{}' for spreadsheet document located at '{}'", vbScriptFile, sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        doc.runScript(vbScriptFile);
        log.info("Running of VB script finished successfully.");

        log.info("Save changes to '{}'.", outputSpreadsheetFile);
        doc.saveAs(outputSpreadsheetFile);

        log.info("Excel document is saved successfully.");
    }
}
