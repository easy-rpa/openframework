package eu.easyrpa.examples.excel.sheet_data_reading.tasks;

import eu.easyrpa.examples.excel.sheet_data_reading.entities.Passenger;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import eu.easyrpa.openframework.excel.Table;
import eu.easyrpa.openframework.excel.constants.MatchMethod;
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

        log.info("List records that contains in the table on sheet '{}'.", activeSheet.getName());
        Table<Passenger> passengersTable1 = activeSheet.getTable(topLeftCellOfTableRef, Passenger.class);
        for (Passenger p : passengersTable1) {
            log.info("{}", p);
        }

        log.info("Get the same list of records using keywords '{}' to localize the table header.", (Object) keywordsToLocalizeTable);
        Table<Passenger> passengersTable2 = activeSheet.findTable(Passenger.class, MatchMethod.EXACT, keywordsToLocalizeTable);
        List<Passenger> records = passengersTable2.getRecords();

        log.info("Amount of fetched records: {}", records.size());
    }
}
