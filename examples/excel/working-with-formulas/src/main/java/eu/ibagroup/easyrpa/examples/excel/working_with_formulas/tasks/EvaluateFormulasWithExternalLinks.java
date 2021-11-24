package eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

@ApTaskEntry(name = "Evaluate Formulas with External Links")
@Slf4j
public class EvaluateFormulasWithExternalLinks extends ApTask {

    @Input
    private String updatedSpreadsheetFilePath;

    @Input
    private String sharedSpreadsheetFilePath;

    @Override
    public void execute() {
        String targetCellRef = "C7";
        String newFormulaWithExternalLink = String.format("C5 + [%s]Data!B6", FilenameUtils.getName(sharedSpreadsheetFilePath));

        log.info("Get value of cell with formula that has links to spreadsheet document located at: {}", updatedSpreadsheetFilePath);
        ExcelDocument doc = new ExcelDocument(updatedSpreadsheetFilePath);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Link document '{}' to the source document '{}'.", sharedSpreadsheetFilePath, updatedSpreadsheetFilePath);
        doc.linkExternalDocument(new ExcelDocument(sharedSpreadsheetFilePath));

        log.info("Set formula '{}' to cell '{}' and get it's value.", newFormulaWithExternalLink, targetCellRef);
        Cell cell = activeSheet.getCell(targetCellRef);
        cell.setFormula(newFormulaWithExternalLink);
        log.info("Evaluated value of cell '{}': {}", targetCellRef, cell.getValue());

        log.info("Save changes");
        doc.save();

        log.info("Spreadsheet document is saved successfully.");
    }
}
