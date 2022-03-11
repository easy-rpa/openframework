package eu.easyrpa.openframework.excel;

import eu.easyrpa.openframework.excel.internal.poi.POIElementsCache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents specific row of Excel document and provides functionality to work with it.
 */
public class Row implements Iterable<Cell> {

    /**
     * Unique id of this row.
     */
    private String id;

    /**
     * Reference to parent sheet.
     */
    private Sheet parent;

    /**
     * Unique id of parent Excel document.
     */
    private int documentId;

    /**
     * Index of the parent sheet.
     */
    private int sheetIndex;

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
        this.documentId = parent.getDocument().getId();
        this.sheetIndex = parent.getIndex();
        this.rowIndex = rowIndex;
        this.id = POIElementsCache.getId(sheetIndex, rowIndex);
    }

    /**
     * Gets parent Excel document.
     *
     * @return parent Excel document.
     */
    public ExcelDocument getDocument() {
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
     * @return value of corresponding cell or <code>null</code> if nothing is found. The actual class of value
     * is depend on cell type. Can be returned <code>Double</code>, <code>Boolean</code>, <code>Date</code>
     * or <code>String</code>.
     */
    public Object getValue(String cellRef) {
        return getValue(new CellRef(cellRef).getCol(), Object.class);
    }

    /**
     * Gets the value of this row cell by given cell reference and converts it to the type
     * specified by <code>valueType</code>.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
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
        return getValue(new CellRef(cellRef).getCol(), valueType);
    }

    /**
     * Gets the value of this row cell by given column index.
     *
     * @param colIndex 0-based column index of the cell.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The actual class of value
     * is depend on cell type. Can be returned <code>Double</code>, <code>Boolean</code>, <code>Date</code>
     * or <code>String</code>.
     */
    public Object getValue(int colIndex) {
        return getValue(colIndex, Object.class);
    }

    /**
     * Gets the value of this row cell by given column index. The return value is automatically
     * converted to the type specified by <code>valueType</code>.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell value to double.
     * If such conversion is not possible then value will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell value to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param colIndex  0-based column index of the cell.
     * @param valueType class instance of returning value.
     * @param <T>       type of returning value. Defined by value of <code>valueType</code>.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The class of return
     * value is defined by <code>valueType</code>. If the actual class of cell value is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and the value of cell
     *                            cannot be cast to <code>T</code>.
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
        Cell cell = getCell(colIndex);
        if (cell == null) {
            cell = createCell(colIndex);
        }
        cell.setValue(value);
    }

    /**
     * Gets values of all row cells.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this row.
     *
     * @return list of values of this row cells. Returns empty list if row is empty. The actual class of values in list
     * depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or <code>String</code>.
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
     * Returning values are automatically converted to the type specified by <code>valueType</code>.
     * <p>
     * It's an equivalent to getting of range between first and last cells of this row.
     *
     * @param valueType class instance of returning values.
     * @param <T>       type of returning values. Defined by value of <code>valueType</code>.
     * @return list of values of this row cells. Returns empty list if row is empty. The class of returning
     * values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
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
     * of values in list depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or
     * <code>String</code>.
     * @see #getRange(int, int, Class)
     */
    public List<Object> getRange(String startRef, String endRef) {
        return getRange(new CellRef(startRef).getCol(), new CellRef(endRef).getCol());
    }

    /**
     * Gets values of cells range on this row and converts them to the type specified by <code>valueType</code>.
     * The range is defined by given start and end cell references.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell values
     * to double. If such conversion is not possible then values will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell values to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param startRef  reference string to the start cell of the range. Only column part is taken into consideration.
     *                  E.g. "A23".
     * @param endRef    reference string to the end cell of the range. Only column part is taken into consideration.
     *                  E.g. "D23".
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of <code>valueType</code>.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and value of cells
     *                            cannot be cast to <code>T</code>.
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
     * of values depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or
     * <code>String</code>.
     */
    public List<Object> getRange(int startCol, int endCol) {
        return getRange(startCol, endCol, Object.class);
    }

    /**
     * Gets values of cells range on this row and converts them to the type specified by <code>valueType</code>.
     * The range is defined by given left and right column indexes.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell values to
     * double. If such conversion is not possible then values will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell values to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param startCol  0-based index of left column of the range.
     * @param endCol    0-based index of right column of the range.
     * @param valueType class instance of returning cell values.
     * @param <T>       type of returning cell values. Defined by value of <code>valueType</code>.
     * @return list of values of the cells range. Returns empty list if specified range is empty. The class of return
     * cell values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and value of cells
     *                            cannot be cast to <code>T</code>.
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
            int col = startCol;
            for (Object cellValue : values) {
                setValue(col++, cellValue);
            }
        }
    }

    /**
     * Gets the cell of this row represented by given reference.
     *
     * @param cellRef reference string to necessary cell. Only column part is taken into consideration. E.g. "A23".
     * @return instance of corresponding cell or <code>null</code> if cell is not defined.
     */
    public Cell getCell(String cellRef) {
        return getCell(new CellRef(cellRef).getCol());
    }

    /**
     * Gets the cell of this row represented by given column index.
     *
     * @param colIndex 0-based column index of necessary cell.
     * @return instance of corresponding cell or <code>null</code> if cell is not defined.
     */
    public Cell getCell(int colIndex) {
        if (colIndex >= 0) {
            org.apache.poi.ss.usermodel.Cell cell = getPoiRow().getCell(colIndex);
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
        getPoiRow().createCell(colIndex);
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
     * Cell is defined if underlying POI object is not <code>null</code>.
     *
     * @return 0-based index of the first defined cell on this row or <code>-1</code> if no cells exist.
     */
    public int getFirstCellIndex() {
        return getPoiRow().getFirstCellNum();
    }

    /**
     * Gets index of the last cell of this row.
     *
     * @return 0-based index of the last cell of this row or <code>-1</code> if no cells exist.
     */
    public int getLastCellIndex() {
        return getPoiRow().getLastCellNum();
    }

    /**
     * Gets format of this row. Format includes information about style of each cell on the row, merged regions and
     * regions with data validation constraints.
     *
     * @return object representing the format of this row.
     * @see ExcelCellsFormat
     */
    public ExcelCellsFormat getFormat() {
        return new ExcelCellsFormat(this);
    }

    /**
     * Gets format of given cells range of this row. Format includes information about style of each cell in
     * the range, merged regions and regions with data validation constraints.
     *
     * @param firstCol 0-based index of left column of the range.
     * @param lastCol  0-based index of right column of the range.
     * @return object representing the format of given cells range of this row.
     * @see ExcelCellsFormat
     */
    public ExcelCellsFormat getFormat(int firstCol, int lastCol) {
        return new ExcelCellsFormat(this, firstCol, lastCol);
    }

    /**
     * <p>Returns an iterator of objects representing existing cells of this row. Meaning it won't iterate
     * over undefined cells.</p>
     * <p>This method allows using of this row object in "for" loop:</p>
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
        return new CellIterator(getPoiRow());
    }

    /**
     * Returns underlay POI object representing this row. This object can be used directly if some specific
     * POI functionality is necessary within RPA process.
     *
     * @return Apache POI object representing this row.
     */
    public org.apache.poi.ss.usermodel.Row getPoiRow() {
        return POIElementsCache.getPoiRow(documentId, id, sheetIndex, rowIndex);
    }

    /**
     * Cells iterator. Allows iteration over all existing cells of this row using "for" loop.
     */
    private class CellIterator implements Iterator<Cell> {

        private org.apache.poi.ss.usermodel.Row poiRow;
        private int index = 0;
        private int cellsCount;

        public CellIterator(org.apache.poi.ss.usermodel.Row poiRow) {
            this.poiRow = poiRow;
            this.cellsCount = poiRow.getLastCellNum() + 1;
        }

        @Override
        public boolean hasNext() {
            if (index < cellsCount) {
                org.apache.poi.ss.usermodel.Cell nextCell = poiRow.getCell(index);
                while (nextCell == null && index + 1 < cellsCount) {
                    nextCell = poiRow.getCell(++index);
                }
                return nextCell != null;
            }
            return false;
        }

        @Override
        public Cell next() {
            return new Cell(parent, rowIndex, index++);
        }
    }
}
