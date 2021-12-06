package eu.ibagroup.easyrpa.examples.excel.working_with_large_files.entities.formatters;

import eu.ibagroup.easyrpa.examples.excel.working_with_large_files.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.function.ColumnFormatter;
import eu.ibagroup.easyrpa.openframework.excel.style.ExcelColors;

public class AgeFormatter implements ColumnFormatter<Passenger> {

    @Override
    public void format(Cell cell, String column, Passenger record) {
        if (record != null) {
            if (record.getAge() > 100) {
                cell.getStyle().background(ExcelColors.LIGHT_BLUE.get()).apply();
            }
        }
    }
}
