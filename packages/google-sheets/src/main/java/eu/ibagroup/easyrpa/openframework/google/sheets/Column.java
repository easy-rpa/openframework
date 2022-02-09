package eu.ibagroup.easyrpa.openframework.google.sheets;

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
     * @return value of corresponding cell or <code>null</code> if nothing is found. The actual class of value
     * is depend on cell type. Can be returned <code>Double</code>, <code>Boolean</code>, <code>Date</code>
     * or <code>String</code>.
     */
    public Object getValue(String cellRef) {
        return getValue(new CellRef(cellRef).getRow(), Object.class);
    }

    /**
     * Gets the value of this column cell by given cell reference and converts it to the type
     * specified by <code>valueType</code>.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of Google Sheets in browser.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell value to double.
     * If such conversion is not possible then value will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell value to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param cellRef   reference string to necessary cell. E.g. "A23".
     * @param valueType class instance of returning value.
     * @param <T>       type of returning value. Defined by value of <code>valueType</code>.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The class of return
     * value is defined by <code>valueType</code>. If the actual class of cell value is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and the value of cell
     *                            cannot be cast to <code>T</code>.
     */
    public <T> T getValue(String cellRef, Class<T> valueType) {
        return getValue(new CellRef(cellRef).getRow(), valueType);
    }

    /**
     * Gets the value of this column cell by given row index.
     *
     * @param rowIndex 0-based row index of the cell.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The actual class of value
     * is depend on cell type. Can be returned <code>Double</code>, <code>Boolean</code>, <code>Date</code>
     * or <code>String</code>.
     */
    public Object getValue(int rowIndex) {
        return getValue(rowIndex, Object.class);
    }

    /**
     * Gets the value of this column cell by given row index. The return value is automatically
     * converted to the type specified by <code>valueType</code>.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of Google Sheets in browser.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell value to double.
     * If such conversion is not possible then value will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell value to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param rowIndex  0-based row index of the cell.
     * @param valueType class instance of returning value.
     * @param <T>       type of returning value. Defined by value of <code>valueType</code>.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The class of return
     * value is defined by <code>valueType</code>. If the actual class of cell value is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and the value of cell
     *                            cannot be cast to <code>T</code>.
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
        Cell cell = getCell(rowIndex);
        if (cell == null) {
            cell = createCell(rowIndex);
        }
        cell.setValue(value);
    }

    /**
     * Gets values of all column cells.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this column.
     *
     * @return list of values of this column cells. Returns empty list if column is empty. The actual class of values
     * in list depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or
     * <code>String</code>.
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
     * Returning values are automatically converted to the type specified by <code>valueType</code>.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this column.
     *
     * @param valueType class instance of returning values.
     * @param <T>       type of returning values. Defined by value of <code>valueType</code>.
     * @return list of values of this column cells. Returns empty list if column is empty. The class of returning
     * values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
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
     * of values in list depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or
     * <code>String</code>.
     * @see #getRange(int, int, Class)
     */
    public List<Object> getRange(String startRef, String endRef) {
        return getRange(new CellRef(startRef).getRow(), new CellRef(endRef).getRow());
    }

    /**
     * Gets values of cells range on this column and converts them to the type specified by <code>valueType</code>.
     * The range is defined by given start and end cell references.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of Google Sheets in browser.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell values
     * to double. If such conversion is not possible then values will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell values to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param startRef  reference string to the start cell of the range. Only row part is taken into consideration.
     *                  E.g. "A5".
     * @param endRef    reference string to the end cell of the range. Only row part is taken into consideration.
     *                  E.g. "A23".
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of <code>valueType</code>.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and value of cells
     *                            cannot be cast to <code>T</code>.
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
     * of values depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or
     * <code>String</code>.
     */
    public List<Object> getRange(int startRow, int endRow) {
        return getRange(startRow, endRow, Object.class);
    }

    /**
     * Gets values of cells range on this column and converts them to the type specified by <code>valueType</code>.
     * The range is defined by given top and bottom row indexes.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of Google Sheets in browser.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell values to
     * double. If such conversion is not possible then values will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell values to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param startRow  0-based index of top row of the range.
     * @param endRow    0-based index of bottom row of the range.
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of <code>valueType</code>.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and value of cells
     *                            cannot be cast to <code>T</code>.
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
            getDocument().batchUpdate(r -> {
                int row = startRow;
                for (Object cellValue : values) {
                    setValue(row++, cellValue);
                }
            });
        }
    }

    /**
     * Gets the cell of this column represented by given reference.
     *
     * @param cellRef reference string to necessary cell. Only row part is taken into consideration. E.g. "A23".
     * @return instance of corresponding cell or <code>null</code> if cell is not defined.
     */
    public Cell getCell(String cellRef) {
        return getCell(new CellRef(cellRef).getRow());
    }

    /**
     * Gets the cell of this column represented by given row index.
     *
     * @param rowIndex 0-based row index of necessary cell.
     * @return instance of corresponding cell or <code>null</code> if cell is not defined.
     */
    public Cell getCell(int rowIndex) {
        int cellDataIndex = -1;
        List<CellData> cellsData = null;
        for (GridData gridData : parent.getGSheet().getData()) {
            if (rowIndex >= gridData.getStartRow() && rowIndex < gridData.getStartRow() + gridData.getRowData().size()) {
                cellDataIndex = columnIndex - gridData.getStartColumn();
                cellsData = gridData.getRowData().get(rowIndex - gridData.getStartRow()).getValues();
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
        com.google.api.services.sheets.v4.model.Sheet gSheet = parent.getGSheet();
        List<GridData> gridsData = gSheet.getData();
        GridData lastGridData = gridsData.get(gridsData.size() - 1);
        int lastRowIndex = lastGridData.getStartRow() + lastGridData.getRowData().size() - 1;
        if (rowIndex > lastRowIndex) {
            relatedGridData = lastGridData;
            for (int i = lastRowIndex + 1; i <= rowIndex; i++) {
                lastGridData.getRowData().add(new RowData());
            }
            rowData = lastGridData.getRowData().get(rowIndex - lastGridData.getStartRow());
            getDocument().batchUpdate(r -> {
                r.addAppendRowsRequest(rowIndex - lastRowIndex, gSheet.getProperties().getSheetId());
            });
        } else {
            for (int gridIndex = 0; gridIndex < gridsData.size(); gridIndex++) {
                GridData gridData = gridsData.get(gridIndex);
                int startRow = gridData.getStartRow();

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

        List<CellData> cellsData = rowData.getValues();
        if (cellsData == null) {
            cellsData = new ArrayList<>();
            cellsData.add(new CellData());
            rowData.setValues(cellsData);
        }

        if (columnIndex < relatedGridData.getStartColumn()) {
            for (int i = relatedGridData.getStartColumn() - columnIndex; i > 0; i--) {
                cellsData.add(0, new CellData());
            }
            relatedGridData.setStartColumn(columnIndex);
        } else if (columnIndex > relatedGridData.getStartColumn() + cellsData.size()) {
            for (int i = columnIndex - relatedGridData.getStartColumn() - cellsData.size(); i > 0; i--) {
                cellsData.add(new CellData());
            }
        }
        //TODO check whether is necessary to do a request to add new cells data.
        return new Cell(parent, rowIndex, columnIndex);
    }

    /**
     * Gets row index of the first defined cell at this column.
     * <p>
     * Cell is defined if underlying Google CellData object is not <code>null</code>.
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
     * @return <code>true</code> if all cells of this column are not defined, blank or has empty values.
     * Returns <code>false</code> otherwise.
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
            GridData lastGridData = gridsData.get(gridsData.size() - 1);
            cellsCount = lastGridData.getStartRow() + lastGridData.getRowData().size();
        }

        @Override
        public boolean hasNext() {
            int cellDataIndex = -1;
            RowData nextRow = null;
            while (nextRow == null && index < cellsCount) {
                for (GridData gridData : gridsData) {
                    if (index >= gridData.getStartRow() && index < gridData.getStartRow() + gridData.getRowData().size()) {
                        cellDataIndex = columnIndex - gridData.getStartColumn();
                        nextRow = gridData.getRowData().get(index - gridData.getStartRow());
                        break;
                    }
                }
                if (nextRow == null) {
                    index++;
                }
            }

            return nextRow != null && nextRow.getValues() != null
                    && cellDataIndex >= 0 && cellDataIndex < nextRow.getValues().size()
                    && nextRow.getValues().get(cellDataIndex) != null;
        }

        @Override
        public Cell next() {
            return new Cell(parent, index++, columnIndex);
        }
    }
}
