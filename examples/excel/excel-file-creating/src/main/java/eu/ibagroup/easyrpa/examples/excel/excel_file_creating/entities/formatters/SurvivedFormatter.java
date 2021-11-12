package eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities.formatters;

import eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.function.ColumnFormatter;
import org.apache.poi.ss.usermodel.Cell;

public class SurvivedFormatter implements ColumnFormatter<Passenger> {

    @Override
    public void format(Cell cell, String column, Passenger record) {
        cell.setCellValue(record.isSurvived() ? "Yes" : "No");
    }
}
