package eu.ibagroup.easyrpa.examples.excel.working_with_columns.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Delete Columns")
@Slf4j
public class DeleteColumns extends ApTask {

    private static final String OUTPUT_FILE_NAME = "column_delete_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String columnToDeleteRef = "D";

        log.info("Delete column of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Delete column '{}' from sheet '{}'", columnToDeleteRef, activeSheet.getName());
        activeSheet.removeColumn(columnToDeleteRef);

        log.info("Column '{}' has been deleted successfully.", columnToDeleteRef);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
}
