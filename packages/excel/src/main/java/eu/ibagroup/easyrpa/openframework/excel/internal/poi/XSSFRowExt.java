package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;

import java.util.Iterator;

public class XSSFRowExt extends XSSFRow {

    private boolean isStale;

    protected XSSFRowExt(CTRow row, XSSFSheet sheet) {
        super(row, sheet);
    }

    @Override
    public Iterator<Cell> cellIterator() {
        checkStale();
        return super.cellIterator();
    }

    @Override
    public XSSFCell createCell(int columnIndex, CellType type) {
        checkStale();
        XSSFCell cell = super.createCell(columnIndex, type);
        boolean isFirstCell = cell.getColumnIndex() == super.getFirstCellNum();
        boolean isLastCell = cell.getColumnIndex() == super.getLastCellNum();
        if (isFirstCell || isLastCell) {
            ((XSSFSheetExt) getSheet()).getRowsProvider().updateSheetDimension(false, true);
        }
        return cell;
    }

    @Override
    public XSSFCell getCell(int cellnum, MissingCellPolicy policy) {
        checkStale();
        return super.getCell(cellnum, policy);
    }

    @Override
    public short getFirstCellNum() {
        checkStale();
        return super.getFirstCellNum();
    }

    @Override
    public short getLastCellNum() {
        checkStale();
        return super.getLastCellNum();
    }

    @Override
    public int getPhysicalNumberOfCells() {
        checkStale();
        return super.getPhysicalNumberOfCells();
    }

    @Override
    public void setRowNum(int rowIndex) {
        checkStale();
        super.setRowNum(rowIndex);
    }

    @Override
    public XSSFCellStyle getRowStyle() {
        checkStale();
        return super.getRowStyle();
    }

    @Override
    public void setRowStyle(CellStyle style) {
        checkStale();
        super.setRowStyle(style);
    }

    @Override
    public void removeCell(Cell cell) {
        checkStale();
        boolean isFirstCell = cell.getColumnIndex() == super.getFirstCellNum();
        boolean isLastCell = cell.getColumnIndex() == super.getLastCellNum();
        super.removeCell(cell);
        if (isFirstCell || isLastCell) {
            ((XSSFSheetExt) getSheet()).getRowsProvider().updateSheetDimension(false, true);
        }
    }

    @Override
    protected void shift(int n) {
        checkStale();
        super.shift(n);
    }

    @Override
    public void copyRowFrom(Row srcRow, CellCopyPolicy policy, CellCopyContext context) {
        checkStale();
        super.copyRowFrom(srcRow, policy, context);
    }

    @Override
    public void shiftCellsRight(int firstShiftColumnIndex, int lastShiftColumnIndex, int step) {
        checkStale();
        super.shiftCellsRight(firstShiftColumnIndex, lastShiftColumnIndex, step);
    }

    @Override
    public void shiftCellsLeft(int firstShiftColumnIndex, int lastShiftColumnIndex, int step) {
        checkStale();
        super.shiftCellsLeft(firstShiftColumnIndex, lastShiftColumnIndex, step);
    }

    protected boolean isStale() {
        return isStale;
    }

    protected void setStale() {
        isStale = true;
    }

    private void checkStale() {
        if (isStale) {
            throw new StaleRecordException(getRowNum());
        }
    }
}
