package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Delete Sheet")
@Slf4j
public class DeleteSheet extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        log.info("Delete the last sheet from spreadsheet document with id: {}", spreadsheetId);
        SpreadsheetDocument spreadsheetDocument = service.getSpreadsheet(spreadsheetId);
        List<String> sheetNames = spreadsheetDocument.getSheetNames();
        String lastSheetName = sheetNames.get(sheetNames.size() - 1);

        log.info("Delete sheet with name '{}'.", lastSheetName);
        spreadsheetDocument.removeSheet(lastSheetName);

        log.info("Sheet '{}' has been deleted successfully.", lastSheetName);
    }
}
