package eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Clone Sheet")
@Slf4j
public class CloneSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {

        String clonedSheetName = "Cloned Sheet";

        log.info("Clone active by default sheet for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);

        Sheet clonedSheet = doc.cloneSheet(doc.getActiveSheet().getName());
        log.info("Sheet '{}' has been cloned successfully.", clonedSheet.getName());

        log.info("Rename cloned sheet to '{}'.", clonedSheetName);
        doc.renameSheet(clonedSheet, clonedSheetName);
    }
}
