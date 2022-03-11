package eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters;

import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.Passenger;
import eu.easyrpa.openframework.google.sheets.Cell;
import eu.easyrpa.openframework.google.sheets.function.ColumnFormatter;

import java.awt.*;

public class AgeFormatter implements ColumnFormatter<Passenger> {

    @Override
    public void format(Cell cell, String column, Passenger record) {
        if (record != null) {
            if (record.getAge() > 100) {
                cell.getStyle().background(Color.CYAN).apply();
            }
        }
    }
}
