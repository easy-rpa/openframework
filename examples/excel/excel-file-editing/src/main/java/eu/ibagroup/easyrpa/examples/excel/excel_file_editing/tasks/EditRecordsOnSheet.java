package eu.ibagroup.easyrpa.examples.excel.excel_file_editing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@ApTaskEntry(name = "Edit Records on Sheet")
@Slf4j
public class EditRecordsOnSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        List<String> keywordsToLocalizeTable = Arrays.asList("Passenger Id", "Name");
        String passengerName = "Moran, Mr. James";

        log.info("Open spreadsheet document located at '{}' and edit.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Lookup record by specific condition on sheet '{}'", activeSheet.getName());
        Passenger record = activeSheet.findRecord(keywordsToLocalizeTable, r -> {
            if (passengerName.equals(r.getName())) {
                return true;
            }
            return false;
        });

        if (record != null) {
            log.info("Edit 'Age' of the record.");
            record.setAge(50);

            log.info("Update corresponding row on sheet.");
            activeSheet.updateRecord(keywordsToLocalizeTable, record);

            log.info("Save changes.");
            doc.save();

            log.info("Spreadsheet document is saved successfully.");

        } else {
            log.warn("Row that specifies condition has not found.");
        }
    }
}
