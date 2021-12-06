package eu.ibagroup.easyrpa.openframework.googlesheets;

import eu.ibagroup.easyrpa.openframework.googlesheets.constants.InsertMethod;
import eu.ibagroup.easyrpa.openframework.googlesheets.utils.GSheetUtils;

import java.util.List;

public class Sheet {
    public int getLastRowIndex() {
        return 0;
    }

    public List<List<Object>> getRange(int hBottomRow, int hLeftCol, int bottomRow, int hRightCol) {
        String s = GSheetUtils.convertNumToColString(hLeftCol) + String.valueOf(hBottomRow + 1) + ":" + GSheetUtils.convertNumToColString(hRightCol) + String.valueOf(bottomRow);
        return null;

    }

    public Row getRow(int i) {
        return new Row(null, "", i);
    }

    public void insertRows(InsertMethod method, int i, int hLeftCol, List<?> data) {
    }

    public Cell getCell(int i, int j) {
        return null;
    }

    public void putRange(int rowNum, int hLeftCol, List<Object> values) {
    }

    public void removeRow(int i) {
    }

    public Cell getColumn(int headerLeftCol) {
        return null;
    }

    public String getName() {
        return "sheetName";
    }
}
