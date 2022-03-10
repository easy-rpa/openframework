package eu.ibagroup.easyrpa.openframework.excel.function;

import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelTable;

import java.util.List;

/**
 * Allows to perform formatting for cells of table on the sheet. Formatting means modification of
 * cell value and style based on some specific logic.
 *
 * @param <T> class of table records
 * @see ExcelTable#formatter()
 */
@FunctionalInterface
public interface TableFormatter<T> {

    /**
     * Formats cells based on some specific logic.
     *
     * @param cell        the cell to which formatting is applied.
     * @param column      name of column to which the cell belongs.
     * @param recordIndex index of current table record in the list.Equals <b><code>-1</code></b> if the cell is a <b>header cell</b>.
     * @param records     whole list of table records. Equals <b><code>null</code></b> if the cell is a <b>header cell</b>.
     */
    void format(Cell cell, String column, int recordIndex, List<T> records);
}
