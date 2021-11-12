package eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Evaluate Formulas with External Links")
@Slf4j
public class EvaluateFormulasWithExternalLinks extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "linked.spreadsheet.file")
    private String linkedSpreadsheetFile;

    @Override
    public void execute() {
        String cellWithFormulaRef = "D3";
        String linkedSheetName = "Data Sheet";

        log.info("Get value of cell with formula that has links to spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Link sheet '{}' of document '{}' to the source document '{}'.", linkedSheetName, linkedSpreadsheetFile, sourceSpreadsheetFile);
        ExcelDocument linkedDoc = new ExcelDocument(linkedSpreadsheetFile);
        Sheet dataSheet = linkedDoc.selectSheet(linkedSheetName);
        doc.linkExternalSheet(String.format("[%s]%s", linkedSpreadsheetFile, linkedSheetName), dataSheet);

        log.info("Get value of cell with formula '{}' the same way as for simple cell.", cellWithFormulaRef);
        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        if (cell.hasFormula()) {
            log.info("Evaluated value of cell '{}': {}", cellWithFormulaRef, cell.getValue());
        } else {
            log.info("Cell '{}' does not have formula.", cellWithFormulaRef);
        }
    }
}
