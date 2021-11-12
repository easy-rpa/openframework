package eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Row;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Read Pivot Table")
@Slf4j
public class ReadPivotTable extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String pivotTableName = "Pivot Data";

        log.info("Read data of pivot table from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);

        log.info("Select sheet with pivot table '{}'", pivotTableName);
        Sheet ptSheet = doc.selectSheet(pivotTableName);

        log.info("Find total row");
        Row totalRow = ptSheet.findRow("Total");

        Object value = totalRow.getValue("B");
        log.info("Value at column 'B': {}", value);
    }
}
