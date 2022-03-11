package eu.easyrpa.examples.excel.working_with_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.Cell;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Edit Cell Formulas")
@Slf4j
public class EditCellFormulas extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Output
    private String updatedSpreadsheetFilePath;

    @Override
    public void execute() {
        String cellWithFormulaRef = "C5";
        String newFormula = "C10 + C11 + 100";

        log.info("Edit cells with formulas of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Edit formula for cell '{}' of sheet '{}'", cellWithFormulaRef, activeSheet.getName());

        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        if (!cell.hasFormula()) {
            log.info("Cell '{}' does not have formula at all.", cellWithFormulaRef);
        }
        cell.setFormula(newFormula);
        log.info("Formula for cell '{}' has been changed/set successfully.", cellWithFormulaRef);

        updatedSpreadsheetFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + FilenameUtils.getName(sourceSpreadsheetFile));
        log.info("Save changes to '{}'.", updatedSpreadsheetFilePath);
        doc.saveAs(updatedSpreadsheetFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
}
