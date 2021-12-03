package eu.ibagroup.easyrpa.examples.excel.working_with_merged_cells.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.File;

@ApTaskEntry(name = "Merge Unmerge Cells")
@Slf4j
public class MergeUnmergeCells extends ApTask {

    private static final String OUTPUT_FILE_NAME = "merge_unmerge_cells_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String cellRegionToMerge = "B2:D2";
        String cellRegionToUnMerge = "I6:L11";

        log.info("Open spreadsheet document located at '{}'.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Merge cells '{}' on sheet '{}'.", cellRegionToMerge, activeSheet.getName());
        Cell topLeftCellOfMergedRegion = activeSheet.mergeCells(cellRegionToMerge);
        topLeftCellOfMergedRegion.getStyle().hAlign(HorizontalAlignment.CENTER).vAlign(VerticalAlignment.CENTER).apply();

        log.info("Unmerge cells '{}'.", cellRegionToUnMerge);
        activeSheet.unmergeCells(cellRegionToUnMerge);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
}
