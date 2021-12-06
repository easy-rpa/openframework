package eu.ibagroup.easyrpa.examples.excel.working_with_merged_cells.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.CellRange;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Read Edit Merged Cells Values")
@Slf4j
public class ReadEditMergedCellsValues extends ApTask {

    private static final String OUTPUT_FILE_NAME = "edit_merged_cells_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String cellRef = "L7";
        String newValue = "Value of merged cell is changed.";

        log.info("Open spreadsheet document located at '{}'.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        Cell cell = activeSheet.getCell(cellRef);

        CellRange mergedRegion = cell.getMergedRegion();
        log.info("Cell '{}' is a part of merged region: {}", cellRef, mergedRegion.formatAsString());

        log.info("Value of merged cell '{}': {}", cellRef, cell.getValue());

        log.info("Change value of merged cell '{}' to '{}'.", cellRef, newValue);
        cell.setValue(newValue);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
}
