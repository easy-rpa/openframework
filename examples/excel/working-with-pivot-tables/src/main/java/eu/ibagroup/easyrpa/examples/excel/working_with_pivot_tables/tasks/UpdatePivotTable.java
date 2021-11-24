package eu.ibagroup.easyrpa.examples.excel.working_with_pivot_tables.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.*;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Update Pivot Table")
@Slf4j
public class UpdatePivotTable extends ApTask {

    @Input("excel_file_path")
    private String excelFilePath;

    @Override
    public void execute() {
        String sourceSheetName = "Passengers";
        String pivotTablesSheetName = "Pivot Tables";

        log.info("Change source data and update pivot table for excel document located at: {}", excelFilePath);
        ExcelDocument doc = new ExcelDocument(excelFilePath);
        log.info("Select source data sheet '{}'", sourceSheetName);
        Sheet srcSheet = doc.selectSheet(sourceSheetName);

        log.info("Change source data by removing one row");
        srcSheet.removeRow(5);
        Table<Object> sourceTable = srcSheet.findTable(Object.class, "Passenger Id");
        sourceTable.trimLeadingAndTrailingSpaces();

        log.info("Update pivot table and get value of Grand Total");
        Sheet ptSheet = doc.selectSheet(pivotTablesSheetName);
        ptSheet.updatePivotTable(PivotTableParams.create("Pivot Table 1").source(sourceTable));

        Cell totalLabelCell = ptSheet.findCell("Grand Total");
        if (totalLabelCell != null) {
            Cell totalCell = ptSheet.getCell(totalLabelCell.getRowIndex(), totalLabelCell.getColumnIndex() + 1);
            log.info("Value of Grand Total: {}", totalCell.getValue());
        } else {
            log.warn("Cell Grand Total has not found");
        }

        log.info("Save changes");
        doc.save();

        log.info("Excel document is saved successfully.");
    }
}
