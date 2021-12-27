package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Rename Sheet")
@Slf4j
public class RenameSheet extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        String newSheetName = "RenamedSheet";

        log.info("Rename active by default sheet to '{}' for spreadsheet with id: {}", newSheetName, spreadsheetId);
        SpreadsheetDocument spreadsheetDocument = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheetDocument.getActiveSheet();

        log.info("Current name of active sheet: '{}'. Rename it to '{}'.", activeSheet.getName(), newSheetName);
        activeSheet.rename(newSheetName);
        log.info("Sheet has been renamed successfully. Current name of active sheet: '{}'", activeSheet.getName());

        spreadsheetDocument.commit();
    }
}
