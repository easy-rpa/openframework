package eu.ibagroup.easyrpa.examples.excel.sheet_data_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.sheet_data_reading.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@ApTaskEntry(name = "Read List of Typed Records")
@Slf4j
public class ReadListOfTypedRecords extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        List<String> keywordsToLocalizeTable = Arrays.asList("Passenger Id", "Name");
        String topLeftCellOfTableRef = "B2";

        log.info("Read list of typed records from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Get list of records that contains in the table on sheet '{}'.", activeSheet.getName());
        List<Passenger> records = activeSheet.getRecords(topLeftCellOfTableRef);

        log.info("Fetched records:");
        records.forEach(r -> log.info("{}", r));

        log.info("Get the same list of records using keywords '{}' to localize the table header.", keywordsToLocalizeTable);
        records = activeSheet.getRecords(keywordsToLocalizeTable);

        log.info("Amount of fetched records: {}", records.size());
    }
}
