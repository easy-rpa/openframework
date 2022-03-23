package eu.easyrpa.examples.google.sheets.working_with_sheet_rows.tasks;

import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Row;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@ApTaskEntry(name = "Delete Rows")
@Slf4j
public class DeleteRows extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_DELETE_ROWS_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        int rowIndex = 8;
        String lookupValue = "keyword1";

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Delete row of spreadsheet document located at: {}", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet sheet = doc.selectSheet("Deleting");

        log.info("Delete row with num '{}' from sheet '{}'", rowIndex + 1, sheet.getName());
        sheet.removeRow(rowIndex);

        log.info("Delete row that contains value '{}' in cells.", lookupValue);
        Row rowToDelete = sheet.findRow(lookupValue);
        if (rowToDelete != null) {
            sheet.removeRow(rowToDelete);
            log.info("Row '{}' deleted successfully.", rowToDelete.getIndex());
        } else {
            log.warn("Row with value '{}' not found.", lookupValue);
        }

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
