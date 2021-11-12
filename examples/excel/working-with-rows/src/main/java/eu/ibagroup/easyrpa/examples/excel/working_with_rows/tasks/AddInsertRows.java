package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Row;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.constants.InsertMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Add Insert Rows")
@Slf4j
public class AddInsertRows extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.spreadsheet.file")
    private String outputSpreadsheetFile;

    @Override
    public void execute() {
        int insertBeforeIndex = 5;

        Row row = new Row();
        row.addValues(getSampleData());

        log.info("Add/Insert rows to spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Add row to the end of sheet '{}'", activeSheet.getName());
        activeSheet.addRow(row);

        log.info("Insert row before index '{}' of sheet '{}'", insertBeforeIndex, activeSheet.getName());
        activeSheet.insertRow(insertBeforeIndex, InsertMethod.BEFORE, row);

        log.info("Save changes to '{}'.", outputSpreadsheetFile);
        doc.saveAs(outputSpreadsheetFile);

        log.info("Spreadsheet document is saved successfully.");
    }

    public List<String> getSampleData() {
        //TODO Implement this
        return new ArrayList<>();
    }
}
