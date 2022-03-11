package eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.easyrpa.openframework.google.sheets.constants.InsertMethod;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Add Insert Columns")
@Slf4j
public class AddInsertColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String insertAfterColumn = "C";
        String startWithRow = "C3";

        log.info("Add/Insert columns to spreadsheet document located at: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Add column to the end of sheet '{}'", activeSheet.getName());
        activeSheet.addColumn(startWithRow, getSampleData(891));

        log.info("Insert column after column '{}' of sheet '{}'", insertAfterColumn, activeSheet.getName());
        activeSheet.insertColumn(InsertMethod.AFTER, insertAfterColumn, startWithRow, getSampleData(891));

        log.info("Spreadsheet document is edited successfully.");
    }

    private List<String> getSampleData(int size) {
        List<String> data = new ArrayList<>();
        data.add("New Column");
        for (int i = 1; i <= size; i++) {
            data.add(String.format("Value %d", i));
        }
        return data;
    }
}
