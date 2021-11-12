package eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "List Existing Sheets")
@Slf4j
public class ListExistingSheets extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        log.info("List all sheets from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);

        List<String> sheetNames = doc.getSheetNames();
        log.info("Spreadsheet document has following sheets: {}", sheetNames);
    }
}
