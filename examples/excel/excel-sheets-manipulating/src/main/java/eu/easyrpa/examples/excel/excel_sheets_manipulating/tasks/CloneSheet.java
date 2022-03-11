package eu.easyrpa.examples.excel.excel_sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Clone Sheet")
@Slf4j
public class CloneSheet extends ApTask {

    private static final String OUTPUT_FILE_NAME = "sheet_clone_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {

        String clonedSheetName = "Cloned Sheet";

        log.info("Clone active by default sheet for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet name: '{}'", activeSheet.getName());

        Sheet clonedSheet = activeSheet.cloneAs(clonedSheetName);
        log.info("Sheet '{}' has been cloned successfully. Current name of cloned sheet '{}'", activeSheet.getName(), clonedSheet.getName());

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");

    }
}
