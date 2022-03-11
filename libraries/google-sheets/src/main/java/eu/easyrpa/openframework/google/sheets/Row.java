package eu.easyrpa.openframework.google.sheets;


import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.RowData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents specific row of Spreadsheet document and provides functionality to work with it.
 */
public class Row implements Iterable<Cell> {

    /**
     * Reference to parent sheet.
     */
    private Sheet parent;

    /**
     * Index of this row.
     */
    private int rowIndex;

    /**
     * Creates a new instance of row.
     *
     * @param parent   reference to parent sheet.
     * @param rowIndex 0-based index of this row.
     */
    protected Row(Sheet parent, int rowIndex) {
        this.parent = parent;
        this.rowIndex = rowIndex;
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
     * Gets index of this row.
     *
     * @return 0-based index of this row.
     */
    public int getIndex() {
        return rowIndex;
    }

    /**
     * Gets reference to this row.
     * <p>
     * Row reference is a reference to its cell with column index 0.
     *
     * @return this row reference.
     * @see CellRef
     */
    public CellRef getReference() {
        return new CellRef(rowIndex, 0);
    }

    /**
     * Gets the value of this row cell by given cell reference.
     *
     * @param cellRef reference string to necessary cell. E.g. "A23".
     * @return value of corresponding cell or {@code null} if nothing is found. The actual class of value
     * is depend on cell type. Can be returned {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue(String cellRef) {
        return getValue(new CellRef(cellRef).getCol(), Object.class);
    }

    /**
     * Gets the value of this row cell by given cell reference and converts it to the type
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
        return getValue(new CellRef(cellRef).getCol(), valueType);
    }

    /**
     * Gets the value of this row cell by given column index.
     *
     * @param colIndex 0-based column index of the cell.
     * @return value of corresponding cell or {@code null} if nothing is found. The actual class of value
     * is depend on cell type. Can be returned {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue(int colIndex) {
        return getValue(colIndex, Object.class);
    }

    /**
     * Gets the value of this row cell by given column index. The return value is automatically
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
     * @param colIndex  0-based column index of the cell.
     * @param valueType class instance of returning value.
     * @param <T>       type of returning value. Defined by value of {@code valueType}.
     * @return value of corresponding cell or {@code null} if nothing is found. The class of return
     * value is defined by {@code valueType}. If the actual class of cell value is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> T getValue(int colIndex, Class<T> valueType) {
        Cell cell = getCell(colIndex);
        return cell != null ? cell.getValue(valueType) : null;
    }

    /**
     * Sets the value of this row cell by given cell reference.
     *
     * @param cellRef reference string to necessary cell. E.g. "A23".
     * @param value   the value to set.
     */
    public void setValue(String cellRef, Object value) {
        setValue(new CellRef(cellRef).getCol(), value);
    }

    /**
     * Sets the value of this row cell by given column index.
     *
     * @param colIndex 0-based column index of the cell.
     * @param value    the value to set.
     */
    public void setValue(int colIndex, Object value) {
        final int columnsAmountToAppend = colIndex + 1 - parent.getMaxColumnsCount();
        getDocument().batchUpdate(r -> {
            if (columnsAmountToAppend > 0) {
                parent.appendColumnMetadata(columnsAmountToAppend);
                r.addAppendColumnsRequest(columnsAmountToAppend, parent.getId());
            }
            Cell cell = getCell(colIndex);
            if (cell == null) {
                cell = createCell(colIndex);
            }
            cell.setValue(value);
        });
    }

    /**
     * Gets values of all row cells.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this row.
     *
     * @return list of values of this row cells. Returns empty list if row is empty. The actual class of values in list
     * depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     * @see #getRange(int, int, Class)
     * @see #getFirstCellIndex()
     * @see #getLastCellIndex()
     */
    public List<Object> getValues() {
        return getValues(Object.class);
    }

    /**
     * Gets values of all row cells.
     * <p>
     * Returning values are automatically converted to the type specified by {@code valueType}.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this row.
     *
     * @param valueType class instance of returning values.
     * @param <T>       type of returning values. Defined by value of {@code valueType}.
     * @return list of values of this row cells. Returns empty list if row is empty. The class of returning
     * values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @see #getRange(int, int, Class)
     * @see #getFirstCellIndex()
     * @see #getLastCellIndex()
     */
    public <T> List<T> getValues(Class<T> valueType) {
        return getRange(getFirstCellIndex(), getLastCellIndex(), valueType);
    }

    /**
     * Sets given values to cells range of this row which starts from cell with column index 0.
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
     * Gets values of cells range of this row. The range is defined by given start and end cell references.
     *
     * @param startRef reference string to the start cell of the range. Only column part is taken into consideration.
     *                 E.g. "A23".
     * @param endRef   reference string to the end cell of the range. Only column part is taken into consideration.
     *                 E.g. "D23".
     * @return list of values of the cells range. Returns empty list if specified range is empty. The actual class
     * of values in list depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or
     * {@code String}.
     * @see #getRange(int, int, Class)
     */
    public List<Object> getRange(String startRef, String endRef) {
        return getRange(new CellRef(startRef).getCol(), new CellRef(endRef).getCol());
    }

    /**
     * Gets values of cells range on this row and converts them to the type specified by {@code valueType}.
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
     * @param startRef  reference string to the start cell of the range. Only column part is taken into consideration.
     *                  E.g. "A23".
     * @param endRef    reference string to the end cell of the range. Only column part is taken into consideration.
     *                  E.g. "D23".
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of {@code valueType}.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> List<T> getRange(String startRef, String endRef, Class<T> valueType) {
        return getRange(new CellRef(startRef).getCol(), new CellRef(endRef).getCol(), valueType);
    }

    /**
     * Gets values of cells range on this row. The range is defined by given left and right column indexes.
     *
     * @param startCol 0-based index of left column of the range.
     * @param endCol   0-based index of right column of the range.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The actual class
     * of values depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public List<Object> getRange(int startCol, int endCol) {
        return getRange(startCol, endCol, Object.class);
    }

    /**
     * Gets values of cells range on this row and converts them to the type specified by {@code valueType}.
     * The range is defined by given left and right column indexes.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell values to corresponding type or {@code null} if the conversion
     * fails.
     * <p>
     * For other types it performs simple type casting of cell values to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param startCol  0-based index of left column of the range.
     * @param endCol    0-based index of right column of the range.
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of {@code valueType}.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
     */
    public <T> List<T> getRange(int startCol, int endCol, Class<T> valueType) {
        List<T> values = new ArrayList<>();

        int c1 = Math.min(startCol, endCol);
        int c2 = Math.max(startCol, endCol);

        for (int col = c1; col <= c2; col++) {
            values.add(getValue(col, valueType));
        }
        return values;
    }

    /**
     * Sets given values to cells range of this row which starts from cell defined by <code>startRef</code>.
     * <p>
     * The end cell of the range is defined by size of given values.
     *
     * @param startRef reference string to the start cell of the range. E.g. "A23".
     * @param values   list of values to set.
     */
    public void putRange(String startRef, List<?> values) {
        putRange(new CellRef(startRef).getCol(), values);
    }

    /**
     * Sets given values to cells range of this row which starts from cell with column index defined by
     * <code>startCol</code>.
     * <p>
     * The end cell of the range is defined by size of given values.
     *
     * @param startCol 0-based column index of the start cell of the range.
     * @param values   list of values to set.
     */
    public void putRange(int startCol, List<?> values) {
        if (values != null) {
            final int columnsAmountToAppend = startCol + values.size() - parent.getMaxColumnsCount();
            getDocument().batchUpdate(r -> {
                if (columnsAmountToAppend > 0) {
                    parent.appendColumnMetadata(columnsAmountToAppend);
                    r.addAppendColumnsRequest(columnsAmountToAppend, parent.getId());
                }
                int col = startCol;
                for (Object cellValue : values) {
                    Cell cell = getCell(col);
                    if (cell == null) {
                        cell = createCell(col);
                    }
                    cell.setValue(cellValue);
                    col++;
                }
            });
        }
    }

    /**
     * Gets the cell of this row represented by given reference.
     *
     * @param cellRef reference string to necessary cell. Only column part is taken into consideration. E.g. "A23".
     * @return instance of corresponding cell or {@code null} if cell is not defined.
     */
    public Cell getCell(String cellRef) {
        return getCell(new CellRef(cellRef).getCol());
    }

    /**
     * Gets the cell of this row represented by given column index.
     *
     * @param colIndex 0-based column index of necessary cell.
     * @return instance of corresponding cell or {@code null} if cell is not defined.
     */
    public Cell getCell(int colIndex) {
        int cellDataIndex = -1;
        List<CellData> cellsData = null;
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                cellDataIndex = colIndex - startColumn;
                cellsData = gridData.getRowData().get(rowIndex - startRow).getValues();
                break;
            }
        }
        if (cellsData != null && cellDataIndex >= 0 && cellDataIndex < cellsData.size()) {
            CellData cell = cellsData.get(cellDataIndex);
            return cell != null ? new Cell(parent, rowIndex, colIndex) : null;
        }
        return null;
    }

    /**
     * Creates a new cell at the given column index and returns its representing object.
     *
     * @param colIndex 0-based column index where cell should be created.
     * @return object representing the created cell.
     */
    public Cell createCell(int colIndex) {
        GridData relatedGridData = null;
        RowData row = null;
        for (GridData gridData : parent.getGSheetGridsData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                relatedGridData = gridData;
                row = gridData.getRowData().get(rowIndex - startRow);
                break;
            }
        }

        if (relatedGridData == null || row == null) {
            return null;
        }

        int startColumn = relatedGridData.getStartColumn() != null ? relatedGridData.getStartColumn() : 0;

        List<CellData> cellsData = row.getValues();
        if (cellsData == null) {
            cellsData = new ArrayList<>();
            row.setValues(cellsData);
        }

        if (colIndex < startColumn) {
            for (int i = startColumn - colIndex; i > 0; i--) {
                if (cellsData.isEmpty()) {
                    cellsData.add(new CellData());
                } else {
                    cellsData.add(0, new CellData());
                }
            }
            relatedGridData.setStartColumn(colIndex);
        } else if (colIndex > startColumn + cellsData.size() - 1) {
            for (int i = colIndex - startColumn - cellsData.size() + 1; i > 0; i--) {
                cellsData.add(new CellData());
            }
        }

        return new Cell(parent, rowIndex, colIndex);
    }

    /**
     * Creates a new cell at the end of this row and sets to it given value.
     *
     * @param value the cell value to set.
     * @return object representing the created cell.
     */
    public Cell addCell(Object value) {
        Cell cell = createCell(getLastCellIndex() + 1);
        cell.setValue(value);
        return cell;
    }

    /**
     * Gets index of the first defined cell on this row.
     * <p>
     * Cell is defined if underlying POI object is not {@code null}.
     *
     * @return 0-based index of the first defined cell on this row or <code>-1</code> if no cells exist.
     */
    public int getFirstCellIndex() {
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                List<CellData> cellsData = gridData.getRowData().get(rowIndex - startRow).getValues();
                if (cellsData != null && !cellsData.isEmpty()) {
                    return gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
                }
                break;
            }
        }
        return -1;
    }

    /**
     * Gets index of the last cell of this row.
     *
     * @return 0-based index of the last cell of this row or <code>-1</code> if no cells exist.
     */
    public int getLastCellIndex() {
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
                List<CellData> cellsData = gridData.getRowData().get(rowIndex - startRow).getValues();
                if (cellsData != null) {
                    return startColumn + cellsData.size() - 1;
                }
                break;
            }
        }
        return -1;
    }

    /**
     * Returns an iterator of objects representing existing cells of this row. Meaning it won't iterate
     * over undefined cells.
     * <p>
     * This method allows using of this row object in "for" loop:
     * <pre>
     *     for(Cell cell: row){
     *         int colIndex = cell.getColumnIndex();
     *         ...
     *     }
     * </pre>
     *
     * @return on iterator of objects representing existing cell of this row.
     */
    @Override
    public Iterator<Cell> iterator() {
        int startCol = 0;
        RowData row = null;
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                startCol = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
                row = gridData.getRowData().get(rowIndex - startRow);
                break;
            }
        }
        return new CellIterator(startCol, row);
    }

    /**
     * Returns underlay Google API object representing this row. This object can be used directly if some specific
     * Google API functionality is necessary within RPA process.
     *
     * @return Google API object representing this row.
     */
    public RowData getGRow() {
        RowData row = null;
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                row = gridData.getRowData().get(rowIndex - startRow);
                break;
            }
        }
        return row;
    }

    /**
     * Cells iterator. Allows iteration over all existing cells of this row using "for" loop.
     */
    private class CellIterator implements Iterator<Cell> {

        private RowData gSheetRow;
        private int index = 0;
        private int cellsCount;
        private int startCol;

        public CellIterator(int startCol, RowData gSheetRow) {
            this.startCol = startCol;
            this.gSheetRow = gSheetRow;
            this.cellsCount = gSheetRow != null && gSheetRow.getValues() != null ? gSheetRow.getValues().size() : 0;
        }

        @Override
        public boolean hasNext() {
            if (index < cellsCount) {
                CellData nextCell = gSheetRow.getValues().get(index);
                while (nextCell == null && index + 1 < cellsCount) {
                    nextCell = gSheetRow.getValues().get(++index);
                }
                return nextCell != null;
            }
            return false;
        }

        @Override
        public Cell next() {
            return new Cell(parent, rowIndex, startCol + index++);
        }
    }
}
