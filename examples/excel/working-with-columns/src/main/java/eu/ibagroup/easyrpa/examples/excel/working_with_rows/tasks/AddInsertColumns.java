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

@ApTaskEntry(name = "Add Insert Columns")
@Slf4j
public class AddInsertColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String insertAfterColumn = "C";

        Column column = new Column();
        column.insertValues(3, InsertMethod.AFTER, getSampleData());

        log.info("Add/Insert columns to spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Add column to the end of sheet '{}'", activeSheet.getName());
        activeSheet.addColumn(column);

        log.info("Insert column after column '{}' of sheet '{}'", insertAfterColumn, activeSheet.getName());
        activeSheet.insertColumn(insertAfterColumn, InsertMethod.AFTER, column);
    }

    public List<String> getSampleData() {
        //TODO Implement this
        return new ArrayList<>();
    }

}
