package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.constants.InsertMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Insert Rows")
@Slf4j
public class InsertRows extends ApTask {

    private static final String OUTPUT_FILE_NAME = "rows_insert_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        int insertBeforeRow = 4;
        String insertAfterCell = "D10";

        log.info("Add/Insert rows to spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet sheet = doc.selectSheet("Inserting");

        log.info("Insert rows after cell '{}' of sheet '{}'", insertAfterCell, sheet.getName());
        sheet.insertRows(InsertMethod.AFTER, insertAfterCell, getSampleData(10, 20));

        log.info("Insert rows before row '{}' of sheet '{}'", insertBeforeRow + 1, sheet.getName());
        sheet.insertRows(InsertMethod.BEFORE, insertBeforeRow, 1, getSampleData(5, 10));

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
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
