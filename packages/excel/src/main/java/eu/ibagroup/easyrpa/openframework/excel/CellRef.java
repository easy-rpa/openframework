package eu.ibagroup.easyrpa.openframework.excel;

import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.util.CellReference;

import java.util.Objects;

public class CellRef {

    /**
     * The character ($) that signifies a row or column value is absolute instead of relative
     */
    private static final char ABSOLUTE_REFERENCE_MARKER = '$';
    /**
     * The character (!) that separates sheet names from cell references
     */
    private static final char SHEET_NAME_DELIMITER = '!';

    private String sheetName;
    private int rowIndex = -1;
    private int colIndex = -1;
    private boolean isRowAbs = false;
    private boolean isColAbs = false;

    private String ref;
    private String rowColRef;

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

    public CellRef(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public CellRef(String sheetName, int rowIndex, int colIndex) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public CellRef(String sheetName, int rowIndex, int colIndex, boolean isRowAbs, boolean isColAbs) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.isRowAbs = isRowAbs;
        this.isColAbs = isColAbs;
    }

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

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        if (!Objects.equals(this.sheetName, sheetName)) {
            this.sheetName = sheetName;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getRow() {
        return rowIndex;
    }

    public void setRow(int rowIndex) {
        if (this.rowIndex != rowIndex) {
            this.rowIndex = rowIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getCol() {
        return colIndex;
    }

    public void setCol(int colIndex) {
        if (this.colIndex != colIndex) {
            this.colIndex = colIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isRowAbsolute() {
        return isRowAbs;
    }

    public void setRowAbsolute(boolean rowAbs) {
        if (this.isRowAbs != rowAbs) {
            this.isRowAbs = rowAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isColAbsolute() {
        return isColAbs;
    }

    public void setColAbsolute(boolean colAbs) {
        if (this.isColAbs != colAbs) {
            this.isColAbs = colAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public String formatAsString() {
        if (ref == null) {
            ref = new CellReference(sheetName, rowIndex, colIndex, isRowAbs, isColAbs).formatAsString();
        }
        return ref;
    }

    public String formatAsRowColString() {
        return formatAsRowColString(true);
    }

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
