package eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.PivotTableParams;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Create Pivot Table")
@Slf4j
public class CreatePivotTable extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String pivotTableName = "Pivot Data";
        String sourceSheetName = "Data";

        log.info("Create pivot table for spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);

        log.info("Create pivot table '{}' using data of sheet '{}' as source.", pivotTableName, sourceSheetName);
        PivotTableParams ptParams = PivotTableParams.getFor(sourceSheetName).filters().columns().rows().value();
        doc.createPivotTable(pivotTableName, ptParams);

        log.info("Save changes.");
        doc.save();

        log.info("Pivot table '{}' created successfully.", pivotTableName);
    }
}
