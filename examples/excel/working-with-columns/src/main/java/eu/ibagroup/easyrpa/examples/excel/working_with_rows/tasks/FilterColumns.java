package eu.ibagroup.easyrpa.examples.excel.working_with_rows.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApTaskEntry(name = "Filter Columns")
@Slf4j
public class FilterColumns extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String columnToFilterRef = "A";
        List<Object> valuesToFilter = Arrays.asList("Bob", "Tom");

        log.info("Filter column for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Filter column '{}' yo show only values: {} ", columnToFilterRef, valuesToFilter);
        activeSheet.filterColumn(columnToFilterRef, valuesToFilter, MatchMethod.EXACT);
    }

    public List<String> getSampleData() {
        return new ArrayList<>();
    }

}
