package eu.easyrpa.examples.google.sheets.working_with_merges.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.sheets.Cell;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.easyrpa.openframework.google.sheets.constants.HorizontalAlignment;
import eu.easyrpa.openframework.google.sheets.constants.VerticalAlignment;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@ApTaskEntry(name = "Merge Unmerge Cells")
@Slf4j
public class MergeUnmergeCells extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_MERGE_UNMERGE_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        String cellRegionToMerge = "B2:D2";
        String cellRegionToUnMerge = "I6:L11";

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Open spreadsheet with ID: {}", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Merge cells '{}' on sheet '{}'.", cellRegionToMerge, activeSheet.getName());
        Cell topLeftCellOfMergedRegion = activeSheet.mergeCells(cellRegionToMerge);
        topLeftCellOfMergedRegion.getStyle().hAlign(HorizontalAlignment.CENTER).vAlign(VerticalAlignment.MIDDLE).apply();

        log.info("Unmerge cells '{}'.", cellRegionToUnMerge);
        activeSheet.unmergeCells(cellRegionToUnMerge);

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
