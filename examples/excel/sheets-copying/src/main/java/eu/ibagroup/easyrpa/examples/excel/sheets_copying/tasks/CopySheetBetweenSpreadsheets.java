package eu.ibagroup.easyrpa.examples.excel.sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Copy Sheet between Spreadsheets")
@Slf4j
public class CopySheetBetweenSpreadsheets extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "source.sheet.name")
    private String sourceSheetName;

    @Configuration(value = "target.spreadsheet.file")
    private String targetSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Copy sheet '{}' from '{}' to '{}'", sourceSheetName, sourceSpreadsheetFile, targetSpreadsheetFile);

        ExcelDocument src = new ExcelDocument(sourceSpreadsheetFile);
        ExcelDocument target = new ExcelDocument(targetSpreadsheetFile);

        Sheet targetSheet = target.createSheet(sourceSheetName);

        src.copySheet(sourceSheetName, targetSheet);

        log.info("Sheet '{}' has been copied successfully.", sourceSheetName);
    }
}
