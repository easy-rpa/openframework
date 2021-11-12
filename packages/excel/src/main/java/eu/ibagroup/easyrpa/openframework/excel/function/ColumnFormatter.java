package eu.ibagroup.easyrpa.openframework.excel.function;

import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface ColumnFormatter<T> {

    /**
     * Formats specific cells based on some specific logic.
     *
     * @param cell - instance of cell to which formatting is applied.
     * @param column - name of column to which the cell belongs.
     * @param record - instance of current record. If value is null then cell is a header cell.
     */
    void format(Cell cell, String column, T record);
}
