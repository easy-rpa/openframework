package eu.ibagroup.easyrpa.examples.excel.excel_file_editing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Row;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@ApTaskEntry(name = "Edit Records on Sheet")
@Slf4j
public class EditRecordsOnSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String[] keywordsToLocalizeTable = new String[]{"Passenger Id", "Name"};
        String passengerName = "Moran, Mr. James";

        log.info("Open spreadsheet document located at '{}' and edit.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Lookup record by specific condition on sheet '{}'", activeSheet.getName());
        Row headerRow = activeSheet.findRow(keywordsToLocalizeTable);
        if (headerRow == null) {
            log.warn("Table with column names '{}' not found.", (Object) keywordsToLocalizeTable);
            return;
        }
        Passenger record = activeSheet.findRecord(headerRow.getReference(), r -> {
            if (passengerName.equals(r.getName())) {
                return true;
            }
            return false;
        });

        if (record != null) {
            log.info("Edit Age of the record with Name '{}'.", passengerName);
            record.setAge(50);

            log.info("Update corresponding record on sheet.");
            activeSheet.updateRecords(headerRow.getReference(), Collections.singletonList(record));

            log.info("Save changes.");
            doc.save();

            log.info("Spreadsheet document is saved successfully.");

        } else {
            log.warn("Record that specifies condition has not found.");
        }
    }
}
