package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Delete Columns")
@Slf4j
public class DeleteColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String columnToDeleteRef = "C";

        log.info("Delete column of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Delete column '{}' from sheet '{}'", columnToDeleteRef, activeSheet.getName());
        activeSheet.removeColumn(columnToDeleteRef);

        log.info("Column '{}' has been deleted successfully.", columnToDeleteRef);
    }
}
