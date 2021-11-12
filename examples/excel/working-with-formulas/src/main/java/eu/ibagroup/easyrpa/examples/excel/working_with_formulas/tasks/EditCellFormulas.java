package eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Edit Cell Formulas")
@Slf4j
public class EditCellFormulas extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {

        String cellWithFormulaRef = "C3";
        String newCellFormula = "A3 * B3 + 10";

        log.info("Edit cells with formulas of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Edit formula for cell '{}' of sheet '{}'.", cellWithFormulaRef, activeSheet.getName());
        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        if (!cell.hasFormula()) {
            log.info("Cell '{}' does not have formula at all.", cellWithFormulaRef);
        }
        cell.setFormula(newCellFormula);

        log.info("Save changes in for the document.");
        doc.save();

        log.info("Formula for cell '{}' has been changed successfully.", cellWithFormulaRef);
    }
}
