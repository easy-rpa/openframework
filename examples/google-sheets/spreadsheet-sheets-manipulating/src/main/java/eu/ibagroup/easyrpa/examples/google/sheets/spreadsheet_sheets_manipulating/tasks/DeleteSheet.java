package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Delete Sheet")
@Slf4j
public class DeleteSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String sourceSheetName = "Renamed Sheet";

        log.info("Delete sheet with name '{}' from spreadsheet with ID: {}", sourceSheetName, sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);

        doc.removeSheet(sourceSheetName);

        log.info("Sheet '{}' has been deleted successfully.", sourceSheetName);
    }
}
