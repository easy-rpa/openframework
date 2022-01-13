package eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Read Sheet Data")
@Slf4j
public class ReadListOfTypedRecords extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Configuration(value = "sheet.name")
    private String sheetName;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {

        String[] keywordsToLocalizeTable = new String[]{"Passenger Id", "Name"};
        String topLeftCellOfTableRef = "A15";

        log.info("Read list of typed records from spreadsheet document with id: {}", spreadsheetId);
        SpreadsheetDocument doc = service.getSpreadsheet(spreadsheetId);
        Sheet sheet = doc.getSheet(sheetName);

        log.info("List records that contains in the table on sheet '{}'.", sheet.getName());
        //todo getTable not implemented
        //Table<Passenger> passengersTable1  = sheet.getTable(topLeftCellOfTableRef, Passenger.class);
//        for (Passenger p : passengersTable1) {
//            log.info("{}", p);
//        }

        log.info("Get the same list of records using keywords '{}' to localize the table header.", (Object) keywordsToLocalizeTable);
        //todo findTable not implemented
//        Table<Passenger> passengersTable2 = sheet.findTable(Passenger.class, MatchMethod.EXACT, keywordsToLocalizeTable);
//        List<Passenger> records = passengersTable2.getRecords();

//        log.info("Amount of fetched records: {}", records.size());
    }
}
