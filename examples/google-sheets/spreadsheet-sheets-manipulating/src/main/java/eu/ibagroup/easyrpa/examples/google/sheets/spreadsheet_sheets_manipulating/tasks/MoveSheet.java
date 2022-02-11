package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.Sheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Move Sheet")
@Slf4j
public class MoveSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String sourceSheetName = "Renamed Sheet";

        log.info("Move renamed sheet of spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet sheet = doc.selectSheet(sourceSheetName);

        int newPosition = 1;
        log.info("Move sheet '{}' to position '{}'.", sheet.getName(), newPosition);

        sheet.moveTo(newPosition);

        log.info("Sheet '{}' has been moved to '{}' position successfully.", sheet.getName(), newPosition);
    }
}
