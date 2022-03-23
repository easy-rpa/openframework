package eu.easyrpa.examples.google.sheets.spreadsheet_sheets_manipulating.tasks;

import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "List Existing Sheets")
@Slf4j
public class ListExistingSheets extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        log.info("List all sheets from spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);

        List<String> sheetNames = doc.getSheetNames();
        log.info("Spreadsheet document has following sheets: {}", sheetNames);
    }
}
