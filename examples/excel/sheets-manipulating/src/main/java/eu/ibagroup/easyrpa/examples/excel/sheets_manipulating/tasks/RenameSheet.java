package eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Rename Sheet")
@Slf4j
public class RenameSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String newSheetName = "Renamed Sheet";

        log.info("Rename active by default sheet to '{}' for spreadsheet document located at: {}", newSheetName, sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();
        String oldSheetName = activeSheet.getName();

        log.info("Current name of active sheet: '{}'. Rename it to '{}'.", activeSheet.getName(), newSheetName);
        doc.renameSheet(activeSheet, newSheetName);
        log.info("Sheet has been renamed successfully. Current name of active sheet: '{}'", activeSheet.getName());

        log.info("Rename active sheet back to old name.");
        doc.renameSheet(activeSheet, oldSheetName);
    }
}
