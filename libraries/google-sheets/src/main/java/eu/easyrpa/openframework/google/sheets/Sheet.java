package eu.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.model.*;
import eu.easyrpa.openframework.google.sheets.annotations.GSheetColumn;
import eu.easyrpa.openframework.google.sheets.annotations.GSheetTable;
import eu.easyrpa.openframework.google.sheets.constants.InsertMethod;
import eu.easyrpa.openframework.google.sheets.constants.MatchMethod;
import eu.easyrpa.openframework.google.sheets.internal.SpreadsheetUpdateRequestsBatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents specific sheet of Spreadsheet document and provides functionality to work with it and its content.
 */
public class Sheet implements Iterable<Row> {

    /**
     * Reference to parent Spreadsheet document.
     */
    private SpreadsheetDocument parent;

    /**
     * Index of this sheet within parent Spreadsheet document.
     */
    private int sheetIndex;

    /**
     * Creates a new Sheet instance.
     *
     * @param parent     reference to parent Spreadsheet document.
     * @param sheetIndex index of the sheet within parent Spreadsheet document.
     */
    public Sheet(SpreadsheetDocument parent, int sheetIndex) {
        this.sheetIndex = sheetIndex;
        this.parent = parent;
    }

    /**
     * Gets parent Spreadsheet document.
     *
     * @return parent Spreadsheet document.
     */
    public SpreadsheetDocument getDocument() {
        return parent;
    }

    /**
     * Gets index of this sheet within parent Spreadsheet document.
     *
     * @return index of this sheet within parent Spreadsheet document.
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
        return getGSheet().getProperties().getTitle();
    }

    /**
     * Gets unique identifier of this sheet.
     *
     * @return unique identifier of this sheet.
     */
    public int getId() {
        return getGSheet().getProperties().getSheetId();
    }

    /**
     * Gets the cell represented by given reference.
     *
     * @param cellRef reference to necessary cell. E.g. "A23".
     * @return instance of corresponding cell or {@code null} if cell is not defined.
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
     * @return instance of corresponding cell or {@code null} if cell is not defined.
     */
    public Cell getCell(int rowIndex, int colIndex) {
        Row row = getRow(rowIndex);
        return row != null ? row.getCell(colIndex) : null;
    }

    /**
     * Searches cell with given value on the sheet.
     *
     * @param value the value to lookup. Exact matching is used during comparing.
     * @return instance of the first found cell with given value or {@code null} if nothing is found.
     */
    public Cell findCell(String value) {
        return findCell(MatchMethod.EXACT, value);
    }

    /**
     * Searches cell with given value on the sheet.
     *
     * @param matchMethod the way how the given value will be matched with value of cell.
     * @param value       the value to lookup.
     * @return instance of the first found cell with given value or {@code null} if nothing is found.
     * @see MatchMethod
     */
    public Cell findCell(MatchMethod matchMethod, String value) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        for (Row row : this) {
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
     * @return value of corresponding cell or {@code null} if nothing is found. The actual class of value
     * is depend on cell type. Can be returned {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue(String cellRef) {
        CellRef ref = new CellRef(cellRef);
        return getValue(ref.getRow(), ref.getCol(), Object.class);
    }

    /**
     * Gets the value of this sheet cell by given cell reference and converts it to the type
     * specified by {@code valueType}.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell value to corresponding type or return {@code null} if
     * the conversion fails.
     * <p>
     * For other types it performs simple type casting of cell value to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param cellRef   reference string to necessary cell. E.g. "A23".
     * @param valueType class instance of return value.
     * @param <T>       type of return value. Defined by value of {@code valueType}.
     * @return value of corresponding cell or {@code null} if nothing is found. The class of return
     * value is defined by {@code valueType}. If the actual class of cell value is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
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
     * @return value of corresponding cell or {@code null} if nothing is found. The actual class of value
     * is depend on cell type. Can be returned {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue(int rowIndex, int colIndex) {
        return getValue(rowIndex, colIndex, Object.class);
    }

    /**
     * Gets the value of this sheet cell by given row and column indexes. The return value is automatically
     * converted to the type specified by {@code valueType}.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell value to corresponding type or return {@code null} if
     * the conversion fails.
     * <p>
     * For other types it performs simple type casting of cell value to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param rowIndex  0-based row index of necessary cell.
     * @param colIndex  0-based column index of necessary cell.
     * @param valueType class instance of return value.
     * @param <T>       type of return value. Defined by value of {@code valueType}.
     * @return value of corresponding cell or {@code null} if nothing is found. The class of return
     * value is defined by {@code valueType}. If the actual class of cell value is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
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
            final int rowsAmountToAppend = rowIndex + 1 - getMaxRowsCount();
            parent.batchUpdate(r -> {
                if (rowsAmountToAppend > 0) {
                    appendRowsMetadata(rowsAmountToAppend);
                    r.addAppendRowsRequest(rowsAmountToAppend, getId());
                }
                Row row = getRow(rowIndex);
                if (row == null) {
                    row = createRow(rowIndex);
                }
                row.setValue(colIndex, value);
            });
        }
    }

    /**
     * Gets values of all cells on the sheet. It's an equivalent to getting of range between top-left and
     * bottom-right cells of this sheet.
     *
     * @return list of cell value lists. Returns empty list if sheet is empty. The actual class of values in lists
     * depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
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
     * depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public List<List<Object>> getRange(String startRef, String endRef) {
        return getRange(startRef, endRef, Object.class);
    }

    /**
     * Gets values of cells range on this sheet and converts them to the type specified by {@code valueType}.
     * The range is defined by given top-left and bottom-right cell references.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell values to corresponding type or {@code null} if the conversion
     * fails.
     * <p>
     * For other types it performs simple type casting of cell values to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param startRef  reference string to top-left cell of the range. E.g. "A23".
     * @param endRef    reference string to bottom-right cell of the range. E.g. "D50".
     * @param valueType class instance of return cell values.
     * @param <T>       type of return cell values. Defined by value of {@code valueType}.
     * @return list of cell value lists. Returns empty list if specified range is empty. The class of return
     * cell values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
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
     * depend on cell types. Can be {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public List<List<Object>> getRange(int startRow, int startCol, int endRow, int endCol) {
        return getRange(startRow, startCol, endRow, endCol, Object.class);
    }

    /**
     * Gets values of cells range on this sheet and converts them to the type specified by {@code valueType}.
     * The range is defined by given top row, left column, bottom row and right column indexes.
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
     * @param startCol  0-based index of left column of the range.
     * @param endRow    0-based index of bottom row of the range.
     * @param endCol    0-based index of right column of the range.
     * @param valueType class instance of return cell values.
     * @param <T>       type of return cell values. Defined by value of {@code valueType}.
     * @return list of cell value lists. Returns empty list if specified range is empty. The class of return
     * cell values is defined by {@code valueType}. If the actual class of cell values is different from
     * {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
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
            final List<?> data = values.get(0) instanceof List ? values : Collections.singletonList(values);
            final int rowsAmountToAppend = startRow + values.size() - getMaxRowsCount();
            parent.batchUpdate(r -> {
                if (rowsAmountToAppend > 0) {
                    appendRowsMetadata(rowsAmountToAppend);
                    r.addAppendRowsRequest(rowsAmountToAppend, getId());
                }
                int rowIndex = startRow;
                for (Object rowList : data) {
                    if (rowList instanceof List) {
                        Row row = getRow(rowIndex);
                        if (row == null) {
                            row = createRow(rowIndex);
                        }
                        row.putRange(startCol, (List<?>) rowList);
                        rowIndex++;
                    }
                }
            });
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
     */
    public Cell mergeCells(String regionRef) {
        CellRange region = new CellRange(regionRef);
        return mergeCells(region.getFirstRow(), region.getFirstCol(),
                region.getLastRow(), region.getLastCol());
    }

    /**
     * Merges given cells range of this sheet.
     * <p>
     * <b>NOTICE:</b> If range intersects with an existing merged regions on this sheet all these regions will
     * be unmerged at first.
     *
     * @param region {@link CellRange} that needs to be merged.
     * @return top-left cell of merged region that represents it.
     */
    public Cell mergeCells(CellRange region) {
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
     */
    public Cell mergeCells(int startRow, int startCol, int endRow, int endCol) {
        final com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        GridRange regionToMerge = new GridRange()
                .setSheetId(gSheet.getProperties().getSheetId())
                .setStartRowIndex(startRow)
                .setStartColumnIndex(startCol)
                .setEndRowIndex(endRow + 1)
                .setEndColumnIndex(endCol + 1);

        parent.batchUpdate(r -> {
            unmergeCells(startRow, startCol, endRow, endCol);
            r.addMergeCellsRequest(regionToMerge);
        });

        List<GridRange> mergedRegions = gSheet.getMerges();
        if (mergedRegions == null) {
            mergedRegions = new ArrayList<>();
            gSheet.setMerges(mergedRegions);
        }
        mergedRegions.add(regionToMerge);

        return new Cell(this, startRow, startCol);
    }

    /**
     * Unmerges all merged regions on this sheet that intersects with given cells range.
     *
     * @param rangeRef reference string to cells range. E.g. "A23:D50".
     */
    public void unmergeCells(String rangeRef) {
        CellRange region = new CellRange(rangeRef);
        unmergeCells(region.getFirstRow(), region.getFirstCol(),
                region.getLastRow(), region.getLastCol());
    }

    /**
     * Unmerges all merged regions on this sheet that intersects with given cells range.
     *
     * @param range cells range.
     * @see CellRange
     */
    public void unmergeCells(CellRange range) {
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
        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        if (gSheet.getMerges() == null) {
            return;
        }

        final CellRange givenRegion = new CellRange(startRow, endRow, startCol, endCol);
        final List<CellRange> mergedRegions = gSheet.getMerges().stream().map(CellRange::new).collect(Collectors.toList());

        final List<GridRange> regionsToRemove = new ArrayList<>();
        for (int index = 0; index < mergedRegions.size(); index++) {
            if (mergedRegions.get(index).intersects(givenRegion)) {
                regionsToRemove.add(gSheet.getMerges().get(index));
            }
        }

        if (regionsToRemove.size() > 0) {
            parent.batchUpdate(r -> {
                for (GridRange region : regionsToRemove) {
                    r.addUnmergeCellsRequest(region);
                    gSheet.getMerges().remove(region);
                }
            });
        }
    }

    /**
     * Gets list of all existing merged regions on this sheet.
     *
     * @return list of cell ranges that represent existing merged regions.
     */
    public List<CellRange> getMergedRegions() {
        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        if (gSheet.getMerges() == null) {
            return new ArrayList<>();
        }
        return gSheet.getMerges().stream().map(CellRange::new).collect(Collectors.toList());
    }

    /**
     * Gets the row represented by given reference.
     *
     * @param rowRef reference to cell that belongs to necessary row. E.g. "A23" defines row with index 22.
     * @return high level Row object representing corresponding row or {@code null} if row is not defined.
     */
    public Row getRow(String rowRef) {
        return getRow(new CellRef(rowRef).getRow());
    }

    /**
     * Gets the row represented with given index.
     *
     * @param rowIndex 0-based index of necessary row.
     * @return high level Row object representing corresponding row or {@code null} if row is not defined.
     */
    public Row getRow(int rowIndex) {
        if (rowIndex >= 0) {
            List<GridData> gridsData = getGSheet().getData();
            if (gridsData == null) {
                return null;
            }
            for (GridData gridData : gridsData) {
                List<RowData> rowsData = gridData.getRowData();
                int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;

                if (rowsData != null && rowIndex >= startRow && rowIndex < startRow + rowsData.size()) {
                    RowData row = rowsData.get(rowIndex - startRow);
                    return row != null ? new Row(this, rowIndex) : null;
                }
            }
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
     * or {@code null} if nothing is found.
     */
    public Row findRow(String... values) {
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
     *                    is {@code null} the {@link MatchMethod#EXACT} is used as default.
     * @param values      the set of values to lookup.
     * @return high level Row object representing the first found row that have cells match to all given values
     * or {@code null} if nothing is found.
     * @see MatchMethod
     */
    public Row findRow(MatchMethod matchMethod, String... values) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        for (Row row : this) {
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
    public Row createRow(int rowIndex) {
        if (rowIndex < 0) {
            throw new IllegalArgumentException("Row index can not be negative.");
        }
        List<GridData> gridsData = getGSheetGridsData();
        int lastRowIndex = getLastRowIndex();
        if (rowIndex > lastRowIndex) {
            GridData lastGridData = gridsData.get(gridsData.size() - 1);
            List<RowData> rowsData = lastGridData.getRowData();
            if (rowsData == null || rowsData.isEmpty()) {
                rowsData = new ArrayList<>();
                rowsData.add(new RowData());
                lastGridData.setRowData(rowsData);
                lastGridData.setStartRow(rowIndex);
            } else {
                for (int i = lastRowIndex + 1; i <= rowIndex; i++) {
                    rowsData.add(new RowData());
                }
            }

        } else {
            for (int gridIndex = 0; gridIndex < gridsData.size(); gridIndex++) {
                GridData gridData = gridsData.get(gridIndex);
                int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;

                if (rowIndex < startRow) {
                    GridData newGrid = new GridData();
                    newGrid.setStartRow(rowIndex);
                    newGrid.setRowData(new ArrayList<>());
                    newGrid.getRowData().add(new RowData());
                    gridsData.add(gridIndex, newGrid);
                    break;
                }

                if (rowIndex == startRow + gridData.getRowData().size()) {
                    gridData.getRowData().add(new RowData());
                    break;
                }
            }
        }
        return new Row(this, rowIndex);
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
            final List<?> data = values.get(0) instanceof List ? values : Collections.singletonList(values);
            int rowsCount = data.size();

            List<GridData> gridsData = getGSheetGridsData();
            for (int gridIndex = 0; gridIndex < gridsData.size(); gridIndex++) {
                GridData gridData = gridsData.get(gridIndex);
                int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;

                if (rowIndex < startRow) {
                    GridData newGrid = new GridData();
                    newGrid.setStartRow(rowIndex);
                    newGrid.setRowData(new ArrayList<>());
                    for (int i = rowsCount; i > 0; i--) {
                        newGrid.getRowData().add(new RowData());
                    }
                    gridsData.add(gridIndex, newGrid);
                    break;
                }

                if (rowIndex < startRow + gridData.getRowData().size()) {
                    for (int i = rowIndex - startRow; i < rowIndex - startRow + rowsCount; i++) {
                        gridData.getRowData().add(i, new RowData());
                    }
                    break;
                }

                if (rowIndex == startRow + gridData.getRowData().size()) {
                    for (int i = rowsCount; i > 0; i--) {
                        gridData.getRowData().add(new RowData());
                    }
                    break;
                }
            }

            parent.batchUpdate(r -> {
                r.addInsertRowsRequest(rowIndex, rowsCount, getId());
                putRange(rowIndex, startCol, data);
            });
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
    public void removeRow(Row row) {
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
        for (GridData gridData : getGSheetGridsData()) {
            List<RowData> rowsData = gridData.getRowData();
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;

            if (rowsData != null && rowIndex >= startRow && rowIndex < startRow + rowsData.size()) {
                rowsData.remove(rowIndex - startRow);
                parent.batchUpdate(r -> {
                    r.addDeleteRowsRequest(rowIndex, 1, getId());
                });
            }
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
    public void cleanRow(Row row) {
        cleanRow(row.getIndex());
    }

    /**
     * Removes all cells contained in the specified row of this sheet.
     *
     * @param rowIndex 0-based index of target row.
     */
    public void cleanRow(int rowIndex) {
        for (GridData gridData : getGSheetGridsData()) {
            List<RowData> rowsData = gridData.getRowData();
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;

            if (rowsData != null && rowIndex >= startRow && rowIndex < startRow + rowsData.size()) {
                rowsData.get(rowIndex - startRow).clear();
                parent.batchUpdate(r -> {
                    r.addCleanRowRequest(rowIndex, getId());
                });
            }
        }
    }

    /**
     * Gets index of the first defined row on the sheet.
     *
     * @return 0-based index of the first defined row on the sheet or <code>-1</code> if no rows exist.
     */
    public int getFirstRowIndex() {
        List<GridData> gridsData = getGSheet().getData();
        if (gridsData == null || gridsData.size() == 0) return -1;
        GridData firstGridData = null;
        for (GridData gridData : gridsData) {
            if (gridData.getRowData() != null && gridData.getRowData().size() > 0) {
                firstGridData = gridData;
                break;
            }
        }
        if (firstGridData == null) return -1;
        return firstGridData.getStartRow() != null ? firstGridData.getStartRow() : 0;
    }

    /**
     * Gets index of the last row contained on the sheet.
     *
     * @return 0-based index of the last row contained on the sheet or <code>-1</code> if no rows exist.
     */
    public int getLastRowIndex() {
        List<GridData> gridsData = getGSheet().getData();
        if (gridsData == null || gridsData.size() == 0) return -1;
        GridData lastGridData = null;
        for (int i = gridsData.size() - 1; i >= 0; i--) {
            GridData gridData = gridsData.get(i);
            if (gridData.getRowData() != null && gridData.getRowData().size() > 0) {
                lastGridData = gridData;
                break;
            }
        }
        if (lastGridData == null) return -1;
        int startRow = lastGridData.getStartRow() != null ? lastGridData.getStartRow() : 0;
        return startRow + lastGridData.getRowData().size() - 1;
    }

    /**
     * Gets column of this sheet represented by given reference.
     *
     * @param colRef reference to cell that belongs to necessary column. E.g. "C23" defines column with index 2.
     * @return high level object representing corresponding column or {@code null} if column is not defined.
     */
    public Column getColumn(String colRef) {
        return getColumn(new CellRef(colRef).getCol());
    }

    /**
     * Gets column of this sheet represented by given index.
     *
     * @param colIndex 0-based index of necessary column.
     * @return high level object representing corresponding column or {@code null} if column is not defined.
     */
    public Column getColumn(int colIndex) {
        return colIndex >= 0 && colIndex <= getLastColumnIndex() ? new Column(this, colIndex) : null;
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
     *
     * @param method      defines position for insertion relatively to column specified by <code>columnRef</code>.
     * @param columnRef   defines a column as position for insertion. E.g. "C23" defines column with index 2.
     * @param startRowRef defines top row on the column starting from which <code>values</code> should be set.
     *                    E.g. "A23" defines row with index 22.
     * @param values      list of values to set.
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
     *
     * @param method    defines position for insertion relatively to column specified by <code>columnPos</code>.
     * @param columnPos 0-based index of column that defines position for insertion.
     * @param startRow  0-based index of row on the column starting from which <code>values</code> should be set.
     * @param values    list of values to set.
     */
    public void insertColumn(InsertMethod method, int columnPos, int startRow, List<?> values) {
        if (columnPos < 0 || startRow < 0 || values == null || values.isEmpty()) {
            return;
        }

        int columnIndex = method == null || method == InsertMethod.BEFORE ? columnPos : columnPos + 1;
        final List<List<?>> columnData = values.stream().map(Collections::singletonList).collect(Collectors.toList());

        if (columnIndex > getLastColumnIndex()) {
            putRange(startRow, columnIndex, columnData);
        } else {
            for (GridData gridsDatum : getGSheetGridsData()) {
                for (RowData rowData : gridsDatum.getRowData()) {
                    List<CellData> cellsData = rowData.getValues();
                    if (cellsData != null && columnIndex < cellsData.size()) {
                        cellsData.add(columnIndex, new CellData());
                    }
                }
            }
            parent.batchUpdate(r -> {
                r.addInsertColumnsRequest(columnIndex, 1, getId());
                putRange(startRow, columnIndex, columnData);
            });
        }
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionRef</code>.</p>
     *
     * @param columnToMoveRef reference to cell that belongs to column to move. E.g. "C23" defines column with index 2.
     * @param method          defines position for moving relatively to column specified by <code>toPositionRef</code>.
     * @param toPositionRef   defines a column as position for moving. E.g. "C23" defines column with index 2.
     */
    public void moveColumn(String columnToMoveRef, InsertMethod method, String toPositionRef) {
        moveColumn(new CellRef(columnToMoveRef).getCol(), method, new CellRef(toPositionRef).getCol());
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionIndex</code>.</p>
     *
     * @param columnToMoveRef reference to cell that belongs to column to move. E.g. "C23" defines column with index 2.
     * @param method          defines position for moving relatively to column specified by <code>toPositionIndex</code>.
     * @param toPositionIndex 0-based index of column that defines position for moving.
     */
    public void moveColumn(String columnToMoveRef, InsertMethod method, int toPositionIndex) {
        moveColumn(new CellRef(columnToMoveRef).getCol(), method, toPositionIndex);
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionRef</code>.</p>
     *
     * @param columnToMoveIndex 0-based index of column to move.
     * @param method            defines position for moving relatively to column specified by <code>toPositionRef</code>.
     * @param toPositionRef     defines a column as position for moving. E.g. "C23" defines column with index 2.
     */
    public void moveColumn(int columnToMoveIndex, InsertMethod method, String toPositionRef) {
        moveColumn(columnToMoveIndex, method, new CellRef(toPositionRef).getCol());
    }

    /**
     * <p>Moves column to given position within this sheet.</p>
     * <p>
     * <p>Column can be moved {@link InsertMethod#BEFORE} or {@link InsertMethod#AFTER} of column specified by
     * <code>toPositionIndex</code>.</p>
     *
     * @param columnToMoveIndex 0-based index of column to move.
     * @param method            defines position for moving relatively to column specified by <code>toPositionIndex</code>.
     * @param toPositionIndex   0-based index of column that defines position for moving.
     */
    public void moveColumn(int columnToMoveIndex, InsertMethod method, int toPositionIndex) {
        if (columnToMoveIndex < 0 || columnToMoveIndex > getLastColumnIndex() || toPositionIndex < 0) {
            return;
        }
        int pos = method == null || method == InsertMethod.BEFORE ? toPositionIndex : toPositionIndex + 1;
        if (pos != columnToMoveIndex) {
            parent.batchUpdate(r -> {
                r.addMoveColumnsRequest(columnToMoveIndex, pos, 1, getGSheet().getProperties().getSheetId());
            });
        }
    }

    /**
     * Removes a column from this sheet and shifts all followed columns to one position left.
     *
     * @param colRef reference to cell that belongs to column to remove. E.g. "C23" defines column with index 2.
     */
    public void removeColumn(String colRef) {
        removeColumn(new CellRef(colRef).getCol());
    }

    /**
     * Removes a column from this sheet and shifts all followed columns to one position left.
     *
     * @param column representing a column to remove.
     */
    public void removeColumn(Column column) {
        removeColumn(column.getIndex());
    }

    /**
     * Removes a column from this sheet and shifts all followed columns to one position left.
     *
     * @param colIndex 0-based index of column to remove.
     */
    public void removeColumn(int colIndex) {
        if (colIndex < 0 || colIndex > getLastColumnIndex()) {
            return;
        }
        for (GridData gridData : getGSheetGridsData()) {
            List<RowData> rowsData = gridData.getRowData();
            if (rowsData == null) {
                continue;
            }
            for (RowData rowData : rowsData) {
                List<CellData> cellsData = rowData.getValues();
                if (cellsData != null && colIndex < cellsData.size()) {
                    cellsData.remove(colIndex);
                }
            }
        }
        parent.batchUpdate(r -> {
            r.addDeleteColumnsRequest(colIndex, 1, getId());
        });
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
        if (colIndex < 0 || colIndex > getLastColumnIndex()) {
            return;
        }
        for (GridData gridData : getGSheetGridsData()) {
            List<RowData> rowsData = gridData.getRowData();
            if (rowsData == null) {
                continue;
            }
            for (RowData rowData : rowsData) {
                List<CellData> cellsData = rowData.getValues();
                if (cellsData != null && colIndex < cellsData.size()) {
                    cellsData.remove(colIndex);
                }
            }
        }
        parent.batchUpdate(r -> {
            r.addCleanColumnRequest(colIndex, getId());
        });
    }

    /**
     * Sets the width of column in pixels.
     *
     * @param colRef reference to cell that belongs to target column. E.g. "C23" defines column with index 2.
     * @param width  the width in pixels.
     */
    public void setColumnWidth(String colRef, int width) {
        setColumnWidth(new CellRef(colRef).getCol(), width);
    }

    /**
     * Sets the width of column in pixels.
     *
     * @param columnIndex 0-based index of target column.
     * @param width       the width in pixels.
     */
    public void setColumnWidth(int columnIndex, int width) {
        if (columnIndex < 0 || columnIndex > getLastColumnIndex()) {
            return;
        }
        for (GridData gridData : getGSheetGridsData()) {
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            List<DimensionProperties> columns = gridData.getColumnMetadata();
            if (columns == null) {
                columns = new ArrayList<>();
                gridData.setColumnMetadata(columns);
            }

            while (columnIndex >= startColumn + columns.size()) {
                columns.add(new DimensionProperties());
            }

            if (columnIndex >= startColumn && columnIndex < startColumn + columns.size()) {
                DimensionProperties columnMetadata = columns.get(columnIndex - startColumn);
                columnMetadata.setPixelSize(width);
                parent.batchUpdate(r -> {
                    r.addUpdateColumnMetadataRequest(columnIndex, columnMetadata, getId());
                });
                break;
            }
        }
    }

    /**
     * Gets index of the first defined column on the sheet. Column is defined if it has at least one
     * defined cell at its position.
     *
     * @return 0-based index of the first defined column on the sheet or <code>-1</code> if no columns exist.
     */
    public int getFirstColumnIndex() {
        int firstColIndex = Integer.MAX_VALUE;
        List<GridData> gridsData = getGSheet().getData();
        if (gridsData == null || gridsData.size() == 0) return -1;
        for (GridData gridData : gridsData) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            firstColIndex = Math.min(firstColIndex, startColumn);
        }
        return firstColIndex != Integer.MAX_VALUE ? firstColIndex : -1;
    }

    /**
     * Gets index of the last defined column contained on the sheet.
     *
     * @return 0-based index of the last defined column contained on the sheet or <code>-1</code> if no columns exist.
     */
    public int getLastColumnIndex() {
        int lastColIndex = -1;
        List<GridData> gridsData = getGSheet().getData();
        if (gridsData == null || gridsData.size() == 0) return -1;
        for (GridData gridData : gridsData) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            for (RowData rowData : gridData.getRowData()) {
                if (rowData.getValues() != null) {
                    lastColIndex = Math.max(lastColIndex, startColumn + rowData.getValues().size() - 1);
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
    public <T> Table<T> getTable(String topLeftCellRef, Class<T> recordType) {
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
    public <T> Table<T> getTable(int headerTopRow, int headerLeftCol, Class<T> recordType) {
        return new Table<T>(this, headerTopRow, headerLeftCol, headerTopRow, getLastColumnIndex(), recordType);
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
    public <T> Table<T> getTable(String headerTopLeftCellRef, String headerBottomRightCellRef, Class<T> recordType) {
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
    public <T> Table<T> getTable(int headerTopRow, int headerLeftCol,
                                 int headerBottomRow, int headerRightCol, Class<T> recordType) {
        return new Table<T>(this, headerTopRow, headerLeftCol, headerBottomRow, headerRightCol, recordType);
    }

    /**
     * Searches table on the sheet with given keywords in header.
     *
     * @param recordType class instance of records that this table works with.
     * @param keywords   - keywords to localize table header. Exact matching is used during comparing keywords with
     *                   table column names.
     * @return object representing the table or {@code null} if nothing was found.
     * @see #findRow(MatchMethod, String...)
     */
    public <T> Table<T> findTable(Class<T> recordType, String... keywords) {
        return findTable(recordType, MatchMethod.EXACT, keywords);
    }

    /**
     * Searches table on the sheet with given keywords in header.
     *
     * @param recordType  class instance of records that this table works with.
     * @param matchMethod the way how given keywords will be matched with table column names. If <code>matchMethod</code>
     *                    is {@code null} the {@link MatchMethod#EXACT} is used as default.
     * @param keywords    keywords to localize table header
     * @return object representing the table or {@code null} if nothing was found.
     * @see #findRow(MatchMethod, String...)
     */
    public <T> Table<T> findTable(Class<T> recordType, MatchMethod matchMethod, String... keywords) {
        if (matchMethod == null) {
            matchMethod = MatchMethod.EXACT;
        }
        Row headerRow = findRow(matchMethod, keywords);
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
     * Inserts table into this sheet at position starting from cell "A1" (0 - row index and 0 - column index)
     * <p>
     * Table header specification and cell styles that determine how the table will looks like should be
     * specified using annotations in the class of record. Otherwise nothing will be inserted.
     *
     * @param records list of records to be inserted as table content.
     * @param <T>     class of records. This class should specify the table header specification and cell styles
     *                using annotations {@link GSheetColumn}.
     * @return object representing the inserted table.
     * @see GSheetColumn
     * @see GSheetTable
     */
    public <T> Table<T> insertTable(List<T> records) {
        return insertTable(0, 0, records);
    }

    /**
     * Inserts table into this sheet at position starting from cell defined by
     * <code>topLeftCellRef</code> (top-left cell of the table)
     * <p>
     * Table header specification and cell styles that determine how the table will looks like should be
     * specified using annotations in the class of record. Otherwise nothing will be inserted.
     *
     * @param topLeftCellRef reference to cell that defines top-left cell of the table (place where table
     *                       should be inserted). E.g. "A23".
     * @param records        list of records to be inserted as table content.
     * @param <T>            class of records. This class should specify the table header specification and cell styles
     *                       using annotations {@link GSheetColumn}.
     * @return object representing the inserted table.
     * @see GSheetColumn
     * @see GSheetTable
     */
    public <T> Table<T> insertTable(String topLeftCellRef, List<T> records) {
        CellRef ref = new CellRef(topLeftCellRef);
        return insertTable(ref.getRow(), ref.getCol(), records);
    }

    /**
     * Inserts table into this sheet at position starting from cell defined by
     * <code>startRow</code> and <code>startCol</code> (top-left cell of the table)
     * <p>
     * Table header specification and cell styles that determine how the table will looks like should be
     * specified using annotations in the class of record. Otherwise nothing will be inserted.
     *
     * @param startRow 0-based index of row that defines top-left cell of the table (place where table should be
     *                 inserted).
     * @param startCol 0-based index of column that defines top-left cell of the table (place where table should be
     *                 inserted).
     * @param records  list of records to be inserted as table content.
     * @param <T>      class of records. This class should specify the table header specification and cell styles
     *                 using annotations {@link GSheetColumn}.
     * @return object representing the inserted table.
     * @see GSheetColumn
     * @see GSheetTable
     */
    public <T> Table<T> insertTable(int startRow, int startCol, List<T> records) {
        return startRow >= 0 && startCol >= 0 && records != null && records.size() > 0
                ? new Table<T>(this, startRow, startCol, records)
                : null;
    }

    /**
     * Moves this sheet to a new position.
     *
     * @param newPos 0-based index where this sheet should be moved.
     */
    public void moveTo(int newPos) {
        List<com.google.api.services.sheets.v4.model.Sheet> sheets = parent.getGSpreadsheet().getSheets();
        if (newPos < 0 || newPos > sheets.size() || newPos == sheetIndex) {
            return;
        }
        com.google.api.services.sheets.v4.model.Sheet gSheet = sheets.remove(sheetIndex);
        sheets.add(newPos, gSheet);
        gSheet.getProperties().setIndex(newPos);
        sheetIndex = newPos;
        parent.batchUpdate(r -> {
            r.addUpdateSheetPropertiesRequest(gSheet, "index");
        });
    }

    /**
     * Changes the name of this sheet.
     *
     * @param newName a new name of this sheet.
     */
    public void rename(String newName) {
        if (parent.getSheetNames().stream().anyMatch(newName::equalsIgnoreCase)) {
            throw new IllegalArgumentException(
                    String.format("The sheet with name '%s' already exist in the spreadsheet.", newName)
            );
        }
        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        gSheet.getProperties().setTitle(newName);
        parent.batchUpdate(r -> {
            r.addUpdateSheetPropertiesRequest(gSheet, "title");
        });
    }

    /**
     * Clones this sheet and place it to the end of Spreadsheet document.
     *
     * @param clonedSheetName the name that should be set for cloned sheet.
     * @return object representing the cloned sheet.
     */
    public Sheet cloneAs(String clonedSheetName) {
        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        com.google.api.services.sheets.v4.model.Sheet gSheetClone = gSheet.clone();
        int clonedSheetIndex = parent.getGSpreadsheet().getSheets().size();

        SpreadsheetUpdateRequestsBatch request = new SpreadsheetUpdateRequestsBatch(getDocument());
        request.addDuplicateSheetRequest(gSheet, clonedSheetIndex, clonedSheetName);
        BatchUpdateSpreadsheetResponse response = request.send().get(0);
        gSheetClone.setProperties(response.getReplies().get(0).getDuplicateSheet().getProperties());
        parent.getGSpreadsheet().getSheets().add(gSheetClone);

        return new Sheet(parent, clonedSheetIndex);
    }

    /**
     * Copies the content of this sheet to another sheet with the same format. Destination sheet can be located
     * in another Spreadsheet document.
     *
     * @param destDoc object representing destination Spreadsheet document.
     * @return object representing just copied sheet of destination Spreadsheet document.
     */
    public Sheet copyTo(SpreadsheetDocument destDoc) {
        CopySheetToAnotherSpreadsheetRequest requestBody = new CopySheetToAnotherSpreadsheetRequest();
        requestBody.setDestinationSpreadsheetId(destDoc.getId());
        try {
            SheetProperties props = parent.getSheetsService().spreadsheets().sheets().copyTo(getDocument().getId(), getId(), requestBody).execute();
            destDoc.reload();
            return destDoc.selectSheet(props.getIndex());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Copying of sheet '%s' has failed.", getName()), e);
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
    public Iterator<Row> iterator() {
        return new RowIterator(getGSheet());
    }

    /**
     * Returns underlay Google API object representing this sheet. This object can be used directly if some specific
     * Google Sheet functionality is necessary within RPA process.
     *
     * @return Google API object representing this sheet.
     */
    public com.google.api.services.sheets.v4.model.Sheet getGSheet() {
        return parent.getGSpreadsheet().getSheets().get(sheetIndex);
    }


    /**
     * Gets the list of GridData contained in underlay Google API object representing this sheet.
     *
     * @return the list of GridData of this sheet.
     */
    List<GridData> getGSheetGridsData() {
        com.google.api.services.sheets.v4.model.Sheet gSheet = getGSheet();
        List<GridData> gridsData = gSheet.getData();
        if (gridsData == null) {
            gridsData = new ArrayList<>();
            gSheet.setData(gridsData);
        }
        if (gridsData.isEmpty()) {
            gridsData.add(new GridData());
        }
        return gridsData;
    }

    /**
     * Gets max amount of available rows on this sheet.
     *
     * @return the max amount of available rows on this sheet.
     */
    int getMaxRowsCount() {
        int maxRowsCount = 0;
        GridData lastGridData = null;
        int startMetaRow = 0;
        List<GridData> gridsData = getGSheetGridsData();
        for (int i = gridsData.size() - 1; i >= 0; i--) {
            GridData gridData = gridsData.get(i);
            if (gridData.getRowMetadata() != null && gridData.getRowMetadata().size() > 0) {
                startMetaRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
                lastGridData = gridData;
                break;
            }
        }
        if (lastGridData != null) {
            maxRowsCount = startMetaRow + lastGridData.getRowMetadata().size();
        }
        return maxRowsCount;
    }

    /**
     * Appends empty records to rows metadata to increase the max amount of available rows on this sheet.
     *
     * @param rowsAmount amount of rows to append.
     */
    void appendRowsMetadata(int rowsAmount) {
        List<GridData> gridsData = getGSheetGridsData();
        GridData lastGridData = null;
        for (int i = gridsData.size() - 1; i >= 0; i--) {
            GridData gridData = gridsData.get(i);
            if (gridData.getRowMetadata() != null && gridData.getRowMetadata().size() > 0) {
                lastGridData = gridData;
                break;
            }
        }
        if (lastGridData == null) {
            lastGridData = gridsData.get(gridsData.size() - 1);
            lastGridData.setRowMetadata(new ArrayList<>());
        }
        for (int i = rowsAmount; i >= 0; i--) {
            lastGridData.getRowMetadata().add(new DimensionProperties());
        }
    }

    /**
     * Gets max amount of available columns on this sheet.
     *
     * @return the max amount of available columns on this sheet.
     */
    int getMaxColumnsCount() {
        int maxColumnsCount = 0;
        for (GridData gridData : getGSheetGridsData()) {
            if (gridData.getColumnMetadata() == null || gridData.getColumnMetadata().isEmpty()) {
                continue;
            }
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            maxColumnsCount = Math.max(maxColumnsCount, startColumn + gridData.getColumnMetadata().size());
        }
        return maxColumnsCount;
    }

    /**
     * Appends empty records to column metadata to increase the max amount of available columns on this sheet.
     *
     * @param columnsAmount amount of columns to append.
     */
    void appendColumnMetadata(int columnsAmount) {
        List<GridData> gridsData = getGSheetGridsData();
        GridData lastGridData = null;
        for (int i = gridsData.size() - 1; i >= 0; i--) {
            GridData gridData = gridsData.get(i);
            if (gridData.getColumnMetadata() != null && gridData.getColumnMetadata().size() > 0) {
                lastGridData = gridData;
                break;
            }
        }
        if (lastGridData == null) {
            lastGridData = gridsData.get(gridsData.size() - 1);
            lastGridData.setColumnMetadata(new ArrayList<>());
        }
        for (int i = columnsAmount; i >= 0; i--) {
            lastGridData.getColumnMetadata().add(new DimensionProperties());
        }
    }

    /**
     * Rows iterator. Allows iteration over all existing rows of the sheet using "for" loop.
     */
    private class RowIterator implements Iterator<Row> {

        private com.google.api.services.sheets.v4.model.Sheet gSheet;
        private int gridIndex = 0;
        private int rowIndex = 0;
        private int rowsCount;

        public RowIterator(com.google.api.services.sheets.v4.model.Sheet gSheet) {
            this.gSheet = gSheet;
            this.rowsCount = gSheet.getData() != null
                    ? gSheet.getData().stream().mapToInt(gd -> gd.getRowData() != null ? gd.getRowData().size() : 0).sum()
                    : 0;
        }

        @Override
        public boolean hasNext() {
            if (rowIndex < rowsCount) {
                GridData gridData = gSheet.getData().get(gridIndex);
                int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
                while (gridData.getRowData() == null || rowIndex >= startRow + gridData.getRowData().size()) {
                    gridData = gSheet.getData().get(++gridIndex);
                    startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
                }
                while (rowIndex < startRow) {
                    rowIndex++;
                }
                RowData nextRow = gridData.getRowData().get(rowIndex - startRow);
                while (rowIndex < startRow + gridData.getRowData().size() && nextRow == null) {
                    nextRow = gridData.getRowData().get(++rowIndex - startRow);
                }
                return nextRow != null;
            }
            return false;
        }

        @Override
        public Row next() {
            return new Row(Sheet.this, rowIndex++);
        }
    }
}
