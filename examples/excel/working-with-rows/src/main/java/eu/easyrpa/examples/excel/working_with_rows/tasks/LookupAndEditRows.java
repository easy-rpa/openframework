package eu.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Row;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Lookup and Edit Rows")
@Slf4j
public class LookupAndEditRows extends ApTask {

    private static final String OUTPUT_FILE_NAME = "rows_edit_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String passengerName = "Moran, Mr. James";

        log.info("Lookup and edit row for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet sheet = doc.selectSheet("Editing");

        log.info("Lookup row that contains value '{}' in cells.", passengerName);
        Row row = sheet.findRow(passengerName);

        if (row != null) {
            log.info("Edit cell at column 'F'.");
            row.setValue("F", 99999);

            String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
            log.info("Save changes to '{}'.", outputFilePath);
            doc.saveAs(outputFilePath);

            log.info("Spreadsheet document is saved successfully.");

        } else {
            log.warn("Row that contains value '{}' in cells has not found.", passengerName);
        }
    }
}
