package eu.easyrpa.examples.google.sheets.spreadsheet_sheets_manipulating.tasks;

import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Activate Specific Sheet")
@Slf4j
public class ActivateSpecificSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        int sheetIndex = 1;
        String sheetName = "Summary";

        log.info("Activate sheet with name '{}' for spreadsheet with ID: {}", sheetName, sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet before any action: {}", activeSheet.getName());

        log.info("Activate sheet.");
        activeSheet = doc.selectSheet(sheetName);

        log.info("Active sheet after activation: {}", activeSheet.getName());

        log.info("Active sheet using index {}.", sheetIndex);
        activeSheet = doc.selectSheet(sheetIndex);

        log.info("Active sheet after activation: {}", activeSheet.getName());
    }
}
