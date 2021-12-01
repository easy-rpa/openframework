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

@ApTaskEntry(name = "Read Large File")
@Slf4j
public class ReadLargeFile extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        int startIndex = 350000;
        int endIndex = 360000;
        int eachNth = 1000;

        log.info("Read list of records from large spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile, true);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Find table on sheet '{}'.", activeSheet.getName());
        Table<Passenger> passengersTable = activeSheet.findTable(Passenger.class, MatchMethod.EXACT, "Passenger Id");

        if (passengersTable != null) {

            log.info("Table is found. List each record with {}th PassengerId between '{}' and '{}'.", eachNth, startIndex, endIndex);
            for (int i = startIndex; i <= endIndex; i++) {
                Passenger p = passengersTable.getRecord(i);
                if (p.getPassengerId() % eachNth == 0) {
                    log.info("{}", p);
                }
            }

        } else {
            log.warn("Table is not found.");
        }
    }
}
