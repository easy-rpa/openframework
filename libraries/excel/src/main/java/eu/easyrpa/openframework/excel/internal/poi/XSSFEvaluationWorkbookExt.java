package eu.easyrpa.openframework.excel.internal.poi;

import eu.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.xssf.usermodel.BaseXSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSFEvaluationWorkbookExt extends BaseXSSFEvaluationWorkbook {
    private XSSFEvaluationSheetExt[] _sheetCache;

    public static XSSFEvaluationWorkbookExt create(XSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new XSSFEvaluationWorkbookExt(book);
    }

    private XSSFEvaluationWorkbookExt(XSSFWorkbook book) {
        super(book);
    }

    /* (non-JavaDoc), inherit JavaDoc from EvaluationSheet
     * @since POI 3.15 beta 3
     */
    @Override
    public void clearAllCachedResultValues() {
        super.clearAllCachedResultValues();
        _sheetCache = null;
    }

    @Override
    public int getSheetIndex(EvaluationSheet evalSheet) {
        XSSFSheet sheet = ((XSSFEvaluationSheetExt) evalSheet).getXSSFSheet();
        return _uBook.getSheetIndex(sheet);
    }

    @Override
    public EvaluationSheet getSheet(int sheetIndex) {
        // Performance optimization: build sheet cache the first time this is called
        // to avoid re-creating the XSSFEvaluationSheet each time a new cell is evaluated
        // EvaluationWorkbooks make not guarantee to synchronize changes made to
        // the underlying workbook after the EvaluationWorkbook is created.
        if (_sheetCache == null) {
            final int numberOfSheets = _uBook.getNumberOfSheets();
            _sheetCache = new XSSFEvaluationSheetExt[numberOfSheets];
            for (int i = 0; i < numberOfSheets; i++) {
                _sheetCache[i] = new XSSFEvaluationSheetExt(_uBook.getSheetAt(i));
            }
        }
        if (sheetIndex < 0 || sheetIndex >= _sheetCache.length) {
            // do this to reuse the out-of-bounds logic and message from XSSFWorkbook
            _uBook.getSheetAt(sheetIndex);
        }
        return _sheetCache[sheetIndex];
    }

    @Override
    public Ptg[] getFormulaTokens(EvaluationCell evalCell) {
        final XSSFCell cell = ((XSSFEvaluationCellExt) evalCell).getXSSFCell();
        final int sheetIndex = _uBook.getSheetIndex(cell.getSheet());
        final int rowIndex = cell.getRowIndex();
        String formula = TypeUtils.callMethod(cell, "getCellFormula", this);
        return FormulaParser.parse(formula, this, FormulaType.CELL, sheetIndex, rowIndex);
    }
}
