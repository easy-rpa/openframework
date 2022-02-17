package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.drive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.Sheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@ApTaskEntry(name = "Copy Sheet between Spreadsheets")
@Slf4j
public class CopySheetBetweenSpreadsheets extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_COPY_SHEET_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Configuration(value = "source.sheet.name")
    private String sourceSheetName;

    @Configuration(value = "target.spreadsheet.file.id")
    private String targetSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        GFileInfo targetSpreadsheetFile = createCopyOfSpreadsheetFile(targetSpreadsheetFileId);
        if (targetSpreadsheetFile == null) return;

        log.info("Copy sheet '{}' from '{}' to '{}'", sourceSheetName, sourceSpreadsheetFileId, targetSpreadsheetFileId);
        SpreadsheetDocument src = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);
        SpreadsheetDocument target = googleSheets.getSpreadsheet(targetSpreadsheetFile.getId());

        Sheet copiedSheet = src.selectSheet(sourceSheetName).copyTo(target);

        log.info("Sheet '{}' has been copied successfully. Current name of copied sheet: '{}'. Rename it back as '{}'",
                sourceSheetName, copiedSheet.getName(), sourceSheetName);

        copiedSheet.rename(sourceSheetName);
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
