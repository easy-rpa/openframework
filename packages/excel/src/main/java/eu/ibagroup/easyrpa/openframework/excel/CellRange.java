package eu.ibagroup.easyrpa.openframework.excel;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import java.util.Objects;

public class CellRange {

    /**
     * The character (:) that separates the two cell references in a multi-cell area reference
     */
    private static final char CELL_DELIMITER = ':';
    private CellRef firstCell;
    private CellRef lastCell;

    private String ref;
    private String rowColRef;

    public CellRange(String rangeRef) {
        AreaReference poiRef = new AreaReference(rangeRef, null);
        this.firstCell = new CellRef(poiRef.getFirstCell());
        this.lastCell = new CellRef(poiRef.getLastCell());
        this.ref = poiRef.formatAsString();
    }

    public CellRange(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstCell = new CellRef(firstRow, firstCol);
        this.lastCell = new CellRef(lastRow, lastCol);
    }

    public CellRange(String sheetName, int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstCell = new CellRef(sheetName, firstRow, firstCol);
        this.lastCell = new CellRef(sheetName, lastRow, lastCol);
    }

    public String getSheetName() {
        return firstCell.getSheetName();
    }

    public void setSheetName(String sheetName) {
        if (!Objects.equals(getSheetName(), sheetName)) {
            firstCell.setSheetName(sheetName);
            lastCell.setSheetName(sheetName);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getFirstRow() {
        return firstCell.getRow();
    }

    public void setFirstRow(int rowIndex) {
        if (firstCell.getRow() != rowIndex) {
            firstCell.setRow(rowIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getFirstCol() {
        return firstCell.getCol();
    }

    public void setFirstCol(int colIndex) {
        if (firstCell.getCol() != colIndex) {
            firstCell.setRow(colIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getLastRow() {
        return lastCell.getRow();
    }

    public void setLastRow(int rowIndex) {
        if (lastCell.getRow() != rowIndex) {
            lastCell.setRow(rowIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getLastCol() {
        return lastCell.getCol();
    }

    public void setLastCol(int colIndex) {
        if (lastCell.getCol() != colIndex) {
            lastCell.setRow(colIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isFirstRowAbsolute() {
        return firstCell.isRowAbsolute();
    }

    public void setFirstRowAbsolute(boolean rowAbs) {
        if (firstCell.isRowAbsolute() != rowAbs) {
            firstCell.setRowAbsolute(rowAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isFirstColAbsolute() {
        return firstCell.isColAbsolute();
    }

    public void setFirstColAbsolute(boolean colAbs) {
        if (firstCell.isColAbsolute() != colAbs) {
            firstCell.setColAbsolute(colAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isLastRowAbsolute() {
        return lastCell.isRowAbsolute();
    }

    public void setLastRowAbsolute(boolean rowAbs) {
        if (lastCell.isRowAbsolute() != rowAbs) {
            lastCell.setRowAbsolute(rowAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isLastColAbsolute() {
        return lastCell.isColAbsolute();
    }

    public void setLastColAbsolute(boolean colAbs) {
        if (lastCell.isColAbsolute() != colAbs) {
            lastCell.setColAbsolute(colAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getColumnsCount() {
        return lastCell.getCol() - firstCell.getCol() + 1;
    }

    public int getRowsCount() {
        return lastCell.getRow() - firstCell.getRow() + 1;
    }

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

    public boolean isSheetNameDefined() {
        String sheetName = getSheetName();
        return sheetName != null && sheetName.length() > 0;
    }

    /**
     * Determines if the given coordinates lie within the bounds
     * of this range.
     *
     * @param rowInd The row, 0-based.
     * @param colInd The column, 0-based.
     * @return True if the coordinates lie within the bounds, false otherwise.
     */
    public boolean isInRange(int rowInd, int colInd) {
        return firstCell.getRow() <= rowInd && rowInd <= lastCell.getRow() && //containsRow
                firstCell.getCol() <= colInd && colInd <= lastCell.getCol(); //containsColumn
    }

    /**
     * Determines if the given {@link CellRef} lies within the bounds
     * of this range.
     *
     * @param ref the CellRef to check
     * @return True if the ref lies within the bounds, false otherwise.
     */
    public boolean isInRange(CellRef ref) {
        return isInRange(ref.getRow(), ref.getCol());
    }

    /**
     * Determines if the given {@link CellRange} lies within the bounds
     * of this range.
     *
     * @param range the CellRange to check
     * @return True if the range lies within the bounds, false otherwise.
     */
    public boolean isInRange(CellRange range) {
        return isInRange(range.firstCell.getRow(), range.firstCell.getCol())
                && isInRange(range.lastCell.getRow(), range.lastCell.getCol());
    }

    /**
     * Determines whether or not this CellRange and the specified CellRange intersect.
     *
     * @param other a candidate cell range address to check for intersection with this range
     * @return returns true if this range and other range have at least 1 cell in common
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
