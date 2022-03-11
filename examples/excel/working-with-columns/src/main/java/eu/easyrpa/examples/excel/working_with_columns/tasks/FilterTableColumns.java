package eu.easyrpa.examples.excel.working_with_columns.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import eu.easyrpa.openframework.excel.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Filter Table Columns")
@Slf4j
public class FilterTableColumns extends ApTask {

    private static final String OUTPUT_FILE_NAME = "column_filter_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {

        log.info("Filter column for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Find table on sheet '{}' and filter it's rows", activeSheet.getName());
        Table<Object> table = activeSheet.findTable(Object.class, "Name");
        table.trimLeadingAndTrailingSpaces();
        table.filter(0, "^1..").filter(5, "1");

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }

    public List<String> getSampleData() {
        return new ArrayList<>();
    }

}
