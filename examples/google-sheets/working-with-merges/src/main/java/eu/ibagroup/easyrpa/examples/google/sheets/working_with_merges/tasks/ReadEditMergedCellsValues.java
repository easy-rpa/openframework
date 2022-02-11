package eu.ibagroup.easyrpa.examples.google.sheets.working_with_merges.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.drive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.sheets.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@ApTaskEntry(name = "Read Edit Merged Cells Values")
@Slf4j
public class ReadEditMergedCellsValues extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_EDIT_MERGED_CELLS_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String cellRef = "L7";
        String newValue = "Value of merged cell is changed.";

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Open spreadsheet with ID: {}", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet activeSheet = doc.getActiveSheet();

        Cell cell = activeSheet.getCell(cellRef);

        CellRange mergedRegion = cell.getMergedRegion();
        log.info("Cell '{}' is a part of merged region: {}", cellRef, mergedRegion.formatAsString());

        log.info("Value of merged cell '{}': {}", cellRef, cell.getValue());

        log.info("Change value of merged cell '{}' to '{}'.", cellRef, newValue);
        cell.setValue(newValue);

        log.info("Spreadsheet document is edited successfully.");
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
