package eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Read Range of Data")
@Slf4j
public class ReadRangeOfData extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Configuration(value = "sheet.name")
    private String sheetName;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        String topLeftCellRef = "A15";
        String bottomRightCellRef = "K200";

        log.info("Read spreadsheet document with id: {}", spreadsheetId);
        GoogleSheets servicee = new GoogleSheets();
        SpreadsheetDocument doc = service.getSpreadsheet(spreadsheetId);
        Sheet sheet = doc.getSheet(sheetName);

        log.info("Get data range [ {} : {} ] of sheet '{}'.", topLeftCellRef, bottomRightCellRef, sheetName);
        List<List<Object>> data = sheet.getRange(topLeftCellRef, bottomRightCellRef);

        log.info("Fetched data:");
        data.forEach(rec -> log.info("{}", rec));

        log.info("Cells Data C5: {}", doc.selectSheet("Sheet1").getValue("C5"));
    }
}
