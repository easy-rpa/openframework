package eu.ibagroup.easyrpa.examples.googlesheets.sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.GSheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Spreadsheet;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Copy Sheet between Spreadsheets")
@Slf4j
public class CopySheetBetweenSpreadsheets extends ApTask {

    @Configuration(value = "spreadsheet.id.copyFrom")
    private String spreadsheetIdFrom;

    @Configuration(value = "spreadsheet.id.copyTo")
    private String spreadsheetIdTo;

    @Configuration(value = "sheet.name")
    private String sheetName;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        log.info("Copy sheet '{}' from spreadsheet with id '{}' to '{}'", sheetName, spreadsheetIdFrom, spreadsheetIdTo);

        Spreadsheet spreadsheetFrom = service.getSpreadsheet(spreadsheetIdFrom);
        Spreadsheet spreadsheetTo = service.getSpreadsheet(spreadsheetIdTo);

        GSheet sourceGSheet = spreadsheetFrom.selectSheet(sheetName);

        spreadsheetTo.copySheet(sourceGSheet);
        log.info("Sheet '{}' has been copied successfully.", sheetName);
    }
}
