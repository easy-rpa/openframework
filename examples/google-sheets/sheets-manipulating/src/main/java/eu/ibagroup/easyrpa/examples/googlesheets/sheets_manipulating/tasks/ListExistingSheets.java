package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "List Existing Sheets")
@Slf4j
public class ListExistingSheets extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    GoogleSheets service;

    @Override
    public void execute() {

        log.info("List all sheets from spreadsheet document with id: {}", spreadsheetId);
        SpreadsheetDocument spreadsheetDocument = service.getSpreadsheet(spreadsheetId);

        List<String> sheetNames = spreadsheetDocument.getSheetNames();
        log.info("Spreadsheet document has following sheets: {}", sheetNames);
    }
}
