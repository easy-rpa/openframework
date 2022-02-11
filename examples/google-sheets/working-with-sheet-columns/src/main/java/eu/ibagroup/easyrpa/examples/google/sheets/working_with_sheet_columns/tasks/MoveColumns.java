package eu.ibagroup.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.Sheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.InsertMethod;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Move Columns")
@Slf4j
public class MoveColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String columnToMoveRef = "D";
        String moveBeforeColumn = "F";

        log.info("Move columns for spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Move column '{}' before column '{}' of sheet '{}'", columnToMoveRef, moveBeforeColumn, activeSheet.getName());
        activeSheet.moveColumn(columnToMoveRef, InsertMethod.BEFORE, moveBeforeColumn);

        log.info("Spreadsheet document is edited successfully.");
    }

}
