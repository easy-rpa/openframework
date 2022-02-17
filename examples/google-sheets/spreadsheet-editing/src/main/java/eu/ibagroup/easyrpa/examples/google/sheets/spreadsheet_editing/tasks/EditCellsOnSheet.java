package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.tasks;

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
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApTaskEntry(name = "Edit Cells on Sheet")
@Slf4j
public class EditCellsOnSheet extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_EDIT_CELLS_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {

        GFileInfo sourceSpreadsheetFile = createCopyOfSpreadsheetFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Open spreadsheet file with ID '{}'.", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet dataSheet = doc.selectSheet("Data");

        //Calling of each method like 'setValue', 'putRange', etc. performs separate request to Google Server that has
        // quotes for this. Using 'withOneBatch' method it possible to combine all requests into one.
        doc.withOneBatch(d -> {
            log.info("Edit cells on sheet '{}'", dataSheet.getName());
            dataSheet.setValue("B2", "Some text");
            dataSheet.setValue("C3", 120);
            dataSheet.setValue("D4", DateTime.now());

            log.info("Put range of sample data on sheet '{}'", dataSheet.getName());
            dataSheet.putRange("D11", getSampleData(20, 100));
        });

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
