package eu.ibagroup.easyrpa.openframework.excel;

import org.apache.poi.ss.util.CellReference;

import java.util.Objects;

public class CellRef {

    private String sheetName;
    private int rowIndex = -1;
    private int colIndex = -1;
    private boolean isRowAbs = false;
    private boolean isColAbs = false;

    private String ref;

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

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        if (!Objects.equals(this.sheetName, sheetName)) {
            this.sheetName = sheetName;
            this.ref = null;
        }
    }

    public int getRow() {
        return rowIndex;
    }

    public void setRow(int rowIndex) {
        if (this.rowIndex != rowIndex) {
            this.rowIndex = rowIndex;
            this.ref = null;
        }
    }

    public int getCol() {
        return colIndex;
    }

    public void setCol(int colIndex) {
        if (this.colIndex != colIndex) {
            this.colIndex = colIndex;
            this.ref = null;
        }
    }

    public boolean isRowAbsolute() {
        return isRowAbs;
    }

    public void setRowAbsolute(boolean rowAbs) {
        if (this.isRowAbs != rowAbs) {
            this.isRowAbs = rowAbs;
            this.ref = null;
        }
    }

    public boolean isColAbsolute() {
        return isColAbs;
    }

    public void setColAbsolute(boolean colAbs) {
        if (this.isColAbs != colAbs) {
            this.isColAbs = colAbs;
            this.ref = null;
        }
    }

    public String formatAsString() {
        if (ref == null) {
            ref = new CellReference(sheetName, rowIndex, colIndex, isRowAbs, isColAbs).formatAsString();
        }
        return ref;
    }

    public boolean isSheetNameDefined() {
        return sheetName != null && sheetName.length() > 0;
    }
}
