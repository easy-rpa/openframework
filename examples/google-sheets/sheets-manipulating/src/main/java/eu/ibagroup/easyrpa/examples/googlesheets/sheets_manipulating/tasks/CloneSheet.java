package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.GSheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Spreadsheet;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Clone Sheet")
@Slf4j
public class CloneSheet extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {

        String clonedSheetName = "Cloned Sheet";

        log.info("Clone active by default sheet for spreadsheet with id: {}", spreadsheetId);
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        GSheet activeGSheet = spreadsheet.getActiveSheet();

        log.info("Active sheet name: '{}'", activeGSheet.getName());

        GSheet clonedGSheet = spreadsheet.cloneSheet(activeGSheet.getName());
        log.info("Sheet '{}' has been cloned successfully.", clonedGSheet.getName());

        log.info("Rename cloned sheet to '{}'.", clonedSheetName);
        clonedGSheet.rename(clonedSheetName);

        spreadsheet.commit();
    }
}
