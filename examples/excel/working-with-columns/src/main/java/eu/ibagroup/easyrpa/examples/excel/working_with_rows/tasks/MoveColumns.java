package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Column;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.constants.InsertMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Move Columns")
@Slf4j
public class MoveColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {

        String columnToMoveRef = "B";

        log.info("Move columns for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Move column '{}' to the end of sheet '{}'", columnToMoveRef, activeSheet.getName());
        Column lastColumn = activeSheet.getLastColumn();
        activeSheet.moveColumn(columnToMoveRef, lastColumn.getRef(), InsertMethod.AFTER);
    }

    public List<String> getSampleData() {
        return new ArrayList<>();
    }

}
