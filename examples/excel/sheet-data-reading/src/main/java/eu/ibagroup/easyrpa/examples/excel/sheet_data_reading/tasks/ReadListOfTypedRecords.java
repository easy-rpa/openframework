package eu.ibagroup.easyrpa.examples.excel.sheet_data_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.sheet_data_reading.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "Read List of Typed Records")
@Slf4j
public class ReadListOfTypedRecords extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String[] keywordsToLocalizeTable = new String[]{"Passenger Id", "Name"};
        String topLeftCellOfTableRef = "C3";

        log.info("Read list of typed records from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Get list of records that contains in the table on sheet '{}'.", activeSheet.getName());
        List<Passenger> records = activeSheet.getRecords(topLeftCellOfTableRef, Passenger.class);

        log.info("Fetched records:");
        records.forEach(r -> log.info("{}", r));

        log.info("Get the same list of records using keywords '{}' to localize the table header.", (Object) keywordsToLocalizeTable);
        records = activeSheet.getRecords(Passenger.class, MatchMethod.EXACT, keywordsToLocalizeTable);

        log.info("Amount of fetched records: {}", records.size());
    }
}
