package eu.ibagroup.easyrpa.examples.excel.working_with_large_files.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.working_with_large_files.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.Table;
import eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "Read List of Typed Records")
@Slf4j
public class WriteLargeFile extends ApTask {

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
