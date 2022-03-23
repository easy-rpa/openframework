package eu.easyrpa.examples.google.sheets.working_with_sheet_rows.tasks;

import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.Sheet;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.easyrpa.openframework.google.sheets.constants.InsertMethod;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApTaskEntry(name = "Insert Rows")
@Slf4j
public class InsertRows extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_ADD_ROWS_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {
        int insertBeforeRow = 4;
        String insertAfterCell = "D10";

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Add/Insert rows to spreadsheet with ID: {}", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet sheet = doc.selectSheet("Inserting");

        log.info("Insert rows after cell '{}' of sheet '{}'", insertAfterCell, sheet.getName());
        sheet.insertRows(InsertMethod.AFTER, insertAfterCell, getSampleData(10, 20));

        log.info("Insert rows before row '{}' of sheet '{}'", insertBeforeRow + 1, sheet.getName());
        sheet.insertRows(InsertMethod.BEFORE, insertBeforeRow, 1, getSampleData(5, 10));

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

    private List<List<String>> getSampleData(int rowsCount, int columnsCount) {
        List<List<String>> data = new ArrayList<>();
        for (int i = 1; i <= rowsCount; i++) {
            List<String> dataRow = new ArrayList<>();
            for (int j = 1; j <= columnsCount; j++) {
                dataRow.add(String.format("Value %d %d", i, j));
            }
            data.add(dataRow);
        }
        return data;
    }
}
