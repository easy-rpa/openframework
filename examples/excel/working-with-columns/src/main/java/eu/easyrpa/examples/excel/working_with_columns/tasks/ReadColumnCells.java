package eu.easyrpa.examples.excel.working_with_columns.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.Cell;
import eu.easyrpa.openframework.excel.Column;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Read Column Cells")
@Slf4j
public class ReadColumnCells extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String columnToReadRef = "D";

        log.info("Read column cells of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Read cells of column '{}'", columnToReadRef);
        Column column = activeSheet.getColumn(columnToReadRef);
        for (Cell cell : column) {
            Object value = cell.getValue();
            log.info("Cell value at row '{}': {} ({})", cell.getRowIndex(), value, (value != null ? value.getClass() : "null"));
        }
    }
}
