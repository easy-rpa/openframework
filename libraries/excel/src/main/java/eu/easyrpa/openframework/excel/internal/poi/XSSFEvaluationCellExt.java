package eu.easyrpa.openframework.excel.internal.poi;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class XSSFEvaluationCellExt implements EvaluationCell {

    private final EvaluationSheet _evalSheet;
    private final XSSFCell _cell;

    public XSSFEvaluationCellExt(XSSFCell cell, XSSFEvaluationSheetExt evaluationSheet) {
        _cell = cell;
        _evalSheet = evaluationSheet;
    }

    public XSSFEvaluationCellExt(XSSFCell cell) {
        this(cell, new XSSFEvaluationSheetExt(cell.getSheet()));
    }

    @Override
    public Object getIdentityKey() {
        // save memory by just using the cell itself as the identity key
        // Note - this assumes XSSFCell has not overridden hashCode and equals
        return _cell;
    }

    public XSSFCell getXSSFCell() {
        return _cell;
    }

    @Override
    public boolean getBooleanCellValue() {
        return _cell.getBooleanCellValue();
    }

    /**
     * @return cell type
     */
    @Override
    public CellType getCellType() {
        return _cell.getCellType();
    }

    @Override
    public int getColumnIndex() {
        return _cell.getColumnIndex();
    }

    @Override
    public int getErrorCellValue() {
        return _cell.getErrorCellValue();
    }

    @Override
    public double getNumericCellValue() {
        return _cell.getNumericCellValue();
    }

    @Override
    public int getRowIndex() {
        return _cell.getRowIndex();
    }

    @Override
    public EvaluationSheet getSheet() {
        return _evalSheet;
    }

    @Override
    public String getStringCellValue() {
        return _cell.getRichStringCellValue().getString();
    }

    @Override
    public CellRangeAddress getArrayFormulaRange() {
        return _cell.getArrayFormulaRange();
    }

    @Override
    public boolean isPartOfArrayFormulaGroup() {
        return _cell.isPartOfArrayFormulaGroup();
    }

    /**
     * @return cell type of cached formula result
     */
    @Override
    public CellType getCachedFormulaResultType() {
        return _cell.getCachedFormulaResultType();
    }
}
