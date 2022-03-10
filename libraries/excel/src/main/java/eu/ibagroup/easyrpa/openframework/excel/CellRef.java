package eu.ibagroup.easyrpa.openframework.excel;

import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.util.CellReference;

import java.util.Objects;

/**
 * Keeps information that identifies specific cell of the sheet.
 */
public class CellRef {

    /**
     * The character ($) that signifies a row or column value is absolute instead of relative
     */
    private static final char ABSOLUTE_REFERENCE_MARKER = '$';
    /**
     * The character (!) that separates sheet names from cell references
     */
    private static final char SHEET_NAME_DELIMITER = '!';

    /**
     * Name of related sheet.
     */
    private String sheetName;

    /**
     * Row index of this cell reference.
     */
    private int rowIndex = -1;

    /**
     * Column index of this cell reference.
     */
    private int colIndex = -1;

    /**
     * Shows whether index of the row is absolute.
     */
    private boolean isRowAbs = false;

    /**
     * Shows whether index of the column is absolute.
     */
    private boolean isColAbs = false;

    /**
     * Cached string representation of this reference in A1 Excel style.
     */
    private String ref;

    /**
     * Cached string representation of this reference in R1C1 Excel style.
     */
    private String rowColRef;

    /**
     * Creates a new reference based on given A1-style string.
     *
     * @param cellRef string representation of the reference in A1-style. E.g. "A3" or "Sheet1!A3"
     */
    public CellRef(String cellRef) {
        if (cellRef != null) {
            CellReference poiRef = new CellReference(cellRef);
            this.sheetName = poiRef.getSheetName();
            this.rowIndex = poiRef.getRow();
            this.colIndex = poiRef.getCol();
            this.isRowAbs = poiRef.isRowAbsolute();
            this.isColAbs = poiRef.isColAbsolute();
            this.ref = poiRef.formatAsString();
        }
    }

    /**
     * Creates a new sheet-free reference based on given row and column indexes.
     *
     * @param rowIndex 0-based row index of the reference.
     * @param colIndex 0-based column index of the reference.
     */
    public CellRef(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    /**
     * Creates a new sheet-based reference based on given row and column indexes.
     *
     * @param sheetName name of related sheet.
     * @param rowIndex  0-based row index of the reference.
     * @param colIndex  0-based column index of the reference.
     */
    public CellRef(String sheetName, int rowIndex, int colIndex) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    /**
     * Creates a new sheet-based reference.
     *
     * @param sheetName name of related sheet.
     * @param rowIndex  0-based row index of the reference.
     * @param colIndex  0-based column index of the reference.
     * @param isRowAbs  <code>true</code> if row index should be absolute or <code>false</code> otherwise.
     * @param isColAbs  <code>true</code> if column index should be absolute or <code>false</code> otherwise.
     */
    public CellRef(String sheetName, int rowIndex, int colIndex, boolean isRowAbs, boolean isColAbs) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.isRowAbs = isRowAbs;
        this.isColAbs = isColAbs;
    }

    /**
     * Creates a new reference based on POI cell reference.
     *
     * @param poiRef the source instance of POI cell reference.
     */
    CellRef(CellReference poiRef) {
        if (poiRef != null) {
            this.sheetName = poiRef.getSheetName();
            this.rowIndex = poiRef.getRow();
            this.colIndex = poiRef.getCol();
            this.isRowAbs = poiRef.isRowAbsolute();
            this.isColAbs = poiRef.isColAbsolute();
            this.ref = poiRef.formatAsString();
        }
    }

    /**
     * Gets the name of related sheet.
     *
     * @return name of related sheet or <code>null</code> if this cell reference is sheet-free.
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * Sets the name of related sheet and makes this cell reference as sheet-based.
     *
     * @param sheetName the name of sheet to set.
     */
    public void setSheetName(String sheetName) {
        if (!Objects.equals(this.sheetName, sheetName)) {
            this.sheetName = sheetName;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets row index of this cell reference.
     *
     * @return row index of this cell reference.
     */
    public int getRow() {
        return rowIndex;
    }

    /**
     * Sets row index for this cell reference.
     *
     * @param rowIndex 0-based row index to set.
     */
    public void setRow(int rowIndex) {
        if (this.rowIndex != rowIndex) {
            this.rowIndex = rowIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets column index of this cell reference.
     *
     * @return column index of this cell reference.
     */
    public int getCol() {
        return colIndex;
    }

    /**
     * Sets column index for this cell reference.
     *
     * @param colIndex 0-based column index to set.
     */
    public void setCol(int colIndex) {
        if (this.colIndex != colIndex) {
            this.colIndex = colIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether row index is absolute.
     *
     * @return <code>true</code> if row index is absolute or <code>false</code> otherwise.
     */
    public boolean isRowAbsolute() {
        return isRowAbs;
    }

    /**
     * Sets row index as absolute.
     *
     * @param rowAbs <code>true</code> if row index should be absolute or <code>false</code> otherwise.
     */
    public void setRowAbsolute(boolean rowAbs) {
        if (this.isRowAbs != rowAbs) {
            this.isRowAbs = rowAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether column index is absolute.
     *
     * @return <code>true</code> if column index is absolute or <code>false</code> otherwise.
     */
    public boolean isColAbsolute() {
        return isColAbs;
    }

    /**
     * Sets column index as absolute.
     *
     * @param colAbs <code>true</code> if column index should be absolute or <code>false</code> otherwise.
     */
    public void setColAbsolute(boolean colAbs) {
        if (this.isColAbs != colAbs) {
            this.isColAbs = colAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Formats this cell reference as string in A1 Excel style. E.g. "A3" or "Sheet1!A3".
     *
     * @return string representation of this cell reference in A1 Excel style.
     */
    public String formatAsString() {
        if (ref == null) {
            ref = new CellReference(sheetName, rowIndex, colIndex, isRowAbs, isColAbs).formatAsString();
        }
        return ref;
    }

    /**
     * Formats this cell reference as string in R1C1 Excel style. E.g. "R3C1:R20C7" or "Sheet1!R3C1:R20C7".
     *
     * @return string representation of this cell reference in R1C1 Excel style.
     */
    public String formatAsRowColString() {
        return formatAsRowColString(true);
    }

    /**
     * Formats this cell reference as string in R1C1 Excel style. E.g. "R3C1:R20C7" or "Sheet1!R3C1:R20C7".
     *
     * @param includeSheetName set to <code>false</code> if necessary to exclude sheet name in the output string.
     * @return string representation of this cell reference in R1C1 Excel style.
     */
    public String formatAsRowColString(boolean includeSheetName) {
        if (rowColRef == null) {
            StringBuilder sb = new StringBuilder(32);
            if (includeSheetName && sheetName != null) {
                SheetNameFormatter.appendFormat(sb, sheetName);
                sb.append(SHEET_NAME_DELIMITER);
            }
            if (rowIndex >= 0) {
                if (isRowAbs) {
                    sb.append(ABSOLUTE_REFERENCE_MARKER);
                }
                sb.append("R").append(rowIndex + 1);
            }
            if (colIndex >= 0) {
                if (isColAbs) {
                    sb.append(ABSOLUTE_REFERENCE_MARKER);
                }
                sb.append("C").append(colIndex + 1);
            }
            rowColRef = sb.toString();
        }
        return rowColRef;
    }

    /**
     * Checks whether related sheet name is defined for this cell reference (sheet-based or not).
     *
     * @return <code>true</code> is related sheet name is defined or <code>false</code> otherwise.
     */
    public boolean isSheetNameDefined() {
        return sheetName != null && sheetName.length() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellRef)) return false;
        CellRef cellRef = (CellRef) o;
        return rowIndex == cellRef.rowIndex &&
                colIndex == cellRef.colIndex &&
                isRowAbs == cellRef.isRowAbs &&
                isColAbs == cellRef.isColAbs &&
                Objects.equals(sheetName, cellRef.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetName, rowIndex, colIndex, isRowAbs, isColAbs);
    }

    @Override
    public String toString() {
        return formatAsString();
    }
}
