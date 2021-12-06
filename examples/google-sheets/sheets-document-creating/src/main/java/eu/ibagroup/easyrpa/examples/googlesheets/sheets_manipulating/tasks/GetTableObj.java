package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Table_bak;
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
        Table_bak table = new Table_bak(service, spreadsheetId, 15, 0, 15, 11, 32, Passenger.class);
        List data = table.getdata();
        List records = table.getRecords();
        log.info("кусщквы size = "+records.size());

    }
}
