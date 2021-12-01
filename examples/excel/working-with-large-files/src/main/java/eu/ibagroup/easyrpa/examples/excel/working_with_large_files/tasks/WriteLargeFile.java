package eu.ibagroup.easyrpa.examples.excel.working_with_large_files.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.working_with_large_files.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Read List of Typed Records")
@Slf4j
public class WriteLargeFile extends ApTask {

    private static final String OUTPUT_FILE_NAME = "edit_large_file_records_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        Integer passengerId = 35500;

        log.info("Open spreadsheet document located at '{}' and edit.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile, true);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Lookup Passengers table on sheet '{}'", activeSheet.getName());
        Table<Passenger> passengersTable = activeSheet.findTable(Passenger.class, "Passenger Id", "Name");

        log.info("Lookup record by specific condition in the table");
        Passenger record = passengersTable.findRecord(r -> passengerId.equals(r.getPassengerId()));

        if (record == null) {
            log.warn("Record not found");
            return;
        }

        log.info("Record with PassengerId '{}' found. Current value of Age: {}", passengerId, record.getAge());

        log.info("Edit Age of the record.");
        record.setAge(1100);

        log.info("Update corresponding record on sheet.");
        passengersTable.updateRecord(record);

//        log.info("Insert new record into the table.");
//        passengersTable.insertRecord(InsertMethod.AFTER, 399000, getNewRecord());

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }

    private Passenger getNewRecord() {
        Passenger newRecord = new Passenger();
        newRecord.setPassengerId(999999);
        newRecord.setName("Custom record");
        newRecord.setAge(200);
        newRecord.setSurvived(true);
        newRecord.setPClass(1);
        newRecord.setTicket("TEST");
        return newRecord;
    }
}
