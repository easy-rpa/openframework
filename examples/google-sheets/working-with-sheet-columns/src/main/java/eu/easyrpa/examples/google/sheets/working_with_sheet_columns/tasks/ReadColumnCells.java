package eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks;

import eu.easyrpa.openframework.google.sheets.*;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Read Column Cells")
@Slf4j
public class ReadColumnCells extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String columnToReadRef = "D";

        log.info("Read column cells of spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Read cells of column '{}'", columnToReadRef);
        Column column = activeSheet.getColumn(columnToReadRef);
        for (Cell cell : column) {
            Object value = cell.getValue();
            log.info("Cell value at row '{}': {} ({})", cell.getRowIndex(), value, (value != null ? value.getClass() : "null"));
        }
    }
}
