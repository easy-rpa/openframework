package eu.easyrpa.examples.excel.excel_file_creating.entities.formatters;

import eu.easyrpa.examples.excel.excel_file_creating.entities.Passenger;
import eu.easyrpa.openframework.excel.Cell;
import eu.easyrpa.openframework.excel.function.TableFormatter;
import org.apache.poi.ss.usermodel.BorderStyle;

import java.util.List;

public class HeaderFormatter implements TableFormatter<Passenger> {

    @Override
    public void format(Cell cell, String column, int recordIndex, List<Passenger> records) {
        if (recordIndex > 0 && recordIndex == (records.size() - 1)) {
            //last row
            cell.getStyle().bottomBorder(BorderStyle.MEDIUM).apply();
        }
    }
}
