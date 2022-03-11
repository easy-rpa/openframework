package eu.easyrpa.examples.excel.excel_sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Copy Sheet between Spreadsheets")
@Slf4j
public class CopySheetBetweenSpreadsheets extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "source.sheet.name")
    private String sourceSheetName;

    @Configuration(value = "target.spreadsheet.file")
    private String targetSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        log.info("Copy sheet '{}' from '{}' to '{}'", sourceSheetName, sourceSpreadsheetFile, targetSpreadsheetFile);
        ExcelDocument src = new ExcelDocument(sourceSpreadsheetFile);
        ExcelDocument target = new ExcelDocument(targetSpreadsheetFile);

        Sheet targetSheet = target.createSheet(sourceSheetName);
        src.selectSheet(sourceSheetName).copy(targetSheet);
        log.info("Sheet '{}' has been copied successfully.", sourceSheetName);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + FilenameUtils.getName(targetSpreadsheetFile));
        log.info("Save changed excel document to '{}'.", outputFilePath);
        target.selectSheet(0);
        target.saveAs(outputFilePath);

        log.info("Excel document is saved successfully.");
    }
}
