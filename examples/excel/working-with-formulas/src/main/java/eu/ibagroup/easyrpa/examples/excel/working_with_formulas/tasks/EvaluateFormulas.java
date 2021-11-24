package eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Evaluate Formulas")
@Slf4j
public class EvaluateFormulas extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String cellWithFormulaRef = "C5";

        log.info("Get value of cell with formula from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
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
