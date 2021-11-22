package eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Activate Specific Sheet")
@Slf4j
public class ActivateSpecificSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        int sheetIndex = 1;
        String sheetName = "Summary";

        log.info("Activate sheet with name '{}' for spreadsheet document located at: {}", sheetName, sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet before any action: {}", activeSheet.getName());

        log.info("Activate sheet.");
        activeSheet = doc.selectSheet(sheetName);

        log.info("Active sheet after activation: {}", activeSheet.getName());

        log.info("Active sheet using index {}.", sheetIndex);
        activeSheet = doc.selectSheet(sheetIndex);

        log.info("Active sheet after activation: {}", activeSheet.getName());
    }
}
