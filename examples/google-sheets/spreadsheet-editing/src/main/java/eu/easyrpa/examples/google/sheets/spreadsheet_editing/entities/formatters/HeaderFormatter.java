package eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters;

import eu.easyrpa.examples.google.sheets.spreadsheet_editing.entities.Passenger;
import eu.easyrpa.openframework.google.sheets.Cell;
import eu.easyrpa.openframework.google.sheets.constants.BorderStyle;
import eu.easyrpa.openframework.google.sheets.function.TableFormatter;

import java.util.List;

public class HeaderFormatter implements TableFormatter<Passenger> {

    @Override
    public void format(Cell cell, String column, int recordIndex, List<Passenger> records) {
        if (recordIndex > 0 && recordIndex == (records.size() - 1)) {
            //last row
            cell.getStyle().borders(null, null, BorderStyle.SOLID_MEDIUM, null).apply();
        }
    }
}
