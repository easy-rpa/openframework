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

@ApTaskEntry(name = "Delete Rows")
@Slf4j
public class DeleteRows extends ApTask {

    private static final String OUTPUT_FILE_NAME = "rows_delete_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        int rowIndex = 8;
        String lookupValue = "keyword1";

        log.info("Delete row of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet sheet = doc.selectSheet("Deleting");

        log.info("Delete row with num '{}' from sheet '{}'", rowIndex + 1, sheet.getName());
        sheet.removeRow(rowIndex);

        log.info("Delete row that contains value '{}' in cells.", lookupValue);
        Row rowToDelete = sheet.findRow(lookupValue);
        if (rowToDelete != null) {
            sheet.removeRow(rowToDelete);
            log.info("Row '{}' deleted successfully.", rowToDelete.getIndex());
        } else {
            log.warn("Row with value '{}' not found.", lookupValue);
        }

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
}
