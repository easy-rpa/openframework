package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.Sheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Clone Sheet")
@Slf4j
public class CloneSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {

        String clonedSheetName = "Cloned Sheet";

        log.info("Clone active by default sheet for spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet name: '{}'", activeSheet.getName());

        Sheet clonedSheet = activeSheet.cloneAs(clonedSheetName);

        log.info("Sheet '{}' has been cloned successfully. Current name of cloned sheet '{}'", activeSheet.getName(), clonedSheet.getName());
    }
}
