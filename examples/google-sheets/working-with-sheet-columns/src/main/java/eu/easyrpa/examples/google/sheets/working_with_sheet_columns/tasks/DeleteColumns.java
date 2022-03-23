package eu.easyrpa.examples.google.sheets.working_with_sheet_columns.tasks;

import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Delete Columns")
@Slf4j
public class DeleteColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String columnToDeleteRef = "E";

        log.info("Delete columns of spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Delete last column from sheet '{}'", activeSheet.getName());
        activeSheet.removeColumn(activeSheet.getLastColumnIndex());

        log.info("Delete column '{}' from sheet '{}'", columnToDeleteRef, activeSheet.getName());
        activeSheet.removeColumn(columnToDeleteRef);

        log.info("Columns have been deleted successfully.");
    }
}
