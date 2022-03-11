package eu.easyrpa.examples.google.sheets.spreadsheet_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Read Range of Data")
@Slf4j
public class ReadRangeOfData extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String topLeftCellRef = "C4";
        String bottomRightCellRef = "M200";

        log.info("Read range of data from spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Get data range [ {} : {} ] of sheet '{}'.", topLeftCellRef, bottomRightCellRef, activeSheet.getName());
        List<List<Object>> data = activeSheet.getRange(topLeftCellRef, bottomRightCellRef);

        log.info("Fetched data:");
        data.forEach(rec -> log.info("{}", rec));

        log.info("Cells Data D8: {}", doc.selectSheet("Cells Data").getValue("D8"));
    }
}
