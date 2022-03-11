package eu.easyrpa.openframework.excel;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import java.util.Objects;

/**
 * Keeps information that identifies specific rectangle area of sheet cells.
 */
public class CellRange {

    /**
     * The character (:) that separates the two cell references in a multi-cell area reference
     */
    private static final char CELL_DELIMITER = ':';

    /**
     * Reference to the top-left cell of related rectangle area.
     */
    private CellRef firstCell;

    /**
     * Reference to the bottom-right cell of related rectangle area.
     */
    private CellRef lastCell;

    /**
     * Cached string representation of this range in A1 Excel style.
     */
    private String ref;

    /**
     * Cached string representation of this range in R1C1 Excel style.
     */
    private String rowColRef;

    /**
     * Creates a new range based on given A1-style string.
     *
     * @param rangeRef string representation of the range in A1-style. E.g. "A3:G20" or "Sheet1!A3:G20"
     */
    public CellRange(String rangeRef) {
        AreaReference poiRef = new AreaReference(rangeRef, null);
        this.firstCell = new CellRef(poiRef.getFirstCell());
        this.lastCell = new CellRef(poiRef.getLastCell());
        this.ref = poiRef.formatAsString();
    }

    /**
     * Creates a new sheet-free range based on given row and column indexes.
     *
     * @param firstRow 0-based index of the top row of the range.
     * @param firstCol 0-based index of the left column of the range.
     * @param lastRow  0-based index of the bottom row of the range.
     * @param lastCol  0-based index of the right column of the range.
     */
    public CellRange(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstCell = new CellRef(firstRow, firstCol);
        this.lastCell = new CellRef(lastRow, lastCol);
    }

    /**
     * Creates a new sheet-based range based on given row and column indexes.
     *
     * @param sheetName name of related sheet.
     * @param firstRow  0-based index of the top row of the range on related sheet.
     * @param firstCol  0-based index of the left column of the range on related sheet.
     * @param lastRow   0-based index of the bottom row of the range on related sheet.
     * @param lastCol   0-based index of the right column of the range on related sheet.
     */
    public CellRange(String sheetName, int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstCell = new CellRef(sheetName, firstRow, firstCol);
        this.lastCell = new CellRef(sheetName, lastRow, lastCol);
    }

    /**
     * Gets the name of related sheet.
     *
     * @return name of related sheet or <code>null</code> if this range is sheet-free.
     */
    public String getSheetName() {
        return firstCell.getSheetName();
    }

    /**
     * Sets the name of related sheet and makes this range as sheet-based.
     *
     * @param sheetName the name of sheet to set.
     */
    public void setSheetName(String sheetName) {
        if (!Objects.equals(getSheetName(), sheetName)) {
            firstCell.setSheetName(sheetName);
            lastCell.setSheetName(sheetName);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets index of the top row of this range.
     *
     * @return index of the top row of this range.
     */
    public int getFirstRow() {
        return firstCell.getRow();
    }

    /**
     * Sets index of the top row for this range.
     *
     * @param rowIndex 0-based row index to set.
     */
    public void setFirstRow(int rowIndex) {
        if (firstCell.getRow() != rowIndex) {
            firstCell.setRow(rowIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets index of the left column of this range.
     *
     * @return index of the left column of this range.
     */
    public int getFirstCol() {
        return firstCell.getCol();
    }

    /**
     * Sets index of the left column for this range.
     *
     * @param colIndex 0-based column index to set.
     */
    public void setFirstCol(int colIndex) {
        if (firstCell.getCol() != colIndex) {
            firstCell.setRow(colIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets index of the bottom row of this range.
     *
     * @return index of the bottom row of this range.
     */
    public int getLastRow() {
        return lastCell.getRow();
    }

    /**
     * Sets index of the bottom row for this range.
     *
     * @param rowIndex 0-based row index to set.
     */
    public void setLastRow(int rowIndex) {
        if (lastCell.getRow() != rowIndex) {
            lastCell.setRow(rowIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets index of the right column of this range.
     *
     * @return index of the right column of this range.
     */
    public int getLastCol() {
        return lastCell.getCol();
    }

    /**
     * Sets index of the right column for this range.
     *
     * @param colIndex 0-based column index to set.
     */
    public void setLastCol(int colIndex) {
        if (lastCell.getCol() != colIndex) {
            lastCell.setRow(colIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether index of the top row is absolute.
     *
     * @return <code>true</code> if index of the top row is absolute or <code>false</code> otherwise.
     */
    public boolean isFirstRowAbsolute() {
        return firstCell.isRowAbsolute();
    }

    /**
     * Sets index of the top row as absolute.
     *
     * @param rowAbs <code>true</code> if index of the top row should be absolute or <code>false</code> otherwise.
     */
    public void setFirstRowAbsolute(boolean rowAbs) {
        if (firstCell.isRowAbsolute() != rowAbs) {
            firstCell.setRowAbsolute(rowAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether index of the left column is absolute.
     *
     * @return <code>true</code> if index of the left column is absolute or <code>false</code> otherwise.
     */
    public boolean isFirstColAbsolute() {
        return firstCell.isColAbsolute();
    }

    /**
     * Sets index of the left column as absolute.
     *
     * @param colAbs <code>true</code> if index of the left column should be absolute or <code>false</code> otherwise.
     */
    public void setFirstColAbsolute(boolean colAbs) {
        if (firstCell.isColAbsolute() != colAbs) {
            firstCell.setColAbsolute(colAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether index of the bottom row is absolute.
     *
     * @return <code>true</code> if index of the bottom row is absolute or <code>false</code> otherwise.
     */
    public boolean isLastRowAbsolute() {
        return lastCell.isRowAbsolute();
    }

    /**
     * Sets index of the bottom row as absolute.
     *
     * @param rowAbs <code>true</code> if index of the bottom row should be absolute or <code>false</code> otherwise.
     */
    public void setLastRowAbsolute(boolean rowAbs) {
        if (lastCell.isRowAbsolute() != rowAbs) {
            lastCell.setRowAbsolute(rowAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether index of the right column is absolute.
     *
     * @return <code>true</code> if index of the right column is absolute or <code>false</code> otherwise.
     */
    public boolean isLastColAbsolute() {
        return lastCell.isColAbsolute();
    }

    /**
     * Sets index of the right column as absolute.
     *
     * @param colAbs <code>true</code> if index of the right column should be absolute or <code>false</code> otherwise.
     */
    public void setLastColAbsolute(boolean colAbs) {
        if (lastCell.isColAbsolute() != colAbs) {
            lastCell.setColAbsolute(colAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets amount of columns in this range (range width).
     *
     * @return amount of columns in this range.
     */
    public int getColumnsCount() {
        return lastCell.getCol() - firstCell.getCol() + 1;
    }

    /**
     * Gets amount of rows in this range (range height).
     *
     * @return amount of rows in this range.
     */
    public int getRowsCount() {
        return lastCell.getRow() - firstCell.getRow() + 1;
    }

    /**
     * Formats this range as string in A1 Excel style. E.g. "A3:G20" or "Sheet1!A3:G20".
     *
     * @return string representation of this range in A1 Excel style.
     */
    public String formatAsString() {
        if (ref == null) {
            CellReference firstRef = new CellReference(firstCell.getSheetName(),
                    firstCell.getRow(), firstCell.getCol(),
                    firstCell.isRowAbsolute(), firstCell.isColAbsolute());
            CellReference lastRef = new CellReference(lastCell.getSheetName(),
                    lastCell.getRow(), lastCell.getCol(),
                    lastCell.isRowAbsolute(), lastCell.isColAbsolute());
            ref = new AreaReference(firstRef, lastRef, null).formatAsString();
        }
        return ref;
    }

    /**
     * Formats this range as string in R1C1 Excel style. E.g. "R3C1:R20C7" or "Sheet1!R3C1:R20C7".
     *
     * @return string representation of this range in R1C1 Excel style.
     */
    public String formatAsRowColString() {
        if (rowColRef == null) {
            if (firstCell.equals(lastCell)) {
                rowColRef = firstCell.formatAsRowColString();
            } else {
                rowColRef = firstCell.formatAsRowColString() + CELL_DELIMITER + lastCell.formatAsRowColString(false);
            }
        }
        return rowColRef;
    }

    /**
     * Checks whether related sheet name is defined for this range (sheet-based or not).
     *
     * @return <code>true</code> is related sheet name is defined or <code>false</code> otherwise.
     */
    public boolean isSheetNameDefined() {
        String sheetName = getSheetName();
        return sheetName != null && sheetName.length() > 0;
    }

    /**
     * Determines if the given coordinates lie within the bounds of this range.
     *
     * @param rowInd 0-based row index to check.
     * @param colInd 0-based column index to check.
     * @return <code>true</code> if given coordinates lie within the bounds or <code>false</code> otherwise.
     */
    public boolean isInRange(int rowInd, int colInd) {
        return firstCell.getRow() <= rowInd && rowInd <= lastCell.getRow() && //containsRow
                firstCell.getCol() <= colInd && colInd <= lastCell.getCol(); //containsColumn
    }

    /**
     * Determines if the given {@link CellRef} lies within the bounds of this range.
     *
     * @param ref the cell reference to check.
     * @return <code>true</code> if given reference lie within the bounds or <code>false</code> otherwise.
     */
    public boolean isInRange(CellRef ref) {
        return isInRange(ref.getRow(), ref.getCol());
    }

    /**
     * Determines if the given {@link CellRange} lies within the bounds of this range.
     *
     * @param range the cells range to check.
     * @return <code>true</code> if given range lies within the bounds or <code>false</code> otherwise.
     */
    public boolean isInRange(CellRange range) {
        return isInRange(range.firstCell.getRow(), range.firstCell.getCol())
                && isInRange(range.lastCell.getRow(), range.lastCell.getCol());
    }

    /**
     * Determines whether this range intersects with given {@link CellRange}.
     *
     * @param other the cells range to check for intersection with this range.
     * @return <code>true</code> if ranges have at least 1 cell in common or <code>false</code> otherwise.
     * @see #isInRange(int, int) for checking if a single cell intersects
     */
    public boolean intersects(CellRange other) {
        return this.firstCell.getRow() <= other.lastCell.getRow() &&
                this.firstCell.getCol() <= other.lastCell.getCol() &&
                other.firstCell.getRow() <= this.lastCell.getRow() &&
                other.firstCell.getCol() <= this.lastCell.getCol();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellRange)) return false;
        CellRange cellRange = (CellRange) o;
        return Objects.equals(firstCell, cellRange.firstCell) &&
                Objects.equals(lastCell, cellRange.lastCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstCell, lastCell);
    }

    @Override
    public String toString() {
        return formatAsString();
    }
}
