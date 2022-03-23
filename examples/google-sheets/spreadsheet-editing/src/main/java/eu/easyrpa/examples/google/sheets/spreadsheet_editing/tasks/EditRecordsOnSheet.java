package eu.easyrpa.examples.google.sheets.spreadsheet_editing.tasks;

import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.Passenger;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.easyrpa.openframework.google.sheets.Table;
import eu.easyrpa.openframework.google.sheets.constants.InsertMethod;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@ApTaskEntry(name = "Edit Records on Sheet")
@Slf4j
public class EditRecordsOnSheet extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_EDIT_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Output
    private String resultSpreadsheetFileId;

    @Override
    public void execute() {
        String passengerName = "Wheadon, Mr. Edward H";

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;
        resultSpreadsheetFileId = sourceSpreadsheetFile.getId();

        log.info("Open spreadsheet file with ID '{}'.", resultSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(resultSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Lookup Passengers table on sheet '{}'", activeSheet.getName());
        Table<Passenger> passengersTable = activeSheet.findTable(Passenger.class, "Passenger Id", "Name");

        log.info("Lookup record by specific condition in the table");
        Passenger record = passengersTable.findRecord(r -> passengerName.equals(r.getName()));

        if (record == null) {
            log.warn("Record not found");
            return;
        }

        log.info("Record with Name '{}' found. Current value of Age: {}", passengerName, record.getAge());

        log.info("Edit Age of the record.");
        record.setAge(110);

        log.info("Update corresponding record on sheet.");
        passengersTable.updateRecord(record);

        log.info("Insert new record into the table.");
        passengersTable.insertRecord(InsertMethod.AFTER, 5, getNewRecord());

        log.info("Spreadsheet document is edited successfully.");
    }

    private GFileInfo createCopyOfSpreadsheetFile(String fileId) {
        log.info("Create copy of spreadsheet file with ID '{}'.", fileId);
        Optional<GFileInfo> spreadsheetFile = googleDrive.getFileInfo(GFileId.of(fileId));
        if (!spreadsheetFile.isPresent()) {
            log.warn("Spreadsheet file with ID '{}' not found.", fileId);
            return null;
        }
        String nameOfCopy = spreadsheetFile.get().getName() + RESULT_FILE_POSTFIX;
        Optional<GFileInfo> spreadsheetCopy = googleDrive.copy(spreadsheetFile.get(), nameOfCopy);
        if (!spreadsheetCopy.isPresent()) {
            log.warn("Creating a copy of spreadsheet file has failed by some reasons.");
            return null;
        }
        return spreadsheetCopy.get();
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
