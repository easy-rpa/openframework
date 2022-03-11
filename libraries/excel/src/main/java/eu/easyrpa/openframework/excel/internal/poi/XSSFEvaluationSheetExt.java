package eu.easyrpa.openframework.excel.internal.poi;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Map;

public class XSSFEvaluationSheetExt implements EvaluationSheet {

    private final XSSFSheetExt _xs;
    private Map<CellKey, EvaluationCell> _cellCache;

    public XSSFEvaluationSheetExt(XSSFSheet sheet) {
        _xs = (XSSFSheetExt) sheet;
    }

    public XSSFSheet getXSSFSheet() {
        return _xs;
    }

    /* (non-Javadoc)
     * @see org.apache.poi.ss.formula.EvaluationSheet#getlastRowNum()
     * @since POI 4.0.0
     */
    @Override
    public int getLastRowNum() {
        return _xs.getLastRowNum();
    }

    /* (non-Javadoc)
     * @see org.apache.poi.ss.formula.EvaluationSheet#isRowHidden(int)
     * @since POI 4.1.0
     */
    @Override
    public boolean isRowHidden(int rowIndex) {
        final XSSFRow row = _xs.getRow(rowIndex);
        if (row == null) return false;
        return row.getZeroHeight();
    }

    /* (non-JavaDoc), inherit JavaDoc from EvaluationWorkbook
     * @since POI 3.15 beta 3
     */
    @Override
    public void clearAllCachedResultValues() {
        _cellCache = null;
    }

    @Override
    public EvaluationCell getCell(int rowIndex, int columnIndex) {
        // shortcut evaluation if reference is outside the bounds of existing data
        // see issue #61841 for impact on VLOOKUP in particular
        if (rowIndex > getLastRowNum()) {
            return null;
        }

        XSSFRow row = _xs.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        XSSFCell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        return new XSSFEvaluationCellExt(cell, this);
    }

    private static class CellKey {
        private final int _row;
        private final int _col;
        private int _hash = -1; //lazily computed

        protected CellKey(int row, int col) {
            _row = row;
            _col = col;
        }

        @Override
        public int hashCode() {
            if (_hash == -1) {
                _hash = (17 * 37 + _row) * 37 + _col;
            }
            return _hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CellKey)) {
                return false;
            }
            // assumes other object is one of us, otherwise ClassCastException is thrown
            final CellKey oKey = (CellKey) obj;
            return _row == oKey._row && _col == oKey._col;
        }
    }
}
