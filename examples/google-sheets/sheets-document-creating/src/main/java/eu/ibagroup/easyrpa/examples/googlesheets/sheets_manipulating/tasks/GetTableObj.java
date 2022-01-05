package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.openframework.googlesheets.Table;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Get table object from the range")
@Slf4j
public class GetTableObj extends ApTask {
    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    GoogleSheets service;

    @Override
    public void execute() throws Exception {
        SpreadsheetDocument spreadsheetDocument = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheetDocument.getActiveSheet();

        Table table = new Table(activeSheet, 14, 0, 14, 11, 905, Passenger.class);
//        List data = table.getdata();
        List records = table.getRecords();
        log.info("records size = "+records.size());

    }
}
