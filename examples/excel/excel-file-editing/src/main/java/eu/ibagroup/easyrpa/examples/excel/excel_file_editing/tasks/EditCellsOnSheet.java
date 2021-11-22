package eu.ibagroup.easyrpa.examples.excel.excel_file_editing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Edit Cells on Sheet")
@Slf4j
public class EditCellsOnSheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Open spreadsheet document located at '{}' and edit.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Edit cells on sheet '{}'", activeSheet.getName());
        activeSheet.setValue("A2", "Some text");
        activeSheet.setValue("B2", 10);
        activeSheet.setValue("C2", DateTime.now());

//        log.info("Put range of sample data on sheet '{}'", activeSheet.getName());
//        activeSheet.putRange("A4", getSampleData());

        log.info("Save changes.");
        doc.save();

        log.info("Spreadsheet document is saved successfully.");
    }

    private List<List<Object>> getSampleData() {
        //TODO Implement this
        return new ArrayList<>();
    }
}
