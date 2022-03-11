package eu.easyrpa.openframework.excel.internal.poi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.*;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.helpers.BaseRowColShifter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.util.Unbox.box;

public class XSSFRowColExtShifter {
    private static final Logger LOG = LogManager.getLogger(XSSFRowColExtShifter.class);

    private XSSFRowColExtShifter() { /*no instances for static classes*/}

    /**
     * Updated named ranges
     */
    /*package*/
    static void updateNamedRanges(Sheet sheet, FormulaShifter formulaShifter) {
        Workbook wb = sheet.getWorkbook();
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create((XSSFWorkbook) wb);
        for (Name name : wb.getAllNames()) {
            String formula = name.getRefersToFormula();
            int sheetIndex = name.getSheetIndex();
            final int rowIndex = -1; //don't care, named ranges are not allowed to include structured references

            Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.NAMEDRANGE, sheetIndex, rowIndex);
            if (formulaShifter.adjustFormula(ptgs, sheetIndex)) {
                String shiftedFmla = FormulaRenderer.toFormulaString(fpb, ptgs);
                name.setRefersToFormula(shiftedFmla);
            }
        }
    }

    /**
     * Update formulas.
     */
    /*package*/
    static void updateFormulas(XSSFSheetExt sheet, FormulaShifter formulaShifter) {
        //update formulas on the parent sheet
        updateSheetFormulas(sheet, formulaShifter);

        //update formulas on other sheets
        Workbook wb = sheet.getWorkbook();
        for (Sheet sh : wb) {
            if (sheet == sh) continue;
            updateSheetFormulas((XSSFSheetExt)sh, formulaShifter);
        }
    }

    /*package*/
    static void updateSheetFormulas(XSSFSheetExt sheet, FormulaShifter formulaShifter) {
        sheet.getRowsProvider().forEachFormula((row, cell) -> {
            CTCell ctCell = cell.getCTCell();
            CTCellFormula f = ctCell.getF();
            String formula = f.getStringValue();
            if (formula.length() > 0) {
                String shiftedFormula = shiftFormula(row, formula, formulaShifter);
                if (shiftedFormula != null) {
                    f.setStringValue(shiftedFormula);
                    if (f.getT() == STCellFormulaType.SHARED) {
                        int si = Math.toIntExact(f.getSi());
                        CTCellFormula sf = sheet.getSharedFormula(si);
                        sf.setStringValue(shiftedFormula);
                        updateRefInCTCellFormula(row, formulaShifter, sf);
                    }
                }

            }
            //Range of cells which the formula applies to.
            updateRefInCTCellFormula(row, formulaShifter, f);
        });
    }

    /**
     * Shift a formula using the supplied FormulaShifter
     *
     * @param row            the row of the cell this formula belongs to. Used to get a reference to the parent workbook.
     * @param formula        the formula to shift
     * @param formulaShifter the FormulaShifter object that operates on the parsed formula tokens
     * @return the shifted formula if the formula was changed,
     * <code>null</code> if the formula wasn't modified
     */
    /*package*/
    static String shiftFormula(Row row, String formula, FormulaShifter formulaShifter) {
        Sheet sheet = row.getSheet();
        Workbook wb = sheet.getWorkbook();
        int sheetIndex = wb.getSheetIndex(sheet);
        final int rowIndex = row.getRowNum();
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create((XSSFWorkbook) wb);

        try {
            Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.CELL, sheetIndex, rowIndex);
            String shiftedFmla = null;
            if (formulaShifter.adjustFormula(ptgs, sheetIndex)) {
                shiftedFmla = FormulaRenderer.toFormulaString(fpb, ptgs);
            }
            return shiftedFmla;
        } catch (FormulaParseException fpe) {
            // Log, but don't change, rather than breaking
            LOG.atWarn().withThrowable(fpe).log("Error shifting formula on row {}", box(row.getRowNum()));
            return formula;
        }
    }

    /*package*/
    static void updateRefInCTCellFormula(Row row, FormulaShifter formulaShifter, CTCellFormula f) {
        if (f.isSetRef()) { //Range of cells which the formula applies to.
            String ref = f.getRef();
            String shiftedRef = shiftFormula(row, ref, formulaShifter);
            if (shiftedRef != null) f.setRef(shiftedRef);
        }
    }


    /*package*/
    static void updateConditionalFormatting(Sheet sheet, FormulaShifter formulaShifter) {
        XSSFSheet xsheet = (XSSFSheet) sheet;
        XSSFWorkbook wb = xsheet.getWorkbook();
        int sheetIndex = wb.getSheetIndex(sheet);
        final int rowIndex = -1; //don't care, structured references not allowed in conditional formatting

        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
        CTWorksheet ctWorksheet = xsheet.getCTWorksheet();
        CTConditionalFormatting[] conditionalFormattingArray = ctWorksheet.getConditionalFormattingArray();
        // iterate backwards due to possible calls to ctWorksheet.removeConditionalFormatting(j)
        for (int j = conditionalFormattingArray.length - 1; j >= 0; j--) {
            CTConditionalFormatting cf = conditionalFormattingArray[j];

            ArrayList<CellRangeAddress> cellRanges = new ArrayList<>();
            for (Object stRef : cf.getSqref()) {
                String[] regions = stRef.toString().split(" ");
                for (String region : regions) {
                    cellRanges.add(CellRangeAddress.valueOf(region));
                }
            }

            boolean changed = false;
            List<CellRangeAddress> temp = new ArrayList<>();
            for (CellRangeAddress craOld : cellRanges) {
                CellRangeAddress craNew = BaseRowColShifter.shiftRange(formulaShifter, craOld, sheetIndex);
                if (craNew == null) {
                    changed = true;
                    continue;
                }
                temp.add(craNew);
                if (craNew != craOld) {
                    changed = true;
                }
            }

            if (changed) {
                int nRanges = temp.size();
                if (nRanges == 0) {
                    ctWorksheet.removeConditionalFormatting(j);
                    continue;
                }
                List<String> refs = new ArrayList<>();
                for (CellRangeAddress a : temp) refs.add(a.formatAsString());
                cf.setSqref(refs);
            }

            for (CTCfRule cfRule : cf.getCfRuleArray()) {
                String[] formulaArray = cfRule.getFormulaArray();
                for (int i = 0; i < formulaArray.length; i++) {
                    String formula = formulaArray[i];
                    Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.CELL, sheetIndex, rowIndex);
                    if (formulaShifter.adjustFormula(ptgs, sheetIndex)) {
                        String shiftedFmla = FormulaRenderer.toFormulaString(fpb, ptgs);
                        cfRule.setFormulaArray(i, shiftedFmla);
                    }
                }
            }
        }
    }


    /*package*/
    static void updateHyperlinks(Sheet sheet, FormulaShifter formulaShifter) {
        int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);
        List<? extends Hyperlink> hyperlinkList = sheet.getHyperlinkList();

        for (Hyperlink hyperlink : hyperlinkList) {
            XSSFHyperlink xhyperlink = (XSSFHyperlink) hyperlink;
            String cellRef = xhyperlink.getCellRef();
            CellRangeAddress cra = CellRangeAddress.valueOf(cellRef);
            CellRangeAddress shiftedRange = BaseRowColShifter.shiftRange(formulaShifter, cra, sheetIndex);
            if (shiftedRange != null && shiftedRange != cra) {
                // shiftedRange should not be null. If shiftedRange is null, that means
                // that a hyperlink wasn't deleted at the beginning of shiftRows when
                // identifying rows that should be removed because they will be overwritten
                xhyperlink.setCellReference(shiftedRange.formatAsString());
            }
        }
    }

}
