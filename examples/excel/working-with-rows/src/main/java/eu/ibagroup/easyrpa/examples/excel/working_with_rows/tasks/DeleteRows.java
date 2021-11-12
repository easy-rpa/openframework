package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Row;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Delete Rows")
@Slf4j
public class DeleteRows extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.spreadsheet.file")
    private String outputSpreadsheetFile;

    @Override
    public void execute() {
        int rowIndex = 5;
        String lookupValue = "Moran, Mr. James";

        log.info("Delete row of spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Delete row with index '{}' from sheet '{}'", rowIndex, activeSheet.getName());
        activeSheet.removeRow(rowIndex);

        log.info("Delete row that contains value '{}' in cells.", lookupValue);
        Row rowToDelete = activeSheet.findRow(lookupValue);
        activeSheet.removeRow(rowToDelete);

        log.info("Save changes to '{}'.", outputSpreadsheetFile);
        doc.saveAs(outputSpreadsheetFile);

        log.info("Rows have been deleted successfully.");
    }
}
