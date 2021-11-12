package eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "Delete Sheet")
@Slf4j
public class DeleteSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Delete the last sheet from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        List<String> sheetNames = doc.getSheetNames();
        String lastSheetName = sheetNames.get(sheetNames.size() - 1);

        log.info("Delete sheet with name '{}'.", lastSheetName);
        doc.removeSheet(lastSheetName);

        log.info("Sheet '{}' has been deleted successfully.", lastSheetName);
    }
}
