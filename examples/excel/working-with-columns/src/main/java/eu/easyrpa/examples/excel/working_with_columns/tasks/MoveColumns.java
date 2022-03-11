package eu.easyrpa.examples.excel.working_with_columns.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import eu.easyrpa.openframework.excel.constants.InsertMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Move Columns")
@Slf4j
public class MoveColumns extends ApTask {

    private static final String OUTPUT_FILE_NAME = "column_move_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String columnToMoveRef = "D";
        String moveBeforeColumn = "F";

        log.info("Move columns for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Move column '{}' before column '{}' of sheet '{}'", columnToMoveRef, moveBeforeColumn, activeSheet.getName());
        activeSheet.moveColumn(columnToMoveRef, InsertMethod.BEFORE, moveBeforeColumn);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }

}
