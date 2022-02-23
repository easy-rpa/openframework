package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.google.drive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.Sheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApTaskEntry(name = "Edit Cells on Sheet")
@Slf4j
public class EditCellsOnSheet extends ApTask {

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Input
    private String resultSpreadsheetFileId;

    @Override
    public void execute() {

        log.info("Open spreadsheet file with ID '{}'.", resultSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(resultSpreadsheetFileId);
        Sheet dataSheet = doc.selectSheet("Data");

        //Calling of each method like 'setValue', 'putRange', etc. performs separate request to Google Server that has
        // quotes for this. Using 'withOneBatch' method it combines all requests into one.
        doc.withOneBatch(d -> {
            log.info("Edit cells on sheet '{}'", dataSheet.getName());
            dataSheet.setValue("B2", "Some text");
            dataSheet.setValue("C3", 120);
            dataSheet.setValue("C4", 1253.545);
            dataSheet.setValue("D4", LocalDateTime.now());
            dataSheet.setValue("D5", LocalDate.now());
            dataSheet.setValue("D6", new Date());
            dataSheet.setValue("E3", true);

            log.info("Put range of sample data on sheet '{}'", dataSheet.getName());
            dataSheet.putRange("D11", getSampleData(20, 100));
        });

        log.info("Spreadsheet document is edited successfully.");
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
