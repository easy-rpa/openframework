package eu.easyrpa.examples.excel.excel_file_editing.entities.formatters;

import eu.easyrpa.examples.excel.excel_file_editing.entities.Passenger;
import eu.easyrpa.openframework.excel.Cell;
import eu.easyrpa.openframework.excel.style.ExcelColor;
import eu.easyrpa.openframework.excel.function.ColumnFormatter;

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
