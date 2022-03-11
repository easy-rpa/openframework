package eu.easyrpa.examples.excel.working_with_pivot_tables.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.PivotTableParams;
import eu.easyrpa.openframework.excel.Sheet;
import eu.easyrpa.openframework.excel.Table;
import eu.easyrpa.openframework.excel.constants.PivotValueSumType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Create Pivot Table")
@Slf4j
public class CreatePivotTable extends ApTask {

    private static final String OUTPUT_FILE_NAME = "create_pivot_table_result.xlsx";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Output("excel_file_path")
    private String excelFilePath;

    @Override
    public void execute() {
        String sourceSheetName = "Passengers";
        String pivotTablesSheetName = "Pivot Tables";
        String pivotTablePosition = "B5";

        log.info("Create pivot table for excel document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet sheet = doc.selectSheet(sourceSheetName);

        log.info("Create new sheet '{}' and put pivot table at position '{}' using data of sheet '{}' as source.",
                pivotTablesSheetName, pivotTablePosition, sheet.getName());
        Table<Object> sourceTable = sheet.findTable(Object.class, "Passenger Id");
        sourceTable.trimLeadingAndTrailingSpaces();

        PivotTableParams ptParams = PivotTableParams.create("Pivot Table 1")
                .position(pivotTablePosition)
                .source(sourceTable)
                .filter("Survived")
                .row("Sex").row("Class")
                .value("Passengers", "Passenger Id", PivotValueSumType.COUNT);

        Sheet pivotTablesSheet = doc.createSheet(pivotTablesSheetName);
        pivotTablesSheet.addPivotTable(ptParams);

        log.info("Pivot table created successfully.");

        excelFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", excelFilePath);
        doc.saveAs(excelFilePath);

        log.info("Excel document is saved successfully.");
    }
}
