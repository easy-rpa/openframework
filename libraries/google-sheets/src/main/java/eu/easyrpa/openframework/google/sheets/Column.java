package eu.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.RowData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents specific column of Spreadsheet document and provides functionality to work with it.
 */
public class Column implements Iterable<Cell> {

    /**
     * Reference to parent sheet.
     */
    private Sheet parent;

    /**
     * Index of this column.
     */
    private int columnIndex;

    /**
     * Creates a new instance of column.
     *
     * @param parent      reference to parent sheet.
     * @param columnIndex 0-based index of this column.
     */
    protected Column(Sheet parent, int columnIndex) {
        this.parent = parent;
        this.columnIndex = columnIndex;
    }

    /**
     * Gets parent Spreadsheet document.
     *
     * @return parent Spreadsheet document.
     */
    public SpreadsheetDocument getDocument() {
        return parent.getDocument();
    }

    /**
     * Gets parent sheet.
     *
     * @return parent sheet.
     */
    public Sheet getSheet() {
        return parent;
    }

    /**
     * Gets index of this column.
     *
     * @return 0-based index of this column.
     */
    public int getIndex() {
        return columnIndex;
    }

    /**
     * Gets reference to this column.
     * <p>
     * Column reference is a reference to its cell with row index 0.
     *
     * @return this column reference.
     * @see CellRef
     */
    public CellRef getReference() {
        return new CellRef(0, columnIndex);
    }

    /**
     * Gets the value of this column cell by given cell reference.
     *
     * @param cellRef reference string to necessary cell. E.g. "A23".
     * @return value of corresponding cell or {@code null} if nothing is found. The actual class of value
     * is depend on cell type. Can be returned {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue(String cellRef) {
        return getValue(new CellRef(cellRef).getRow(), Object.class);
    }

    /**
     * Gets the value of this column cell by given cell reference and converts it to the type
     * specified by {@code valueType}.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell values to corresponding type or {@code null} if the conversion
     * fails.
     * <p>
     * For other types it performs simple type casting of cell values to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param cellRef   reference string to necessary cell. E.g. "A23".
     * @param valueType class instance of returning value.
     * @param <T>       type of returning value. Defined by value of {@code valueType}.
     * @return value of corresponding cell or {@code null} if nothing is found. The class of return
     * value is defined by {@code valueType}. If the actual class of cell value is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> T getValue(String cellRef, Class<T> valueType) {
        return getValue(new CellRef(cellRef).getRow(), valueType);
    }

    /**
     * Gets the value of this column cell by given row index.
     *
     * @param rowIndex 0-based row index of the cell.
     * @return value of corresponding cell or {@code null} if nothing is found. The actual class of value
     * is depend on cell type. Can be returned {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue(int rowIndex) {
        return getValue(rowIndex, Object.class);
    }

    /**
     * Gets the value of this column cell by given row index. The return value is automatically
     * converted to the type specified by {@code valueType}.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell values to corresponding type or {@code null} if the conversion
     * fails.
     * <p>
     * For other types it performs simple type casting of cell values to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param rowIndex  0-based row index of the cell.
     * @param valueType class instance of returning value.
     * @param <T>       type of returning value. Defined by value of {@code valueType}.
     * @return value of corresponding cell or {@code null} if nothing is found. The class of return
     * value is defined by {@code valueType}. If the actual class of cell value is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> T getValue(int rowIndex, Class<T> valueType) {
        Cell cell = getCell(rowIndex);
        return cell != null ? cell.getValue(valueType) : null;
    }

    /**
     * Sets the value of this column cell by given cell reference.
     *
     * @param cellRef reference string to necessary cell. E.g. "A23".
     * @param value   the value to set.
     */
    public void setValue(String cellRef, Object value) {
        setValue(new CellRef(cellRef).getRow(), value);
    }

    /**
     * Sets the value of this column cell by given row index.
     *
     * @param rowIndex 0-based row index of the cell.
     * @param value    the value to set.
     */
    public void setValue(int rowIndex, Object value) {
        final int rowsAmountToAppend = rowIndex + 1 - parent.getMaxRowsCount();
        getDocument().batchUpdate(r -> {
            if (rowsAmountToAppend > 0) {
                parent.appendRowsMetadata(rowsAmountToAppend);
                r.addAppendRowsRequest(rowsAmountToAppend, parent.getId());
            }
            Cell cell = getCell(rowIndex);
            if (cell == null) {
                cell = createCell(rowIndex);
            }
            cell.setValue(value);
        });
    }

    /**
     * Gets values of all column cells.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this column.
     *
     * @return list of values of this column cells. Returns empty list if column is empty. The actual class of values
     * in list depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     * @see #getRange(int, int, Class)
     * @see #getFirstRowIndex()
     * @see #getLastRowIndex()
     */
    public List<Object> getValues() {
        return getValues(Object.class);
    }

    /**
     * Gets values of all column cells.
     * <p>
     * Returning values are automatically converted to the type specified by {@code valueType}.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this column.
     *
     * @param valueType class instance of returning values.
     * @param <T>       type of returning values. Defined by value of {@code valueType}.
     * @return list of values of this column cells. Returns empty list if column is empty. The class of returning
     * values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @see #getRange(int, int, Class)
     * @see #getFirstRowIndex()
     * @see #getLastRowIndex()
     */
    public <T> List<T> getValues(Class<T> valueType) {
        return getRange(getFirstRowIndex(), getLastRowIndex(), valueType);
    }

    /**
     * Sets given values to cells range of this column which starts from cell with row index 0.
     * <p>
     * The end cell of the range is defined by size of given values.
     * <p>
     * It's an equivalent to <code>putRange(0, values)</code>.
     *
     * @param values list of values to set.
     * @see #putRange(int, List)
     */
    public void setValues(List<?> values) {
        putRange(0, values);
    }

    /**
     * Gets values of cells range of this column. The range is defined by given start and end cell references.
     *
     * @param startRef reference string to the start cell of the range. Only row part is taken into consideration.
     *                 E.g. "A5".
     * @param endRef   reference string to the end cell of the range. Only row part is taken into consideration.
     *                 E.g. "A23".
     * @return list of values of the cells range. Returns empty list if specified range is empty. The actual class
     * of values in list depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or 
     * {@code String}.
     * @see #getRange(int, int, Class)
     */
    public List<Object> getRange(String startRef, String endRef) {
        return getRange(new CellRef(startRef).getRow(), new CellRef(endRef).getRow());
    }

    /**
     * Gets values of cells range on this column and converts them to the type specified by {@code valueType}.
     * The range is defined by given start and end cell references.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell values to corresponding type or {@code null} if the conversion
     * fails.
     * <p>
     * For other types it performs simple type casting of cell values to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param startRef  reference string to the start cell of the range. Only row part is taken into consideration.
     *                  E.g. "A5".
     * @param endRef    reference string to the end cell of the range. Only row part is taken into consideration.
     *                  E.g. "A23".
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of {@code valueType}.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> List<T> getRange(String startRef, String endRef, Class<T> valueType) {
        return getRange(new CellRef(startRef).getRow(), new CellRef(endRef).getRow(), valueType);
    }

    /**
     * Gets values of cells range on this column. The range is defined by given top and bottom row indexes.
     *
     * @param startRow 0-based index of top row of the range.
     * @param endRow   0-based index of bottom row of the range.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The actual class
     * of values depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public List<Object> getRange(int startRow, int endRow) {
        return getRange(startRow, endRow, Object.class);
    }

    /**
     * Gets values of cells range on this column and converts them to the type specified by {@code valueType}.
     * The range is defined by given top and bottom row indexes.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell values to corresponding type or {@code null} if the conversion
     * fails.
     * <p>
     * For other types it performs simple type casting of cell values to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param startRow  0-based index of top row of the range.
     * @param endRow    0-based index of bottom row of the range.
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of {@code valueType}.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> List<T> getRange(int startRow, int endRow, Class<T> valueType) {
        List<T> values = new ArrayList<>();

        int r1 = Math.min(startRow, endRow);
        int r2 = Math.max(startRow, endRow);

        for (int row = r1; row <= r2; row++) {
            values.add(getValue(row, valueType));
        }
        return values;
    }

    /**
     * Sets given values to cells range of this column which starts from cell defined by <code>startRef</code>.
     * <p>
     * The end cell of the range is defined by size of given values.
     *
     * @param startRef reference string to the start cell of the range. E.g. "A23".
     * @param values   list of values to set.
     */
    public void putRange(String startRef, List<?> values) {
        putRange(new CellRef(startRef).getRow(), values);
    }

    /**
     * Sets given values to cells range of this column which starts from cell with row index defined by
     * <code>startRow</code>.
     * <p>
     * The end cell of the range is defined by size of given values.
     *
     * @param startRow 0-based row index of the start cell of the range.
     * @param values   list of values to set.
     */
    public void putRange(int startRow, List<?> values) {
        if (values != null) {
            final int rowsAmountToAppend = startRow + values.size() - parent.getMaxRowsCount();
            getDocument().batchUpdate(r -> {
                if (rowsAmountToAppend > 0) {
                    parent.appendRowsMetadata(rowsAmountToAppend);
                    r.addAppendRowsRequest(rowsAmountToAppend, parent.getId());
                }
                int row = startRow;
                for (Object cellValue : values) {
                    Cell cell = getCell(row);
                    if (cell == null) {
                        cell = createCell(row);
                    }
                    cell.setValue(cellValue);
                    row++;
                }
            });
        }
    }

    /**
     * Gets the cell of this column represented by given reference.
     *
     * @param cellRef reference string to necessary cell. Only row part is taken into consideration. E.g. "A23".
     * @return instance of corresponding cell or {@code null} if cell is not defined.
     */
    public Cell getCell(String cellRef) {
        return getCell(new CellRef(cellRef).getRow());
    }

    /**
     * Gets the cell of this column represented by given row index.
     *
     * @param rowIndex 0-based row index of necessary cell.
     * @return instance of corresponding cell or {@code null} if cell is not defined.
     */
    public Cell getCell(int rowIndex) {
        int cellDataIndex = -1;
        List<CellData> cellsData = null;
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                cellDataIndex = columnIndex - startColumn;
                cellsData = gridData.getRowData().get(rowIndex - startRow).getValues();
                break;
            }
        }
        if (cellsData != null && cellDataIndex >= 0 && cellDataIndex < cellsData.size()) {
            CellData cell = cellsData.get(cellDataIndex);
            return cell != null ? new Cell(parent, rowIndex, columnIndex) : null;
        }
        return null;
    }

    /**
     * Creates a new cell at the given row index and returns its representing object.
     *
     * @param rowIndex 0-based row index where cell should be created.
     * @return object representing the created cell.
     */
    public Cell createCell(int rowIndex) {
        RowData rowData = null;
        GridData relatedGridData = null;
        List<GridData> gridsData = parent.getGSheetGridsData();

        int lastRowIndex = getLastRowIndex();
        if (rowIndex > lastRowIndex) {
            GridData lastGridData = gridsData.get(gridsData.size() - 1);
            List<RowData> rowsData = lastGridData.getRowData();
            if (rowsData == null || rowsData.isEmpty()) {
                rowData = new RowData();
                rowsData = new ArrayList<>();
                rowsData.add(rowData);
                lastGridData.setRowData(rowsData);
                lastGridData.setStartRow(rowIndex);
            } else {
                for (int i = lastRowIndex + 1; i <= rowIndex; i++) {
                    rowsData.add(new RowData());
                }
                int startRow = lastGridData.getStartRow() != null ? lastGridData.getStartRow() : 0;
                rowData = rowsData.get(rowIndex - startRow);
            }

        } else {
            for (int gridIndex = 0; gridIndex < gridsData.size(); gridIndex++) {
                GridData gridData = gridsData.get(gridIndex);
                int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;

                if (rowIndex < startRow) {
                    rowData = new RowData();
                    relatedGridData = new GridData();
                    relatedGridData.setStartRow(rowIndex);
                    relatedGridData.setRowData(new ArrayList<>());
                    relatedGridData.getRowData().add(rowData);
                    gridsData.add(gridIndex, relatedGridData);
                    break;
                }

                if (rowIndex < startRow + gridData.getRowData().size()) {
                    relatedGridData = gridData;
                    rowData = gridData.getRowData().get(rowIndex - startRow);
                    break;
                }

                if (rowIndex == startRow + gridData.getRowData().size()) {
                    relatedGridData = gridData;
                    rowData = new RowData();
                    gridData.getRowData().add(rowData);
                    break;
                }
            }
        }

        if (relatedGridData == null || rowData == null) {
            return null;
        }

        int startColumn = relatedGridData.getStartColumn() != null ? relatedGridData.getStartColumn() : 0;

        List<CellData> cellsData = rowData.getValues();
        if (cellsData == null) {
            cellsData = new ArrayList<>();
            rowData.setValues(cellsData);
        }

        if (columnIndex < startColumn) {
            for (int i = startColumn - columnIndex; i > 0; i--) {
                cellsData.add(0, new CellData());
            }
            relatedGridData.setStartColumn(columnIndex);
        } else if (columnIndex > startColumn + cellsData.size() - 1) {
            for (int i = columnIndex - startColumn - cellsData.size() + 1; i > 0; i--) {
                cellsData.add(new CellData());
            }
        }

        return new Cell(parent, rowIndex, columnIndex);
    }

    /**
     * Gets row index of the first defined cell at this column.
     * <p>
     * Cell is defined if underlying Google CellData object is not {@code null}.
     *
     * @return 0-based row index of the first defined cell at this column or <code>-1</code> if no cells
     * exist (column is empty).
     */
    public int getFirstRowIndex() {
        return parent.getFirstRowIndex();
    }

    /**
     * Gets row index of the last cell of this column.
     *
     * @return 0-based row index of the last cell of this column or <code>-1</code> if no cells
     * exist (column is empty).
     */
    public int getLastRowIndex() {
        return parent.getLastRowIndex();
    }

    /**
     * Checks whether this column is empty.
     *
     * @return {@code true} if all cells of this column are not defined, blank or has empty values.
     * Returns {@code false} otherwise.
     */
    public boolean isEmpty() {
        for (Cell cell : this) {
            if (cell != null && !cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an iterator of objects representing existing cells of this column. Meaning it won't iterate
     * over undefined cells.
     * <p>
     * >This method allows using of this column object in "for" loop:
     * <pre>
     *     for(Cell cell: column){
     *         int rowIndex = cell.getRowIndex();
     *         ...
     *     }
     * </pre>
     *
     * @return on iterator of objects representing existing cell of this column.
     */
    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator(parent.getGSheet());
    }

    /**
     * Cells iterator. Allows iteration over all existing cells of this column using "for" loop.
     */
    private class CellIterator implements Iterator<Cell> {

        private List<GridData> gridsData;
        private int index = 0;
        private int cellsCount;

        public CellIterator(com.google.api.services.sheets.v4.model.Sheet gSheet) {
            gridsData = gSheet.getData();
            GridData lastGridData = null;
            for (int i = gridsData.size() - 1; i >= 0; i--) {
                GridData gridData = gridsData.get(i);
                if (gridData.getRowData() != null && gridData.getRowData().size() > 0) {
                    lastGridData = gridData;
                    break;
                }
            }
            int startRow = lastGridData != null && lastGridData.getStartRow() != null ? lastGridData.getStartRow() : 0;
            cellsCount = lastGridData != null ? startRow + lastGridData.getRowData().size() : 0;
        }

        @Override
        public boolean hasNext() {
            int cellDataIndex = -1;
            RowData nextRow = null;
            while (nextRow == null && index < cellsCount) {
                for (GridData gridData : gridsData) {
                    if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                        continue;
                    }
                    int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
                    int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
                    if (index >= startRow && index < startRow + gridData.getRowData().size()) {
                        cellDataIndex = columnIndex - startColumn;
                        nextRow = gridData.getRowData().get(index - startRow);
                        break;
                    }
                }
                if (nextRow == null || nextRow.getValues() == null
                        || cellDataIndex < 0 || cellDataIndex >= nextRow.getValues().size()
                        || nextRow.getValues().get(cellDataIndex) == null) {
                    nextRow = null;
                    index++;
                }
            }
            return nextRow != null;
        }

        @Override
        public Cell next() {
            return new Cell(parent, index++, columnIndex);
        }
    }
}
