package eu.easyrpa.examples.google.sheets.spreadsheet_sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Rename Sheet")
@Slf4j
public class RenameSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String sourceSheetName = "Cloned Sheet";
        String newSheetName = "Renamed Sheet";

        log.info("Rename cloned on previous step sheet to '{}' for spreadsheet with ID: {}",
                newSheetName, sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet sheet = doc.selectSheet(sourceSheetName);

        log.info("Current name of sheet: '{}'. Rename it to '{}'.", sheet.getName(), newSheetName);

        sheet.rename(newSheetName);

        log.info("Sheet has been renamed successfully. Current name of sheet: '{}'", sheet.getName());
    }
}
