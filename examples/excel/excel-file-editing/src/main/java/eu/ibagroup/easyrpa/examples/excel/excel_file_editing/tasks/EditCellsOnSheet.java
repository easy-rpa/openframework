package eu.ibagroup.easyrpa.examples.excel.excel_file_editing.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Edit Cells on Sheet")
@Slf4j
public class EditCellsOnSheet extends ApTask {

    private static final String OUTPUT_FILE_NAME = "edit_data_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        log.info("Open spreadsheet document located at '{}' and edit.", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet dataSheet = doc.selectSheet("Data");

        log.info("Edit cells on sheet '{}'", dataSheet.getName());
        dataSheet.setValue("B2", "Some text");
        dataSheet.setValue("C3", 120);
        dataSheet.setValue("D4", DateTime.now());

        log.info("Put range of sample data on sheet '{}'", dataSheet.getName());
        dataSheet.putRange("D11", getSampleData());

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }

    private List<List<String>> getSampleData() {
        List<List<String>> data = new ArrayList<>();
        int columnsCount = 20;
        int rowsCount = 100;
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
