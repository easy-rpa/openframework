package eu.easyrpa.examples.excel.macros_running.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Run Macro")
@Slf4j
public class RunMacro extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "macro.function.name")
    private String macroFunctionName;

    @Configuration(value = "output.spreadsheet.file")
    private String outputSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Run macro function '{}' of Excel Document located at '{}'", macroFunctionName, sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        doc.runMacro(macroFunctionName);
        log.info("Running of macro finished successfully.");

        log.info("Save changes to '{}'.", outputSpreadsheetFile);
        doc.saveAs(outputSpreadsheetFile);

        log.info("Excel document is saved successfully.");
    }
}
