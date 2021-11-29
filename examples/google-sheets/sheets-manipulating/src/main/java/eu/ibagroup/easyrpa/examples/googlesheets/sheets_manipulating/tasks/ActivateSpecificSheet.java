package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Spreadsheet;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Activate Specific Sheet")
@Slf4j
public class ActivateSpecificSheet extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        int sheetIndex = 2;
        String sheetName = "2";

        log.info("Activate sheet with name '{}' for spreadsheet document with id: {}", sheetName, spreadsheetId);
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheet.getActiveSheet();

        log.info("Active sheet before any action: {}", activeSheet.getName());

        log.info("Activate sheet.");
        activeSheet = spreadsheet.selectSheet(sheetName);

        log.info("Active sheet after activation: {}", activeSheet.getName());

        log.info("Active sheet using index {}.", sheetIndex);
        activeSheet = spreadsheet.selectSheet(sheetIndex);

        log.info("Active sheet after activation: {}", activeSheet.getName());
    }
}
