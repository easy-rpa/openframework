package eu.ibagroup.easyrpa.examples.excel.working_with_large_files.entities.formatters;

import eu.ibagroup.easyrpa.examples.excel.working_with_large_files.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelColor;
import eu.ibagroup.easyrpa.openframework.excel.function.ColumnFormatter;

public class SurvivedFormatter implements ColumnFormatter<Passenger> {

    private ExcelColor customGreen = new ExcelColor("#7eb67f");

    @Override
    public void format(Cell cell, String column, Passenger record) {
        if (record != null) {
            if (record.isSurvived()) {
                cell.setValue("Yes");
                cell.getStyle().background(customGreen).apply();
            } else {
                cell.setValue("No");
            }
        }
    }
}
