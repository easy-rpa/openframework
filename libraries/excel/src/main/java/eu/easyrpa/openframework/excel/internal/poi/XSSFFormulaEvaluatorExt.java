package eu.easyrpa.openframework.excel.internal.poi;

import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.BaseXSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSFFormulaEvaluatorExt extends BaseXSSFFormulaEvaluator {
    private final XSSFWorkbook _book;

    public XSSFFormulaEvaluatorExt(XSSFWorkbook workbook) {
        this(workbook, null, null);
    }

    private XSSFFormulaEvaluatorExt(XSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this(workbook, new WorkbookEvaluator(XSSFEvaluationWorkbookExt.create(workbook), stabilityClassifier, udfFinder));
    }

    protected XSSFFormulaEvaluatorExt(XSSFWorkbook workbook, WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
        _book = workbook;
    }

    /**
     * @param stabilityClassifier used to optimise caching performance. Pass <code>null</code>
     *                            for the (conservative) assumption that any cell may have its definition changed after
     *                            evaluation begins.
     * @param udfFinder           pass <code>null</code> for default (AnalysisToolPak only)
     */
    public static XSSFFormulaEvaluatorExt create(XSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new XSSFFormulaEvaluatorExt(workbook, stabilityClassifier, udfFinder);
    }

    public void notifySetFormula(Cell cell) {
        _bookEvaluator.notifyUpdateCell(new XSSFEvaluationCellExt((XSSFCell) cell));
    }

    public void notifyDeleteCell(Cell cell) {
        _bookEvaluator.notifyDeleteCell(new XSSFEvaluationCellExt((XSSFCell) cell));
    }

    public void notifyUpdateCell(Cell cell) {
        _bookEvaluator.notifyUpdateCell(new XSSFEvaluationCellExt((XSSFCell) cell));
    }

    /**
     * Loops over all cells in all sheets of the supplied
     * workbook.
     * For cells that contain formulas, their formulas are
     * evaluated, and the results are saved. These cells
     * remain as formula cells.
     * For cells that do not contain formulas, no changes
     * are made.
     * This is a helpful wrapper around looping over all
     * cells, and calling evaluateFormulaCell on each one.
     */
    public static void evaluateAllFormulaCells(XSSFWorkbook wb) {
        BaseFormulaEvaluator.evaluateAllFormulaCells(wb);
    }

    @Override
    public XSSFCell evaluateInCell(Cell cell) {
        return (XSSFCell) super.evaluateInCell(cell);
    }

    /**
     * Loops over all cells in all sheets of the supplied
     * workbook.
     * For cells that contain formulas, their formulas are
     * evaluated, and the results are saved. These cells
     * remain as formula cells.
     * For cells that do not contain formulas, no changes
     * are made.
     * This is a helpful wrapper around looping over all
     * cells, and calling evaluateFormulaCell on each one.
     */
    public void evaluateAll() {
        evaluateAllFormulaCells(_book, this);
    }

    /**
     * Turns a XSSFCell into a XSSFEvaluationCell
     */
    @Override
    protected EvaluationCell toEvaluationCell(Cell cell) {
        if (!(cell instanceof XSSFCell)) {
            throw new IllegalArgumentException("Unexpected type of cell: " + cell.getClass() + "." +
                    " Only XSSFCells can be evaluated.");
        }

        return new XSSFEvaluationCellExt((XSSFCell) cell);
    }
}
