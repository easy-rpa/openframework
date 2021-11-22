package eu.ibagroup.easyrpa.openframework.excel.function;

import eu.ibagroup.easyrpa.openframework.excel.Cell;

import java.util.List;

@FunctionalInterface
public interface TableFormatter<T> {

    /**
     * Formats specific cells based on some specific logic.
     *
     * @param cell        - instance of cell to which formatting is applied.
     * @param column      - name of column to which the cell belongs.
     * @param recordIndex - index of current record in the list. If value is <code>-1</code> then cell is a header cell.
     * @param records     - list of adding/inserting/updating records. Contains only thous records that passed into
     *                    add/insert/update records function. It does not includes records that have been
     *                    added/inserted/updated before.
     */
    void format(Cell cell, String column, int recordIndex, List<T> records);
}
