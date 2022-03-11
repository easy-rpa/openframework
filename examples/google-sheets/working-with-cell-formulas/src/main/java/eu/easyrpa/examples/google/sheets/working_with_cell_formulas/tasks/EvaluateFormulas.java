package eu.easyrpa.examples.google.sheets.working_with_cell_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.sheets.Cell;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Evaluate Formulas")
@Slf4j
public class EvaluateFormulas extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String cellWithFormulaRef = "C5";

        log.info("Get value of cell with formula from spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Get value of cell with formula '{}' the same way as for simple cell.", cellWithFormulaRef);
        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        if (cell.hasFormula()) {
            log.info("Evaluated value of cell '{}': {}; Formula: {}", cellWithFormulaRef, cell.getValue(), cell.getFormula());
        } else {
            log.info("Cell '{}' does not have formula.", cellWithFormulaRef);
        }
    }
}
