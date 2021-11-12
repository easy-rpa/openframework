package eu.ibagroup.easyrpa.examples.excel.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Move Sheet")
@Slf4j
public class MoveSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Move sheet active sheet to the and of all sheets for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        int newPosition = doc.getSheetNames().size() - 1;
        doc.moveSheet(activeSheet.getName(), newPosition);

        log.info("Sheet '{}' has been moved to '{}' position successfully.", activeSheet.getName(), newPosition);
    }
}
