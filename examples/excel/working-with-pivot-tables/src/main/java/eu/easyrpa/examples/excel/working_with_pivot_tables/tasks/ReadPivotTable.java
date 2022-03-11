package eu.easyrpa.examples.excel.working_with_pivot_tables.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.Cell;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Read Pivot Table")
@Slf4j
public class ReadPivotTable extends ApTask {

    @Input("excel_file_path")
    private String excelFilePath;

    @Override
    public void execute() {
        String pivotTablesSheetName = "Pivot Tables";

        log.info("Read data of pivot table from excel document located at: {}", excelFilePath);
        ExcelDocument doc = new ExcelDocument(excelFilePath);

        log.info("Select sheet with pivot table");
        Sheet ptSheet = doc.selectSheet(pivotTablesSheetName);

        log.info("Find Grand Total cell");
        Cell totalLabelCell = ptSheet.findCell("Grand Total");
        if (totalLabelCell != null) {
            Cell totalCell = ptSheet.getCell(totalLabelCell.getRowIndex(), totalLabelCell.getColumnIndex() + 1);
            log.info("Value of Grand Total: {}", totalCell.getValue());
        } else {
            log.warn("Cell Grand Total has not found");
        }
    }
}
