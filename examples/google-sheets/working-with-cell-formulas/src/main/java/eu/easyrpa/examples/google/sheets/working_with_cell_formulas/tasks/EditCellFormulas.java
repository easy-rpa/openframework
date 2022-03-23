package eu.easyrpa.examples.google.sheets.working_with_cell_formulas.tasks;

import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.sheets.Cell;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@ApTaskEntry(name = "Edit Cell Formulas")
@Slf4j
public class EditCellFormulas extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_EDIT_FORMULAS_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String cellWithFormulaRef = "C5";
        String newFormula = "C10 + C11 + 100";

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Edit cells with formulas of spreadsheet with ID: {}", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Edit formula for cell '{}' of sheet '{}'", cellWithFormulaRef, activeSheet.getName());

        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        if (!cell.hasFormula()) {
            log.info("Cell '{}' does not have formula at all.", cellWithFormulaRef);
        }

        cell.setFormula(newFormula);

        log.info("Formula for cell '{}' has been changed/set successfully.", cellWithFormulaRef);

        log.info("Current value of cell '{}': {}", cellWithFormulaRef, cell.getValue());
    }

    private GFileInfo createCopyOfSpreadsheetFile(String fileId) {
        log.info("Create copy of spreadsheet file with ID '{}'.", fileId);
        Optional<GFileInfo> spreadsheetFile = googleDrive.getFileInfo(GFileId.of(fileId));
        if (!spreadsheetFile.isPresent()) {
            log.warn("Spreadsheet file with ID '{}' not found.", fileId);
            return null;
        }
        String nameOfCopy = spreadsheetFile.get().getName() + RESULT_FILE_POSTFIX;
        Optional<GFileInfo> spreadsheetCopy = googleDrive.copy(spreadsheetFile.get(), nameOfCopy);
        if (!spreadsheetCopy.isPresent()) {
            log.warn("Creating a copy of spreadsheet file has failed by some reasons.");
            return null;
        }
        return spreadsheetCopy.get();
    }
}
