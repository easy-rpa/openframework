package eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters;

import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.Passenger;
import eu.easyrpa.openframework.google.sheets.Cell;
import eu.easyrpa.openframework.google.sheets.function.ColumnFormatter;

import java.awt.*;

public class SurvivedFormatter implements ColumnFormatter<Passenger> {

    private Color customGreen = Color.decode("#7eb67f");

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
