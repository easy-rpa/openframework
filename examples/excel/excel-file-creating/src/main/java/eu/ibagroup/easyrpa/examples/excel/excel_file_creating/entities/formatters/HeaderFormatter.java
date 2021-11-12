package eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities.formatters;

import eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.function.TableFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.List;

public class HeaderFormatter implements TableFormatter<Passenger> {

    private CellStyle headerCellStyle;

    @Override
    public void format(Cell cell, String column, int recordIndex, List<Passenger> records) {
        cell.setCellStyle(getHeaderCellStyle(cell));
    }

    public CellStyle getHeaderCellStyle(Cell cell) {
        if (headerCellStyle == null) {
            headerCellStyle = cell.getCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            Font font = cell.getSheet().getWorkbook().createFont();
            font.setBold(true);
            headerCellStyle.setFont(font);
        }
        return headerCellStyle;
    }
}
