package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Row;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Lookup and Edit Rows")
@Slf4j
public class LookupAndEditRows extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.spreadsheet.file")
    private String outputSpreadsheetFile;

    @Override
    public void execute() {
        String passengerName = "Moran, Mr. James";

        log.info("Lookup and edit row for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Lookup row that contains value '{}' in cells.", passengerName);
        Row row = activeSheet.findRow(passengerName);

        if (row != null) {
            log.info("Edit cell at column 'F'.");
            row.setValue("F", 50);

            log.info("Save changes to '{}'.", outputSpreadsheetFile);
            doc.saveAs(outputSpreadsheetFile);

            log.info("Spreadsheet document is saved successfully.");

        } else {
            log.warn("Row that contains value '{}' in cells has not found.", passengerName);
        }
    }
}
