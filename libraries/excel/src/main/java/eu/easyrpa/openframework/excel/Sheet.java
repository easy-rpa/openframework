package eu.easyrpa.openframework.excel;

import eu.easyrpa.openframework.core.utils.FilePathUtils;
import eu.easyrpa.openframework.core.utils.TypeUtils;
import eu.easyrpa.openframework.excel.annotations.ExcelColumn;
import eu.easyrpa.openframework.excel.annotations.ExcelTable;
import eu.easyrpa.openframework.excel.constants.InsertMethod;
import eu.easyrpa.openframework.excel.constants.MatchMethod;
import eu.easyrpa.openframework.excel.exceptions.VBScriptExecutionException;
import eu.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import eu.easyrpa.openframework.excel.internal.poi.XSSFSheetExt;
import eu.easyrpa.openframework.excel.vbscript.*;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents specific sheet of Excel document and provides functionality to work with it and its content.
 */
public class Sheet implements Iterable<eu.easyrpa.openframework.excel.Row> {

    /**
     * Reference to parent Excel document.
     */
    private ExcelDocument parent;

    /**
     * Unique id of parent Excel document.
     */
    private int documentId;

    /**
     * Index of this sheet within parent Excel document.
     */
    private int sheetIndex;

    /**
     * Creates a new Sheet instance.
     *
     * @param parent     reference to parent Excel document.
     * @param sheetIndex index of the sheet within parent Excel document.
     */
    protected Sheet(ExcelDocument parent, int sheetIndex) {
        this.parent = parent;
        this.documentId = parent.getId();
        this.sheetIndex = sheetIndex;
    }

    /**
     * Gets parent Excel document.
     *
     * @return parent Excel document.
     */
    public ExcelDocument getDocument() {
        return parent;
    }

    /**
     * Gets index of this sheet within parent Excel document.
     *
     * @return index of this sheet within parent Excel document.
     */
    public int getIndex() {
        return sheetIndex;
    }

    /**
     * Gets the name of this sheet.
     *
     * @return the name of this sheet.
     */
    public String getName() {
        return getPoiSheet().getSheetName();
    }

    /**
     * Gets the cell represented by given reference.
     *
     * @param cellRef reference to necessary cell. E.g. "A23".
     * @return instance of corresponding cell or <code>null</code> if cell is not defined.
     */
    public Cell getCell(String cellRef) {
        CellRef ref = new CellRef(cellRef);
        return getCell(ref.getRow(), ref.getCol());
    }

    /**
     * Gets the cell represented by given row and column indexes.
     *
     * @param rowIndex 0-based row index of necessary cell.
     * @param colIndex 0-based column index of necessary cell.
     * @return instance of corresponding cell or <code>null</code> if cell is not defined.
     */
    public Cell getCell(int rowIndex, int colIndex) {
        eu.easyrpa.openframework.excel.Row row = getRow(rowIndex);
        return row != null ? row.getCell(colIndex) : null;
    }

    /**
     * Searches cell with given value on the sheet.
     *
     * @param value the value to lookup. Exact matching is used during comparing.
     * @return instance of the first found cell with given value or <code>null</code> if nothing is found.
     */
    public Cell findCell(String value) {
        return findCell(MatchMethod.EXACT, value);
    }

    /**
     * Searches cell with given value on the sheet.
     *
     * @param matchMethod the way how the given value will be matched with value of cell.
     * @param value       the value to lookup.
     * @return instance of the first found cell with given value or <code>null</code> if nothing is found.
     * @see MatchMethod
     */
    public Cell findCell(MatchMethod matchMethod, String value) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        for (eu.easyrpa.openframework.excel.Row row : this) {
            for (Cell cell : row) {
                if (matchMethod.match(cell.getValue(String.class), value)) {
                    return cell;
                }
            }
        }
        return null;
    }

    /**
     * Gets the value of this sheet cell by given cell reference.
     *
     * @param cellRef reference string to necessary cell. E.g. "A23".
     * @return value of corresponding cell or <code>null</code> if nothing is found. The actual class of value
     * is depend on cell type. Can be returned <code>Double</code>, <code>Boolean</code>, <code>Date</code>
     * or <code>String</code>.
     */
    public Object getValue(String cellRef) {
        CellRef ref = new CellRef(cellRef);
        return getValue(ref.getRow(), ref.getCol(), Object.class);
    }

    /**
     * Gets the value of this sheet cell by given cell reference and converts it to the type
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
     * @param valueType class instance of return value.
     * @param <T>       type of return value. Defined by value of <code>valueType</code>.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The class of return
     * value is defined by <code>valueType</code>. If the actual class of cell value is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and the value of cell
     *                            cannot be cast to <code>T</code>.
     */
    public <T> T getValue(String cellRef, Class<T> valueType) {
        CellRef ref = new CellRef(cellRef);
        return getValue(ref.getRow(), ref.getCol(), valueType);
    }

    /**
     * Gets the value of this sheet cell by given row and column indexes.
     *
     * @param rowIndex 0-based row index of necessary cell.
     * @param colIndex 0-based column index of necessary cell.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The actual class of value
     * is depend on cell type. Can be returned <code>Double</code>, <code>Boolean</code>, <code>Date</code>
     * or <code>String</code>.
     */
    public Object getValue(int rowIndex, int colIndex) {
        return getValue(rowIndex, colIndex, Object.class);
    }

    /**
     * Gets the value of this sheet cell by given row and column indexes. The return value is automatically
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
     * @param rowIndex  0-based row index of necessary cell.
     * @param colIndex  0-based column index of necessary cell.
     * @param valueType class instance of return value.
     * @param <T>       type of return value. Defined by value of <code>valueType</code>.
     * @return value of corresponding cell or <code>null</code> if nothing is found. The class of return
     * value is defined by <code>valueType</code>. If the actual class of cell value is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and the value of cell
     *                            cannot be cast to <code>T</code>.
     */
    public <T> T getValue(int rowIndex, int colIndex, Class<T> valueType) {
        Cell cell = getCell(rowIndex, colIndex);
        return cell != null ? cell.getValue(valueType) : null;
    }

    /**
     * Sets the value of the sheet cell by given cell reference.
     *
     * @param cellRef reference string to necessary cell. E.g. "A23".
     * @param value   the value to set.
     */
    public void setValue(String cellRef, Object value) {
        CellRef ref = new CellRef(cellRef);
        setValue(ref.getRow(), ref.getCol(), value);
    }

    /**
     * Sets the value of the sheet cell by given row and column indexes.
     *
     * @param rowIndex 0-based row index of necessary cell.
     * @param colIndex 0-based column index of necessary cell.
     * @param value    the value to set.
     */
    public void setValue(int rowIndex, int colIndex, Object value) {
        if (rowIndex >= 0 && colIndex >= 0) {
            eu.easyrpa.openframework.excel.Row row = getRow(rowIndex);
            if (row == null) {
                row = createRow(rowIndex);
            }
            row.setValue(colIndex, value);
        }
    }

    /**
     * Gets values of all cells on the sheet. It's an equivalent to getting of range between top-left and
     * bottom-right cells of this sheet.
     *
     * @return list of cell value lists. Returns empty list if sheet is empty. The actual class of values in lists
     * depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or <code>String</code>.
     * @see #getRange(int, int, int, int)
     * @see #getFirstRowIndex()
     * @see #getFirstColumnIndex()
     * @see #getLastRowIndex()
     * @see #getLastColumnIndex()
     */
    public List<List<Object>> getValues() {
        return getRange(getFirstRowIndex(), getFirstColumnIndex(), getLastRowIndex(), getLastColumnIndex());
    }

    /**
     * Sets given values to cells range of this sheet which starts from cell "A1" (0 - row index and 0 - column index).
     * The end (bottom-right) cell of the range is defined by sizes of given values.
     * <p>
     * It's an equivalent to <code>putRange(0, 0, values)</code>.
     *
     * @param values list of values to set. Can be simple list of values or list of value lists. Simple list of values
     *               is an equivalent to list with one list of values.
     * @see #putRange(int, int, List)
     */
    public void setValues(List<?> values) {
        putRange(0, 0, values);
    }

    /**
     * Gets values of cells range on this sheet. The range is defined by given top-left and bottom-right
     * cell references.
     *
     * @param startRef reference string to top-left cell of the range. E.g. "A23".
     * @param endRef   reference string to bottom-right cell of the range. E.g. "D50".
     * @return list of cell value lists. Returns empty list if specified range is empty. The actual class of values in lists
     * depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or <code>String</code>.
     */
    public List<List<Object>> getRange(String startRef, String endRef) {
        return getRange(startRef, endRef, Object.class);
    }

    /**
     * Gets values of cells range on this sheet and converts them to the type specified by <code>valueType</code>.
     * The range is defined by given top-left and bottom-right cell references.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell values to double.
     * If such conversion is not possible then values will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell values to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param startRef  reference string to top-left cell of the range. E.g. "A23".
     * @param endRef    reference string to bottom-right cell of the range. E.g. "D50".
     * @param valueType class instance of return cell values.
     * @param <T>       type of return cell values. Defined by value of <code>valueType</code>.
     * @return list of cell value lists. Returns empty list if specified range is empty. The class of return
     * cell values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and value of cells
     *                            cannot be cast to <code>T</code>.
     */
    public <T> List<List<T>> getRange(String startRef, String endRef, Class<T> valueType) {
        CellRef sRef = new CellRef(startRef);
        CellRef eRef = new CellRef(endRef);
        return getRange(sRef.getRow(), sRef.getCol(), eRef.getRow(), eRef.getCol(), valueType);
    }

    /**
     * Gets values of cells range on this sheet. The range is defined by given top row, left column,
     * bottom row and right column indexes.
     *
     * @param startRow 0-based index of top row of the range.
     * @param startCol 0-based index of left column of the range.
     * @param endRow   0-based index of bottom row of the range.
     * @param endCol   0-based index of right column of the range.
     * @return list of cell value lists. Returns empty list if specified range is empty. The actual class of values in lists
     * depend on cell types. Can be <code>Double</code>, <code>Boolean</code>, <code>Date</code> or <code>String</code>.
     */
    public List<List<Object>> getRange(int startRow, int startCol, int endRow, int endCol) {
        return getRange(startRow, startCol, endRow, endCol, Object.class);
    }

    /**
     * Gets values of cells range on this sheet and converts them to the type specified by <code>valueType</code>.
     * The range is defined by given top row, left column, bottom row and right column indexes.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell values to double.
     * If such conversion is not possible then values will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell values to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param startRow  0-based index of top row of the range.
     * @param startCol  0-based index of left column of the range.
     * @param endRow    0-based index of bottom row of the range.
     * @param endCol    0-based index of right column of the range.
     * @param valueType class instance of return cell values.
     * @param <T>       type of return cell values. Defined by value of <code>valueType</code>.
     * @return list of cell value lists. Returns empty list if specified range is empty. The class of return
     * cell values is defined by <code>valueType</code>. If the actual class of cell values is different from
     * <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and value of cells
     *                            cannot be cast to <code>T</code>.
     */
    public <T> List<List<T>> getRange(int startRow, int startCol, int endRow, int endCol, Class<T> valueType) {
        List<List<T>> data = new ArrayList<>();

        if (startRow < 0 || startCol < 0 || endRow < 0 || endCol < 0) {
            return data;
        }

        int r1 = Math.min(startRow, endRow);
        int r2 = Math.max(startRow, endRow);
        int c1 = Math.min(startCol, endCol);
        int c2 = Math.max(startCol, endCol);

        for (int row = r1; row <= r2; row++) {
            List<T> rowList = new ArrayList<>();
            for (int col = c1; col <= c2; col++) {
                rowList.add(getValue(row, col, valueType));
            }
            data.add(rowList);
        }
        return data;
    }

    /**
     * Sets given values to cells range of this sheet which starts from cell (top-left) defined by <code>startRef</code>.
     * <p>
     * The end (bottom-right) cell of the range is defined by sizes of given values.
     *
     * @param startRef reference string to top-left cell of the range. E.g. "A23".
     * @param values   list of values to set. Can be simple list of values or list of value lists. Simple list of values
     *                 is an equivalent to list with one list of values.
     */
    public void putRange(String startRef, List<?> values) {
        CellRef sRef = new CellRef(startRef);
        putRange(sRef.getRow(), sRef.getCol(), values);
    }

    /**
     * Sets given values to cells range of this sheet which starts from cell (top-left) defined by <code>startRow</code>
     * and <code>startCol</code>.
     * <p>
     * The end (bottom-right) cell of the range is defined by sizes of given values.
     *
     * @param startRow 0-based index of top row of the range.
     * @param startCol 0-based index of left column of the range.
     * @param values   list of values to set. Can be simple list of values or list of value lists. Simple list of values
     *                 is an equivalent to list with one list of values.
     */
    public void putRange(int startRow, int startCol, List<?> values) {
        if (values != null && values.size() > 0) {
            if (!(values.get(0) instanceof List)) {
                values = Collections.singletonList(values);
            }
            int rowIndex = startRow;
            for (Object rowList : values) {
                if (rowList instanceof List) {
                    eu.easyrpa.openframework.excel.Row row = getRow(rowIndex);
                    if (row == null) {
                        row = createRow(rowIndex);
                    }
                    row.putRange(startCol, (List<?>) rowList);
                    rowIndex++;
                }
            }
        }
    }

    /**
     * Merges cells range of this sheet by given reference.
     * <p>
     * <b>NOTICE:</b> If range intersects with an existing merged regions on this sheet all these regions will
     * be unmerged at first.
     *
     * @param regionRef reference string to cells range that needs to be merged. E.g. "A23:D50".
     * @return top-left cell of merged region that represents it.
     * @throws IllegalArgumentException if range contains fewer than 2 cells
     */
    public Cell mergeCells(String regionRef) {
        eu.easyrpa.openframework.excel.CellRange region = new eu.easyrpa.openframework.excel.CellRange(regionRef);
        return mergeCells(region.getFirstRow(), region.getFirstCol(),
                region.getLastRow(), region.getLastCol());
    }

    /**
     * Merges given cells range of this sheet.
     * <p>
     * <b>NOTICE:</b> If range intersects with an existing merged regions on this sheet all these regions will
     * be unmerged at first.
     *
     * @param region cells range that needs to be merged.
     * @return top-left cell of merged region that represents it.
     * @throws IllegalArgumentException if range contains fewer than 2 cells
     * @see eu.easyrpa.openframework.excel.CellRange
     */
    public Cell mergeCells(eu.easyrpa.openframework.excel.CellRange region) {
        return mergeCells(region.getFirstRow(), region.getFirstCol(),
                region.getLastRow(), region.getLastCol());
    }

    /**
     * Merges cells range of this sheet. The range is defined by given top-left and bottom-right
     * cell references.
     * <p>
     * <b>NOTICE:</b> If range intersects with an existing merged regions on this sheet all these regions will
     * be unmerged at first.
     *
     * @param startCellRef reference string to top-left cell of the range. E.g. "A23".
     * @param endCellRef   reference string to bottom-right cell of the range. E.g. "D50".
     * @return top-left cell of merged region that represents it.
     * @throws IllegalArgumentException if range contains fewer than 2 cells
     */
    public Cell mergeCells(String startCellRef, String endCellRef) {
        CellRef startRef = new CellRef(startCellRef);
        CellRef endRef = new CellRef(endCellRef);
        return mergeCells(startRef.getRow(), startRef.getCol(), endRef.getRow(), endRef.getCol());
    }

    /**
     * Merges cells range of this sheet. The range is defined by given top row, left column, bottom row
     * and right column indexes.
     * <p>
     * <b>NOTICE:</b> If range intersects with an existing merged regions on this sheet all these regions will
     * be unmerged at first.
     *
     * @param startRow 0-based index of top row of the range.
     * @param startCol 0-based index of left column of the range.
     * @param endRow   0-based index of bottom row of the range.
     * @param endCol   0-based index of right column of the range.
     * @return top-left cell of merged region that represents it.
     * @throws IllegalArgumentException if range contains fewer than 2 cells
     */
    public Cell mergeCells(int startRow, int startCol, int endRow, int endCol) {
        unmergeCells(startRow, startCol, endRow, endCol);
        CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);
        int regionIndex = getPoiSheet().addMergedRegion(region);
        if (regionIndex >= 0) {
            POIElementsCache.addMergedRegion(documentId, sheetIndex, regionIndex, region);
            Cell topLeftCell = new Cell(this, region.getFirstRow(), region.getFirstColumn());
            topLeftCell.getStyle().apply();
            return topLeftCell;
        }
        return null;
    }

    /**
     * Unmerges all merged regions on this sheet that intersects with given cells range.
     *
     * @param rangeRef reference string to cells range. E.g. "A23:D50".
     */
    public void unmergeCells(String rangeRef) {
        eu.easyrpa.openframework.excel.CellRange region = new eu.easyrpa.openframework.excel.CellRange(rangeRef);
        unmergeCells(region.getFirstRow(), region.getFirstCol(),
                region.getLastRow(), region.getLastCol());
    }

    /**
     * Unmerges all merged regions on this sheet that intersects with given cells range.
     *
     * @param range cells range.
     * @see eu.easyrpa.openframework.excel.CellRange
     */
    public void unmergeCells(eu.easyrpa.openframework.excel.CellRange range) {
        unmergeCells(range.getFirstRow(), range.getFirstCol(),
                range.getLastRow(), range.getLastCol());
    }

    /**
     * Unmerges all merged regions on this sheet that intersects with given cells range.
     *
     * @param startCellRef reference string to top-left cell of the range. E.g. "A23".
     * @param endCellRef   reference string to bottom-right cell of the range. E.g. "D50".
     */
    public void unmergeCells(String startCellRef, String endCellRef) {
        CellRef startRef = new CellRef(startCellRef);
        CellRef endRef = new CellRef(endCellRef);
        unmergeCells(startRef.getRow(), startRef.getCol(), endRef.getRow(), endRef.getCol());
    }

    /**
     * Unmerges all merged regions on this sheet that intersects with given cells range.
     *
     * @param startRow 0-based index of top row of the range.
     * @param startCol 0-based index of left column of the range.
     * @param endRow   0-based index of bottom row of the range.
     * @param endCol   0-based index of right column of the range.
     */
    public void unmergeCells(int startRow, int startCol, int endRow, int endCol) {

        CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);

        org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();

        final List<CellRangeAddress> regions = poiSheet.getMergedRegions();
        List<Integer> indicesToRemove = new ArrayList<>();
        for (int index = 0; index < regions.size(); index++) {
            if (regions.get(index).intersects(region)) {
                indicesToRemove.add(index);
            }
        }

        if (indicesToRemove.size() > 0) {
            poiSheet.removeMergedRegions(indicesToRemove);
            if (poiSheet instanceof XSSFSheet) {
                CTMergeCells mergedCells = ((XSSFSheet) poiSheet).getCTWorksheet().getMergeCells();
                mergedCells.setCount(mergedCells.getCount() - indicesToRemove.size());
            }
            POIElementsCache.removeMergedRegions(documentId, indicesToRemove);
        }
    }

    /**
     * Gets list of all existing merged regions on this sheet.
     *
     * @return list of cell ranges that represent existing merged regions.
     */
    public List<eu.easyrpa.openframework.excel.CellRange> getMergedRegions() {
        return getPoiSheet().getMergedRegions().stream()
                .map(r -> new eu.easyrpa.openframework.excel.CellRange(r.getFirstRow(), r.getFirstColumn(), r.getLastRow(), r.getLastColumn()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the row represented by given reference.
     *
     * @param rowRef reference to cell that belongs to necessary row. E.g. "A23" defines row with index 22.
     * @return high level Row object representing corresponding row or <code>null</code> if row is not defined.
     */
    public eu.easyrpa.openframework.excel.Row getRow(String rowRef) {
        return getRow(new CellRef(rowRef).getRow());
    }

    /**
     * Gets the row represented with given index.
     *
     * @param rowIndex 0-based index of necessary row.
     * @return high level Row object representing corresponding row or <code>null</code> if row is not defined.
     */
    public eu.easyrpa.openframework.excel.Row getRow(int rowIndex) {
        if (rowIndex >= 0) {
            org.apache.poi.ss.usermodel.Row row = getPoiSheet().getRow(rowIndex);
            return row != null ? new eu.easyrpa.openframework.excel.Row(this, rowIndex) : null;
        }
        return null;
    }

    /**
     * <p>Searches row on the sheet with given values in cells.</p>
     * <p>
     * Each specified value should correspond to some cell value on the row. To find necessary row it looks up cells
     * with value that matches given values. Row is found if if has cells match to all given values.
     * </p>
     * <p>
     * E.g. for given values <code>["Value1","Value2","Value3"]</code> this method should return first row that has cell
     * with value "Value1", cell with value "Value2" and cell with value "Value3".
     * </p>
     *
     * @param values the set of values to lookup. Exact matching is used during comparing.
     * @return high level Row object representing the first found row that have cells match to all given values
     * or <code>null</code> if nothing is found.
     */
    public eu.easyrpa.openframework.excel.Row findRow(String... values) {
        return findRow(MatchMethod.EXACT, values);
    }

    /**
     * <p>Searches row on the sheet with given values in cells.</p>
     * <p>
     * Each specified value should correspond to some cell value on the row. To find necessary row it looks up cells
     * with value that matches given values. Row is found if if has cells match to all given values.
     * </p>
     * <p>
     * E.g. for {@link MatchMethod#EXACT} and given values <code>["Value1","Value2","Value3"]</code> this method
     * should return first row that has cell with value "Value1", cell with value "Value2" and cell with value "Value3".
     * </p>
     *
     * @param matchMethod the way how the given values will be matched with value of cells. If <code>matchMethod</code>
     *                    is <code>null</code> the {@link MatchMethod#EXACT} is used as default.
     * @param values      the set of values to lookup.
     * @return high level Row object representing the first found row that have cells match to all given values
     * or <code>null</code> if nothing is found.
     * @see MatchMethod
     */
    public eu.easyrpa.openframework.excel.Row findRow(MatchMethod matchMethod, String... values) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        for (eu.easyrpa.openframework.excel.Row row : this) {
            boolean matchesFound = false;
            for (String key : values) {
                matchesFound = false;
                for (Cell cell : row) {
                    matchesFound = matchMethod.match(cell.getValue(String.class), key);
                    if (matchesFound) {
                        break;
                    }
                }
                if (!matchesFound) {
                    break;
                }
            }

            if (matchesFound) {
                return row;
            }
        }
        return null;
    }

    /**
     * Creates a new row at the given index and return the high level representation.
     *
     * @param rowIndex 0-based row index where row should be created.
     * @return high level Row object representing a row in the sheet
     */
    public eu.easyrpa.openframework.excel.Row createRow(int rowIndex) {
        getPoiSheet().createRow(rowIndex);
        return new eu.easyrpa.openframework.excel.Row(this, rowIndex);
    }

    /**
     * <p>Inserts rows into this sheet at given position and sets given values to their cells.</p>
     * <p>
     * <p>Rows can be inserted {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of row specified by
     * <code>startCellRef</code>.</p>
     * <p>
     * Amount of row to insert is defined by size of given values.
     *
     * @param method       defines position for insertion relatively to row specified by <code>startCellRef</code>.
     * @param startCellRef defines a row as position for insertion and top-left cell of inserted range where
     *                     <code>values</code> should be set.
     * @param values       list of values to set. Can be simple list of values or list of value lists. Simple
     *                     list of values is an equivalent to list with one list of values. The size of first
     *                     level list defines how many rows should be inserted.
     */
    public void insertRows(InsertMethod method, String startCellRef, List<?> values) {
        CellRef ref = new CellRef(startCellRef);
        insertRows(method, ref.getRow(), ref.getCol(), values);
    }

    /**
     * <p>Inserts rows into this sheet at given position and sets given values to their cells.</p>
     * <p>
     * <p>Rows can be inserted {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of row specified by
     * <code>rowPos</code>.</p>
     * <p>
     * Amount of row to insert is defined by size of given values.
     *
     * @param method   defines position for insertion relatively to row specified by <code>rowPos</code>.
     * @param rowPos   0-based index of row that defines position for insertion.
     * @param startCol 0-based index of column that defines top-left cell of inserted range where
     *                 <code>values</code> should be set.
     * @param values   list of values to set. Can be simple list of values or list of value lists. Simple
     *                 list of values is an equivalent to list with one list of values. The size of first
     *                 level list defines how many rows should be inserted.
     */
    public void insertRows(InsertMethod method, int rowPos, int startCol, List<?> values) {
        if (rowPos < 0 || startCol < 0 || values == null || values.isEmpty()) {
            return;
        }

        int rowIndex = method == null || method == InsertMethod.BEFORE ? rowPos : rowPos + 1;

        if (rowIndex > getLastRowIndex()) {
            putRange(rowIndex, startCol, values);

        } else {
            if (!(values.get(0) instanceof List)) {
                values = Collections.singletonList(values);
            }
            shiftRows(rowIndex, values.size());
            putRange(rowIndex, startCol, values);
        }
    }

    /**
     * Removes a row from this sheet and shifts all rows below to one position up.
     *
     * @param rowRef reference to cell that belongs to row to remove. E.g. "A23" defines row with index 22.
     */
    public void removeRow(String rowRef) {
        removeRow(new CellRef(rowRef).getRow());
    }

    /**
     * Removes a row from this sheet and shifts all rows below to one position up.
     *
     * @param row representing a row to remove.
     */
    public void removeRow(eu.easyrpa.openframework.excel.Row row) {
        removeRow(row.getIndex());
    }

    /**
     * Removes a row from this sheet and shifts all rows below to one position up.
     *
     * @param rowIndex 0-based index of row to remove.
     */
    public void removeRow(int rowIndex) {
        int lastRowIndex = getLastRowIndex();
        if (rowIndex < 0 || rowIndex > lastRowIndex) {
            return;
        }
        if (rowIndex == lastRowIndex) {
            org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();
            org.apache.poi.ss.usermodel.Row row = poiSheet.getRow(rowIndex);
            if (row != null) {
                poiSheet.removeRow(row);
            }
        } else {
            shiftRows(rowIndex + 1, -1);
        }
    }

    /**
     * Removes all cells contained in the specified row of this sheet.
     *
     * @param rowRef reference to cell that belongs to target row. E.g. "A23" defines row with index 22.
     */
    public void cleanRow(String rowRef) {
        cleanRow(new CellRef(rowRef).getRow());
    }

    /**
     * Removes all cells contained in the specified row of this sheet.
     *
     * @param row representing a target row.
     */
    public void cleanRow(eu.easyrpa.openframework.excel.Row row) {
        cleanRow(row.getIndex());
    }

    /**
     * Removes all cells contained in the specified row of this sheet.
     *
     * @param rowIndex 0-based index of target row.
     */
    public void cleanRow(int rowIndex) {
        org.apache.poi.ss.usermodel.Row row = getPoiSheet().getRow(rowIndex);
        if (row != null) {
            while (row.getLastCellNum() >= 0) {
                row.removeCell(row.getCell(row.getLastCellNum()));
            }
        }
    }

    /**
     * Gets index of the first defined row on the sheet.
     *
     * @return 0-based index of the first defined row on the sheet or <code>-1</code> if no rows exist.
     */
    public int getFirstRowIndex() {
        return getPoiSheet().getFirstRowNum();
    }

    /**
     * Gets index of the last row contained on the sheet.
     *
     * @return 0-based index of the last row contained on the sheet or <code>-1</code> if no rows exist.
     */
    public int getLastRowIndex() {
        return getPoiSheet().getLastRowNum();
    }

    /**
     * Gets column of this sheet represented by given reference.
     *
     * @param colRef reference to cell that belongs to necessary column. E.g. "C23" defines column with index 2.
     * @return high level object representing corresponding column or <code>null</code> if column is not defined.
     */
    public Column getColumn(String colRef) {
        return getColumn(new CellRef(colRef).getCol());
    }

    /**
     * Gets column of this sheet represented by given index.
     *
     * @param colIndex 0-based index of necessary column.
     * @return high level object representing corresponding column or <code>null</code> if column is not defined.
     */
    public Column getColumn(int colIndex) {
        return colIndex >= 0 ? new Column(this, colIndex) : null;
    }

    /**
     * Adds new column at the end of sheet and sets given values to it's cells starting from row with index 0.
     *
     * @param values list of values to set.
     */
    public void addColumn(List<?> values) {
        addColumn(0, values);
    }

    /**
     * Adds new column at the end of sheet and sets given values to it's cells starting from row defined
     * by <code>startRowRef</code>.
     *
     * @param startRowRef defines top row on the column starting from which <code>values</code> should be set.
     *                    E.g. "A23" defines row with index 22.
     * @param values      list of values to set.
     */
    public void addColumn(String startRowRef, List<?> values) {
        addColumn(new CellRef(startRowRef).getRow(), values);
    }

    /**
     * Adds new column at the end of sheet and sets given values to it's cells starting from row defined
     * by <code>startRow</code>.
     *
     * @param startRow 0-based index of row on the column starting from which <code>values</code> should be set.
     * @param values   list of values to set.
     */
    public void addColumn(int startRow, List<?> values) {
        List<List<?>> columnData = values.stream().map(Collections::singletonList).collect(Collectors.toList());
        putRange(startRow, getLastColumnIndex() + 1, columnData);
    }

    /**
     * <p>Inserts one column into this sheet at given position and sets given values to it's cells starting from
     * row defined by <code>startRowRef</code></p>
     * <p>
     * <p>Column can be inserted {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>columnRef</code>.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column insertion. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param method      defines position for insertion relatively to column specified by <code>columnRef</code>.
     * @param columnRef   defines a column as position for insertion. E.g. "C23" defines column with index 2.
     * @param startRowRef defines top row on the column starting from which <code>values</code> should be set.
     *                    E.g. "A23" defines row with index 22.
     * @param values      list of values to set.
     * @see ColumnInsert
     */
    public void insertColumn(InsertMethod method, String columnRef, String startRowRef, List<?> values) {
        CellRef cRef = new CellRef(columnRef);
        CellRef srRef = new CellRef(startRowRef);
        insertColumn(method, cRef.getCol(), srRef.getRow(), values);
    }

    /**
     * <p>Inserts one column into this sheet at given position and sets given values to it's cells starting from
     * row defined by <code>startRow</code></p>
     * <p>
     * <p>Column can be inserted {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>columnPos</code>.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column insertion. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param method    defines position for insertion relatively to column specified by <code>columnPos</code>.
     * @param columnPos 0-based index of column that defines position for insertion.
     * @param startRow  0-based index of row on the column starting from which <code>values</code> should be set.
     * @param values    list of values to set.
     * @see ColumnInsert
     */
    public void insertColumn(InsertMethod method, int columnPos, int startRow, List<?> values) {
        if (columnPos < 0 || startRow < 0 || values == null || values.isEmpty()) {
            return;
        }

        int columnIndex = method == null || method == InsertMethod.BEFORE ? columnPos : columnPos + 1;
        List<List<?>> columnData = values.stream().map(Collections::singletonList).collect(Collectors.toList());

        if (columnIndex <= getLastColumnIndex()) {
            getDocument().runScript(new ColumnInsert(new CellRef(getName(), -1, columnIndex)));
        }
        putRange(startRow, columnIndex, columnData);
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionRef</code>.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column moving. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param columnToMoveRef reference to cell that belongs to column to move. E.g. "C23" defines column with index 2.
     * @param method          defines position for moving relatively to column specified by <code>toPositionRef</code>.
     * @param toPositionRef   defines a column as position for moving. E.g. "C23" defines column with index 2.
     * @see ColumnsMove
     */
    public void moveColumn(String columnToMoveRef, InsertMethod method, String toPositionRef) {
        moveColumn(new CellRef(columnToMoveRef).getCol(), method, new CellRef(toPositionRef).getCol());
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionIndex</code>.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column moving. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param columnToMoveRef reference to cell that belongs to column to move. E.g. "C23" defines column with index 2.
     * @param method          defines position for moving relatively to column specified by <code>toPositionIndex</code>.
     * @param toPositionIndex 0-based index of column that defines position for moving.
     * @see ColumnsMove
     */
    public void moveColumn(String columnToMoveRef, InsertMethod method, int toPositionIndex) {
        moveColumn(new CellRef(columnToMoveRef).getCol(), method, toPositionIndex);
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionRef</code>.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column moving. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param columnToMoveIndex 0-based index of column to move.
     * @param method            defines position for moving relatively to column specified by <code>toPositionRef</code>.
     * @param toPositionRef     defines a column as position for moving. E.g. "C23" defines column with index 2.
     * @see ColumnsMove
     */
    public void moveColumn(int columnToMoveIndex, InsertMethod method, String toPositionRef) {
        moveColumn(columnToMoveIndex, method, new CellRef(toPositionRef).getCol());
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionIndex</code>.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column moving. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param columnToMoveIndex 0-based index of column to move.
     * @param method            defines position for moving relatively to column specified by <code>toPositionIndex</code>.
     * @param toPositionIndex   0-based index of column that defines position for moving.
     * @see ColumnsMove
     */
    public void moveColumn(int columnToMoveIndex, InsertMethod method, int toPositionIndex) {
        if (columnToMoveIndex < 0 || columnToMoveIndex > getLastColumnIndex() || toPositionIndex < 0) {
            return;
        }
        int pos = method == null || method == InsertMethod.BEFORE ? toPositionIndex : toPositionIndex + 1;
        if (pos != columnToMoveIndex) {
            eu.easyrpa.openframework.excel.CellRange columnsRange = new eu.easyrpa.openframework.excel.CellRange(getName(), -1, columnToMoveIndex, -1, columnToMoveIndex);
            getDocument().runScript(new ColumnsMove(columnsRange, new CellRef(-1, pos)));
        }
    }

    /**
     * <p>Removes a column from this sheet and shifts all followed columns to one position left.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column delete. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param colRef reference to cell that belongs to column to remove. E.g. "C23" defines column with index 2.
     * @see ColumnsDelete
     */
    public void removeColumn(String colRef) {
        removeColumn(new CellRef(colRef).getCol());
    }

    /**
     * <p>Removes a column from this sheet and shifts all followed columns to one position left.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column delete. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param column representing a column to remove.
     * @see ColumnsDelete
     */
    public void removeColumn(Column column) {
        removeColumn(column.getIndex());
    }

    /**
     * <p>Removes a column from this sheet and shifts all followed columns to one position left.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting to perform column delete. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param colIndex 0-based index of column to remove.
     * @see ColumnsDelete
     */
    public void removeColumn(int colIndex) {
        int lastColumnIndex = getLastColumnIndex();
        if (colIndex < 0 || colIndex > lastColumnIndex) {
            return;
        }
        if (colIndex == lastColumnIndex) {
            cleanColumn(colIndex);
        } else {
            eu.easyrpa.openframework.excel.CellRange columnsRange = new eu.easyrpa.openframework.excel.CellRange(getName(), -1, colIndex, -1, colIndex);
            getDocument().runScript(new ColumnsDelete(columnsRange));
        }
    }

    /**
     * Removes all cells contained in the specified column of this sheet.
     *
     * @param colRef reference to cell that belongs to target column. E.g. "C23" defines column with index 2.
     */
    public void cleanColumn(String colRef) {
        cleanColumn(new CellRef(colRef).getCol());
    }

    /**
     * Removes all cells contained in the specified column of this sheet.
     *
     * @param column representing target column.
     */
    public void cleanColumn(Column column) {
        cleanColumn(column.getIndex());
    }

    /**
     * Removes all cells contained in the specified column of this sheet.
     *
     * @param colIndex 0-based index of target column.
     */
    public void cleanColumn(int colIndex) {
        for (org.apache.poi.ss.usermodel.Row row : getPoiSheet()) {
            if (row != null) {
                org.apache.poi.ss.usermodel.Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    row.removeCell(cell);
                }
            }
        }
    }

    /**
     * <p>Sets the width of column. The width of column is a number of characters that can be displayed in a cell that is formatted
     * with the standard font (first font in the workbook).</p>
     *
     * <p>The maximum column width for an individual cell is 255 characters.</p>
     *
     * @param colRef reference to cell that belongs to target column. E.g. "C23" defines column with index 2.
     * @param width  the width as number of characters that can be displayed in a cell.
     * @throws IllegalArgumentException if width is more than 255.
     */
    public void setColumnWidth(String colRef, int width) {
        setColumnWidth(new CellRef(colRef).getCol(), width);
    }

    /**
     * <p>Sets the width of column. The width of column is a number of characters that can be displayed in a cell that is formatted
     * with the standard font (first font in the workbook).</p>
     *
     * <p>The maximum column width for an individual cell is 255 characters.</p>
     *
     * @param columnIndex 0-based index of target column.
     * @param width       the width as number of characters that can be displayed in a cell.
     * @throws IllegalArgumentException if width is more than 255.
     */
    public void setColumnWidth(int columnIndex, int width) {
        if (width > 255) {
            throw new IllegalArgumentException("Column width cannot be more than 255.");
        }
        getPoiSheet().setColumnWidth(columnIndex, width * 256);
    }

    /**
     * Gets index of the first defined column on the sheet. Column is defined if it has at least one
     * defined cell at its position.
     *
     * @return 0-based index of the first defined column on the sheet or <code>-1</code> if no columns exist.
     */
    public int getFirstColumnIndex() {
        org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();
        int firstColIndex = -1;
        int firstRowNum = poiSheet.getFirstRowNum();
        if (firstRowNum >= 0) {
            firstColIndex = Integer.MAX_VALUE;
            for (org.apache.poi.ss.usermodel.Row row : poiSheet) {
                firstColIndex = Math.min(firstColIndex, row.getFirstCellNum());
            }
        }
        return firstColIndex;
    }

    /**
     * Gets index of the last column contained on the sheet.
     *
     * @return 0-based index of the last column contained on the sheet or <code>-1</code> if no columns exist.
     */
    public int getLastColumnIndex() {
        org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();
        int lastColIndex = -1;
        int firstRowNum = poiSheet.getFirstRowNum();
        if (firstRowNum >= 0) {
            if (poiSheet instanceof XSSFSheetExt) {
                CellRangeAddress sheetDimension = ((XSSFSheetExt) poiSheet).getSheetDimension();
                lastColIndex = sheetDimension.getLastColumn();
            } else {
                for (org.apache.poi.ss.usermodel.Row row : poiSheet) {
                    lastColIndex = Math.max(lastColIndex, row.getLastCellNum());
                }
            }
        }
        return lastColIndex;
    }

    /**
     * Gets table located at this sheet.
     *
     * @param topLeftCellRef reference string to the top-left cell of table header. E.g. "A23".
     * @param recordType     class instance of records that this table works with.
     * @param <T>            class type of records that this table works with. Defined by value of
     *                       <code>recordType</code>.
     * @return object representing corresponding table.
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> getTable(String topLeftCellRef, Class<T> recordType) {
        CellRef ref = new CellRef(topLeftCellRef);
        return getTable(ref.getRow(), ref.getCol(), recordType);
    }

    /**
     * Gets table located at this sheet.
     *
     * @param headerTopRow  0-based index of table header top row.
     * @param headerLeftCol 0-based index of table left column.
     * @param recordType    class instance of records that this table works with.
     * @param <T>           class type of records that this table works with. Defined by value of
     *                      <code>recordType</code>.
     * @return object representing corresponding table.
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> getTable(int headerTopRow, int headerLeftCol, Class<T> recordType) {
        return new eu.easyrpa.openframework.excel.Table<T>(this, headerTopRow, headerLeftCol, headerTopRow, getLastColumnIndex(), recordType);
    }

    /**
     * Gets table located at this sheet.
     *
     * @param headerTopLeftCellRef     reference string to the top-left cell of table header. E.g. "A23".
     * @param headerBottomRightCellRef reference string to the bottom-right cell of table header. E.g. "A23".
     * @param recordType               class instance of records that this table works with.
     * @param <T>                      class type of records that this table works with. Defined by value of
     *                                 <code>recordType</code>.
     * @return object representing corresponding table.
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> getTable(String headerTopLeftCellRef, String headerBottomRightCellRef, Class<T> recordType) {
        CellRef tlRef = new CellRef(headerTopLeftCellRef);
        CellRef brRef = new CellRef(headerBottomRightCellRef);
        return getTable(tlRef.getRow(), tlRef.getCol(), brRef.getRow(), brRef.getCol(), recordType);
    }

    /**
     * Gets table located at this sheet.
     *
     * @param headerTopRow    0-based index of table header top row.
     * @param headerLeftCol   0-based index of table left column.
     * @param headerBottomRow 0-based index of table header bottom row.
     * @param headerRightCol  0-based index of table right column.
     * @param recordType      class instance of records that this table works with.
     * @param <T>             class type of records that this table works with. Defined by value of
     *                        <code>recordType</code>.
     * @return object representing corresponding table.
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> getTable(int headerTopRow, int headerLeftCol,
                                                                int headerBottomRow, int headerRightCol, Class<T> recordType) {
        return new eu.easyrpa.openframework.excel.Table<T>(this, headerTopRow, headerLeftCol, headerBottomRow, headerRightCol, recordType);
    }

    /**
     * Searches table on the sheet with given keywords in header.
     *
     * @param recordType class instance of records that this table works with.
     * @param keywords   - keywords to localize table header. Exact matching is used during comparing keywords with
     *                   table column names.
     * @return object representing the table or <code>null</code> if nothing was found.
     * @see #findRow(MatchMethod, String...)
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> findTable(Class<T> recordType, String... keywords) {
        return findTable(recordType, MatchMethod.EXACT, keywords);
    }

    /**
     * Searches table on the sheet with given keywords in header.
     *
     * @param recordType  class instance of records that this table works with.
     * @param matchMethod the way how given keywords will be matched with table column names. If <code>matchMethod</code>
     *                    is <code>null</code> the {@link MatchMethod#EXACT} is used as default.
     * @param keywords    keywords to localize table header
     * @return object representing the table or <code>null</code> if nothing was found.
     * @see #findRow(MatchMethod, String...)
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> findTable(Class<T> recordType, MatchMethod matchMethod, String... keywords) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        eu.easyrpa.openframework.excel.Row headerRow = findRow(matchMethod, keywords);
        if (headerRow != null) {
            int topRow = Integer.MAX_VALUE, leftCol = Integer.MAX_VALUE;
            int botRow = -1, rightCol = -1;
            for (Cell cell : headerRow) {
                CellRange region = cell.getMergedRegion();
                if (region != null) {
                    topRow = Math.min(topRow, region.getFirstRow());
                    leftCol = Math.min(leftCol, region.getFirstCol());
                    botRow = Math.max(botRow, region.getLastRow());
                    rightCol = Math.max(rightCol, region.getLastCol());
                } else {
                    topRow = Math.min(topRow, cell.getRowIndex());
                    leftCol = Math.min(leftCol, cell.getColumnIndex());
                    botRow = Math.max(botRow, cell.getRowIndex());
                    rightCol = Math.max(rightCol, cell.getColumnIndex());
                }
            }
            return getTable(topRow, leftCol, botRow, rightCol, recordType);
        }
        return null;
    }

    /**
     * <p>Inserts table into this sheet at position starting from cell "A1" (0 - row index and 0 - column index)</p>
     *
     * <p>Table header specification and cell styles that determine how the table will looks like should be
     * specified using annotations in the class of record. Otherwise nothing will be inserted.</p>
     *
     * @param records list of records to be inserted as table content.
     * @param <T>     class of records. This class should specify the table header specification and cell styles
     *                using annotations {@link ExcelColumn}.
     * @return object representing the inserted table.
     * @see ExcelColumn
     * @see ExcelTable
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> insertTable(List<T> records) {
        return insertTable(0, 0, records);
    }

    /**
     * <p>Inserts table into this sheet at position starting from cell defined by
     * <code>topLeftCellRef</code> (top-left cell of the table)</p>
     *
     * <p>Table header specification and cell styles that determine how the table will looks like should be
     * specified using annotations in the class of record. Otherwise nothing will be inserted.</p>
     *
     * @param topLeftCellRef reference to cell that defines top-left cell of the table (place where table
     *                       should be inserted). E.g. "A23".
     * @param records        list of records to be inserted as table content.
     * @param <T>            class of records. This class should specify the table header specification and cell styles
     *                       using annotations {@link ExcelColumn}.
     * @return object representing the inserted table.
     * @see ExcelColumn
     * @see ExcelTable
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> insertTable(String topLeftCellRef, List<T> records) {
        CellRef ref = new CellRef(topLeftCellRef);
        return insertTable(ref.getRow(), ref.getCol(), records);
    }

    /**
     * <p>Inserts table into this sheet at position starting from cell defined by
     * <code>startRow</code> and <code>startCol</code> (top-left cell of the table)</p>
     *
     * <p>Table header specification and cell styles that determine how the table will looks like should be
     * specified using annotations in the class of record. Otherwise nothing will be inserted.</p>
     *
     * @param startRow 0-based index of row that defines top-left cell of the table (place where table should be
     *                 inserted).
     * @param startCol 0-based index of column that defines top-left cell of the table (place where table should be
     *                 inserted).
     * @param records  list of records to be inserted as table content.
     * @param <T>      class of records. This class should specify the table header specification and cell styles
     *                 using annotations {@link ExcelColumn}.
     * @return object representing the inserted table.
     * @see ExcelColumn
     * @see ExcelTable
     */
    public <T> eu.easyrpa.openframework.excel.Table<T> insertTable(int startRow, int startCol, List<T> records) {
        return startRow >= 0 && startCol >= 0 && records != null && records.size() > 0
                ? new Table<T>(this, startRow, startCol, records)
                : null;
    }

    /**
     * <p>Exports this sheet to PDF file.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting. MS Excel application MUST be installed on the machine
     * where RPA process that uses this function is working. Otherwise {@link VBScriptExecutionException}
     * will be thrown.
     *
     * @param pdfFilePath path to output PDF file on local file system where this sheet should be exported.
     * @throws VBScriptExecutionException with error description if exporting failed.
     */
    public void exportToPDF(String pdfFilePath) {
        parent.runScript(new ExportToPDF(getName(), pdfFilePath));
    }

    /**
     * <p>Adds image into this sheet at given position.</p>
     * <br>
     * <b>IMPORTANT:</b>  Only adding of PNG, JPEG, EMF and WMF images is supported. In case of other type of images
     * {@link IllegalArgumentException} will be throws.
     *
     * @param pathToImage path to necessary image on local file system or within resources folder of
     *                    RPA process module.
     * @param positionRef reference to cell that defines top-left cell of the image (place where image should be
     *                    inserted). E.g. "A23".
     * @throws IllegalArgumentException if <code>pathToImage</code> is null or refers to unsupported image type.
     */
    public void addImage(String pathToImage, String positionRef) {
        addImage(pathToImage, positionRef, null);
    }

    /**
     * <p>Adds image into this sheet at given position.</p>
     * <br>
     * <b>IMPORTANT:</b>  Only adding of PNG, JPEG, EMF and WMF images is supported. In case of other type of images
     * {@link IllegalArgumentException} will be throws.
     *
     * @param pathToImage path to necessary image on local file system or within resources folder of
     *                    RPA process module.
     * @param fromCellRef reference to cell that defines top-left cell of the image (place where image should be
     *                    inserted). E.g. "A23".
     * @param toCellRef   reference to cell that defines bottom-right cell of the image (in conjunction with
     *                    <code>fromCellRef</code> determines the image width and height on the sheet). E.g. "A23".
     * @throws IllegalArgumentException if <code>pathToImage</code> is null or refers to unsupported image type.
     */
    public void addImage(String pathToImage, String fromCellRef, String toCellRef) {
        File imageFile = FilePathUtils.getFile(pathToImage);
        if (imageFile == null) {
            throw new IllegalArgumentException("Image path is not specified.");
        }
        try {
            addImage(new FileInputStream(imageFile), fromCellRef, toCellRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Adds image into this sheet at given position.</p>
     * <br>
     * <b>IMPORTANT:</b>  Only adding of PNG, JPEG, EMF and WMF images is supported. In case of other type of images
     * {@link IllegalArgumentException} will be throws.
     *
     * @param imageIS     input stream with content of the image.
     * @param positionRef reference to cell that defines top-left cell of the image (place where image should be
     *                    inserted). E.g. "A23".
     * @throws IllegalArgumentException if <code>imageIS</code> contains unsupported image type.
     */
    public void addImage(InputStream imageIS, String positionRef) {
        addImage(imageIS, positionRef, null);
    }

    /**
     * <p>Adds image into this sheet at given position.</p>
     * <br>
     * <b>IMPORTANT:</b>  Only adding of PNG, JPEG, EMF and WMF images is supported. In case of other type of images
     * {@link IllegalArgumentException} will be throws.
     *
     * @param imageIS     input stream with content of the image.
     * @param fromCellRef reference to cell that defines top-left cell of the image (place where image should be
     *                    inserted). E.g. "A23".
     * @param toCellRef   reference to cell that defines bottom-right cell of the image (in conjunction with
     *                    <code>fromCellRef</code> determines the image width and height on the sheet). E.g. "A23".
     * @throws IllegalArgumentException if <code>imageIS</code> contains unsupported image type.
     */
    public void addImage(InputStream imageIS, String fromCellRef, String toCellRef) {
        if (imageIS == null) {
            throw new IllegalArgumentException("Image input stream cannot be null.");
        }
        byte[] imageData;
        int imageFormat;
        try {
            imageData = IOUtils.toByteArray(imageIS);
            final FileMagic fm = FileMagic.valueOf(imageData);
            if (fm == FileMagic.PNG) {
                imageFormat = Workbook.PICTURE_TYPE_PNG;
            } else if (fm == FileMagic.JPEG) {
                imageFormat = Workbook.PICTURE_TYPE_JPEG;
            } else if (fm == FileMagic.EMF) {
                imageFormat = Workbook.PICTURE_TYPE_EMF;
            } else if (fm == FileMagic.WMF) {
                imageFormat = Workbook.PICTURE_TYPE_WMF;
            } else {
                throw new IllegalArgumentException("Unknown image file format. Only JPEG, PNG, EMF and WMF are supported.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();
        Workbook poiWb = poiSheet.getWorkbook();
        int pictureIdx = poiWb.addPicture(imageData, imageFormat);
        Drawing<?> drawing = poiSheet.createDrawingPatriarch();

        ClientAnchor anchor = poiWb.getCreationHelper().createClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

        CellRef fromRef = new CellRef(fromCellRef);
        anchor.setRow1(Math.max(fromRef.getRow(), 0));
        anchor.setCol1(Math.max(fromRef.getCol(), 0));
        if (toCellRef != null) {
            CellRef toRef = new CellRef(toCellRef);
            anchor.setRow2(Math.max(toRef.getRow(), 0));
            anchor.setCol2(Math.max(toRef.getCol(), 0));
        }

        Picture picture = drawing.createPicture(anchor, pictureIdx);
        if (toCellRef == null) {
            picture.resize();
        }
    }

    /**
     * <p>Adds Pivot Table with given parameters into this sheet.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param ptParams Pivot Table parameters.
     * @see PivotTableParams
     * @see PivotTableScript
     */
    public void addPivotTable(PivotTableParams ptParams) {
        if (ptParams == null) {
            return;
        }
        ptParams.setSheetName(getName());
        ptParams.checkPosition();
        getDocument().runScript(new PivotTableScript(PivotTableScript.ScriptAction.CREATE, ptParams));
    }

    /**
     * <p>Updates caches of Pivot Table with given name on this sheet.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param pTableName name of existing Pivot Table to update.
     * @see PivotTableScript
     */
    public void updatePivotTable(String pTableName) {
        updatePivotTable(PivotTableParams.create(pTableName));
    }

    /**
     * <p>Updates caches and parameters of existing Pivot Table on this sheet.</p>
     * <br>
     * <b>IMPORTANT:</b> This function uses VB scripting. MS Excel application MUST be
     * installed on the machine where RPA process that uses this function is working. Otherwise
     * {@link VBScriptExecutionException} will be thrown.
     *
     * @param ptParams Pivot Table parameters that refers to existing Pivot Table by given name and optionally
     *                 specifies new settings that should be applied to it.
     * @see PivotTableParams
     * @see PivotTableScript
     */
    public void updatePivotTable(PivotTableParams ptParams) {
        if (ptParams == null) {
            return;
        }
        ptParams.setSheetName(getName());
        getDocument().runScript(new PivotTableScript(PivotTableScript.ScriptAction.UPDATE, ptParams));
    }

    /**
     * Gets format of this sheet. Format includes information about style of each cell on the sheet, merged regions and
     * regions with data validation constraints.
     *
     * @return object representing the format of this sheet.
     * @see ExcelCellsFormat
     */
    public ExcelCellsFormat getFormat() {
        return new ExcelCellsFormat(this);
    }

    /**
     * Gets format of given cells range of this sheet. Format includes information about style of each cell in
     * the range, merged regions and regions with data validation constraints.
     *
     * @param startCellRef reference string to top-left cell of the range. E.g. "A23".
     * @param endCellRef   reference string to bottom-right cell of the range. E.g. "D50".
     * @return object representing the format of given cells range of this sheet.
     * @see ExcelCellsFormat
     */
    public ExcelCellsFormat getFormat(String startCellRef, String endCellRef) {
        CellRef startRef = new CellRef(startCellRef);
        CellRef endRef = new CellRef(endCellRef);
        return new ExcelCellsFormat(this, startRef.getRow(), startRef.getCol(), endRef.getRow(), endRef.getCol());
    }

    /**
     * Gets format of given cells range of this sheet. Format includes information about style of each cell in
     * the range, merged regions and regions with data validation constraints.
     *
     * @param firstRow 0-based index of top row of the range.
     * @param firstCol 0-based index of left column of the range.
     * @param lastRow  0-based index of bottom row of the range.
     * @param lastCol  0-based index of right column of the range.
     * @return object representing the format of given cells range of this sheet.
     * @see ExcelCellsFormat
     */
    public ExcelCellsFormat getFormat(int firstRow, int firstCol, int lastRow, int lastCol) {
        return new ExcelCellsFormat(this, firstRow, firstCol, lastRow, lastCol);
    }

    /**
     * Removes all rows with cells on this sheet.
     */
    public void clear() {
        org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();
        int rowNum;
        while ((rowNum = poiSheet.getLastRowNum()) > 0) {
            org.apache.poi.ss.usermodel.Row row = poiSheet.getRow(rowNum);
            if (row != null) {
                poiSheet.removeRow(row);
            }
        }
    }

    /**
     * Moves this sheet to a new position.
     *
     * @param newPos 0-based index where this sheet should be moved.
     */
    public void moveTo(int newPos) {
        parent.getWorkbook().setSheetOrder(getName(), newPos);
        sheetIndex = newPos;
    }


    /**
     * Changes the name of this sheet.
     *
     * @param newName a new name of this sheet.
     */
    public void rename(String newName) {
        parent.getWorkbook().setSheetName(sheetIndex, newName);
    }

    /**
     * Clones this sheet and place it to the end of Excel document.
     *
     * @param clonedSheetName the name that should be set for cloned sheet.
     * @return object representing the cloned sheet.
     */
    public Sheet cloneAs(String clonedSheetName) {
        Workbook workbook = parent.getWorkbook();
        org.apache.poi.ss.usermodel.Sheet clone = workbook.cloneSheet(sheetIndex);
        int clonedSheetIndex = workbook.getSheetIndex(clone);
        workbook.setSheetName(clonedSheetIndex, clonedSheetName);
        return new Sheet(parent, clonedSheetIndex);
    }

    /**
     * Copies the content of this sheet to another sheet with the same format. Destination sheet can be located
     * in another Excel document.
     *
     * @param destSheet object representing destination sheet.
     */
    public void copy(Sheet destSheet) {
        copy(destSheet, true);
    }

    /**
     * Copies the content of this sheet to another sheet. Destination sheet can be located in another Excel document.
     *
     * @param destSheet  object representing destination sheet.
     * @param copyFormat specifies whether it's necessary to copy the format of this sheet (styles, merged regions etc.).
     */
    public void copy(Sheet destSheet, boolean copyFormat) {
        int rowsCount = getLastRowIndex() + 1;
        int columnsCount = getLastColumnIndex() + 1;

        destSheet.clear();
        destSheet.putRange(0, 0, getRange(0, 0, rowsCount - 1, columnsCount - 1));

        org.apache.poi.ss.usermodel.Sheet srcPoiSheet = getPoiSheet();
        org.apache.poi.ss.usermodel.Sheet destPoiSheet = destSheet.getPoiSheet();

        //Copy XSSF tables
        if (srcPoiSheet instanceof XSSFSheet && destPoiSheet instanceof XSSFSheet) {
            XSSFSheet srcXSSFSheet = (XSSFSheet) srcPoiSheet;
            XSSFSheet destXSSFSheet = (XSSFSheet) destPoiSheet;

            for (XSSFTable srcXSSFTable : srcXSSFSheet.getTables()) {
                XSSFTable destXSSFTable = destXSSFSheet.createTable(srcXSSFTable.getArea());
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    srcXSSFTable.writeTo(baos);
                    destXSSFTable.readFrom(new ByteArrayInputStream(baos.toByteArray()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Copy images
        Drawing<?> srcDrawing = srcPoiSheet.getDrawingPatriarch();
        if (srcDrawing != null) {
            Workbook destWb = destPoiSheet.getWorkbook();
            Drawing<?> destDrawing = destPoiSheet.createDrawingPatriarch();
            for (Shape shape : srcDrawing) {
                if (shape instanceof Picture) {
                    Picture srcPicture = (Picture) shape;
                    int pictureIdx = destWb.addPicture(srcPicture.getPictureData().getData(), srcPicture.getPictureData().getPictureType());
                    destDrawing.createPicture(srcPicture.getClientAnchor(), pictureIdx);
                }
            }
        }

        if (copyFormat) {
            getFormat().applyTo(destSheet);

            //Copy column widths and row heights
            short defaultRowHeight = srcPoiSheet.getDefaultRowHeight();
            if (defaultRowHeight != destPoiSheet.getDefaultRowHeight()) {
                destPoiSheet.setDefaultRowHeight(defaultRowHeight);
            }
            int defaultColWidth = srcPoiSheet.getDefaultColumnWidth();
            if (defaultColWidth != destPoiSheet.getDefaultColumnWidth()) {
                destPoiSheet.setDefaultColumnWidth(defaultColWidth);
            }
            for (int i = 0; i < columnsCount; i++) {
                int width = srcPoiSheet.getColumnWidth(i);
                if (width != defaultColWidth) {
                    destPoiSheet.setColumnWidth(i, width);
                }
            }
            for (int i = 0; i < rowsCount; i++) {
                org.apache.poi.ss.usermodel.Row srcRow = srcPoiSheet.getRow(i);
                org.apache.poi.ss.usermodel.Row destRow = destPoiSheet.getRow(i);
                if (srcRow != null && destRow != null) {
                    short height = srcRow.getHeight();
                    if (height != defaultRowHeight) {
                        destRow.setHeight(height);
                    }
                }
            }

            //Copy tab color
            if (srcPoiSheet instanceof XSSFSheet && destPoiSheet instanceof XSSFSheet) {
                XSSFSheet srcXSSFSheet = (XSSFSheet) srcPoiSheet;
                XSSFSheet destXSSFSheet = (XSSFSheet) destPoiSheet;
                XSSFColor tabColor = srcXSSFSheet.getTabColor();
                if (tabColor != null) {
                    destXSSFSheet.setTabColor(tabColor);
                }
            }
        }
    }

    /**
     * <p>Returns an iterator of objects representing existing rows on this sheet. Meaning it won't iterate
     * over undefined rows.</p>
     * <p>This method allows using of this sheet object in "for" loop:</p>
     * <pre>
     *     for(Row row: sheet){
     *         int rowIndex = row.getIndex();
     *         ...
     *     }
     * </pre>
     *
     * @return on iterator of objects representing existing rows on this sheet.
     */
    @Override
    public Iterator<eu.easyrpa.openframework.excel.Row> iterator() {
        return new RowIterator(getPoiSheet());
    }

    /**
     * Returns underlay POI object representing this sheet. This object can be used directly if some specific
     * POI functionality is necessary within RPA process.
     *
     * @return Apache POI object representing this sheet.
     */
    public org.apache.poi.ss.usermodel.Sheet getPoiSheet() {
        return POIElementsCache.getPoiSheet(documentId, sheetIndex);
    }

    /**
     * Shift rows of this sheet up or down.
     *
     * @param startRow  0-based index of top row that needs to be shifted.
     * @param rowsCount amount of rows that needs to be shifted. If this value is positive(+) the shifting is
     *                  performed down (rows inserting), if negative (-) - up (rows deleting).
     */
    private void shiftRows(int startRow, int rowsCount) {
        org.apache.poi.ss.usermodel.Sheet poiSheet = getPoiSheet();
        int endRow = poiSheet.getLastRowNum();

        if (startRow < 0 || startRow > endRow) {
            return;
        }

        poiSheet.shiftRows(startRow, endRow, rowsCount);

        //Rows have been shifted and their positions changed. We need to cleanup
        // caches to get actual poi elements.
        POIElementsCache.clearRowsAndCellsCache(documentId);

        // Shift data validation ranges separately since by default shifting of rows
        // doesn't affect position of data validation
        List<? extends DataValidation> dataValidations = poiSheet.getDataValidations();

        try {
            //Cleanup all data validations
            if (poiSheet instanceof XSSFSheet) {
                ((XSSFSheet) poiSheet).getCTWorksheet().unsetDataValidations();
            } else if (poiSheet instanceof SXSSFSheet) {
                XSSFSheet xssfSheet = TypeUtils.getFieldValue(poiSheet, "_sh");
                xssfSheet.getCTWorksheet().unsetDataValidations();
            } else if (poiSheet instanceof HSSFSheet) {
                TypeUtils.setFieldValue(((HSSFSheet) poiSheet).getSheet(), "_dataValidityTable", null);
            }
        } catch (Exception e) {
            // do nothing
        }

        for (DataValidation dv : dataValidations) {
            CellRangeAddressList regions = dv.getRegions();
            for (int i = 0; i < regions.countRanges(); i++) {
                CellRangeAddress dvRegion = regions.getCellRangeAddress(i);
                if (dvRegion.getFirstRow() >= startRow) {
                    dvRegion.setFirstRow(dvRegion.getFirstRow() + rowsCount);
                }
                if (dvRegion.getLastRow() >= startRow) {
                    dvRegion.setLastRow(dvRegion.getLastRow() + rowsCount);
                }
            }
            poiSheet.addValidationData(poiSheet.getDataValidationHelper().createValidation(dv.getValidationConstraint(), dv.getRegions()));
        }
    }

    /**
     * Rows iterator. Allows iteration over all existing rows of the sheet using "for" loop.
     */
    private class RowIterator implements Iterator<eu.easyrpa.openframework.excel.Row> {

        private org.apache.poi.ss.usermodel.Sheet poiSheet;
        private int index = 0;
        private int rowsCount;

        public RowIterator(org.apache.poi.ss.usermodel.Sheet poiSheet) {
            this.poiSheet = poiSheet;
            this.rowsCount = poiSheet.getLastRowNum() + 1;
        }

        @Override
        public boolean hasNext() {
            if (index < rowsCount) {
                org.apache.poi.ss.usermodel.Row nextRow = poiSheet.getRow(index);
                while (nextRow == null && index + 1 < rowsCount) {
                    nextRow = poiSheet.getRow(++index);
                }
                return nextRow != null;
            }
            return false;
        }

        @Override
        public eu.easyrpa.openframework.excel.Row next() {
            return new eu.easyrpa.openframework.excel.Row(Sheet.this, index++);
        }
    }
}
