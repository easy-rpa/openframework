package eu.ibagroup.easyrpa.examples.excel.working_with_columns.tasks;

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

@ApTaskEntry(name = "Add Insert Columns")
@Slf4j
public class AddInsertColumns extends ApTask {

    private static final String OUTPUT_FILE_NAME = "column_insert_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String insertAfterColumn = "C";
        String startWithRow = "C3";

        log.info("Add/Insert columns to spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Add column to the end of sheet '{}'", activeSheet.getName());
        activeSheet.addColumn(startWithRow, getSampleData(891));

        log.info("Insert column after column '{}' of sheet '{}'", insertAfterColumn, activeSheet.getName());
        activeSheet.insertColumn(InsertMethod.AFTER, insertAfterColumn, startWithRow, getSampleData(891));

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }

    private List<String> getSampleData(int size) {
        List<String> data = new ArrayList<>();
        data.add("New Column");
        for (int i = 1; i <= size; i++) {
            data.add(String.format("Value %d", i));
        }
        return data;
    }
}
