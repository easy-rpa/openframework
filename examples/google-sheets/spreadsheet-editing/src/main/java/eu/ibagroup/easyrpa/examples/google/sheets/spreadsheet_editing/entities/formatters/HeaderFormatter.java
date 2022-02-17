package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.entities.formatters;

import eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.google.sheets.Cell;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.BorderStyle;
import eu.ibagroup.easyrpa.openframework.google.sheets.function.TableFormatter;

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
