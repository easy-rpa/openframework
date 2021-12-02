package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

public class POIElementsCache {

    private static POIElementsCache INSTANCE;

    private static POIElementsCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new POIElementsCache();
        }
        return INSTANCE;
    }

    public static void register(int excelDocumentId, Workbook workbook) {
        POIElementsCache cache = getInstance();
        cache.workbooks.put(excelDocumentId, workbook);
        FormulaEvaluator fe = workbook instanceof XSSFWorkbook
                ? createFormulaEvaluator((XSSFWorkbook) workbook)
                : workbook.getCreationHelper().createFormulaEvaluator();
        cache.formulaEvaluators.put(excelDocumentId, fe);
        cache.dataFormatters.put(excelDocumentId, new DataFormatter());
        if (!POISaveMemoryExtension.isInitialized()) {
            cache.sheetsCache.put(excelDocumentId, new HashMap<>());
            cache.rowsCache.put(excelDocumentId, new HashMap<>());
            cache.cellsCache.put(excelDocumentId, new HashMap<>());
        }
    }

    public static void unregister(int excelDocumentId) {
        POIElementsCache cache = getInstance();
        cache.formulaEvaluators.remove(excelDocumentId);
        cache.dataFormatters.remove(excelDocumentId);
        if (!POISaveMemoryExtension.isInitialized()) {
            cache.sheetsCache.remove(excelDocumentId);
            cache.rowsCache.remove(excelDocumentId);
            cache.cellsCache.remove(excelDocumentId);
        }
        cache.workbooks.remove(excelDocumentId);
    }

    public static FormulaEvaluator getEvaluator(int excelDocumentId) {
        return getInstance().formulaEvaluators.get(excelDocumentId);
    }

    public static DataFormatter getDataFormatter(int excelDocumentId) {
        return getInstance().dataFormatters.get(excelDocumentId);
    }

    public static void setDataFormatter(int excelDocumentId, DataFormatter dataFormatter) {
        getInstance().dataFormatters.put(excelDocumentId, dataFormatter);
    }

    public static Sheet getPoiSheet(int excelDocumentId, int sheetIndex) {
        POIElementsCache cache = getInstance();
        if (POISaveMemoryExtension.isInitialized()) {
            return cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex);
        }
        Map<Integer, Sheet> sheetsCache = cache.sheetsCache.get(excelDocumentId);
        Sheet poiSheet = sheetsCache.get(sheetIndex);
        if (poiSheet == null) {
            poiSheet = cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex);
            sheetsCache.put(sheetIndex, poiSheet);
        }
        return poiSheet;
    }

    public static Row getPoiRow(int excelDocumentId, String rowId, int sheetIndex, int rowIndex) {
        POIElementsCache cache = getInstance();
        if (POISaveMemoryExtension.isInitialized()) {
            return cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex).getRow(rowIndex);
        }
        Map<String, Row> rowsCache = cache.rowsCache.get(excelDocumentId);
        Row poiRow = rowsCache.get(rowId);
        if (poiRow == null) {
            poiRow = cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex).getRow(rowIndex);
            if (poiRow != null) {
                rowsCache.put(rowId, poiRow);
            }
        }
        return poiRow;
    }

    public static Cell getPoiCell(int excelDocumentId, String cellId, int sheetIndex, int rowIndex, int columnIndex) {
        POIElementsCache cache = getInstance();
        if (POISaveMemoryExtension.isInitialized()) {
            return cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex).getRow(rowIndex).getCell(columnIndex);
        }
        Map<String, Cell> cellsCache = cache.cellsCache.get(excelDocumentId);
        Cell poiCell = cellsCache.get(cellId);
        if (poiCell == null) {
            poiCell = cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex).getRow(rowIndex).getCell(columnIndex);
            if (poiCell != null) {
                cellsCache.put(cellId, poiCell);
            }
        }
        return poiCell;
    }

    public static void clearRowsAndCellsCache(int excelDocumentId) {
        if (!POISaveMemoryExtension.isInitialized()) {
            POIElementsCache cache = getInstance();
            cache.rowsCache.get(excelDocumentId).clear();
            cache.cellsCache.get(excelDocumentId).clear();
        }
    }

    private static FormulaEvaluator createFormulaEvaluator(XSSFWorkbook workbook) {
        FormulaEvaluator evaluator = new XSSFFormulaEvaluatorExt(workbook);
        Map<String, FormulaEvaluator> evaluatorMap = new HashMap<>();
        evaluatorMap.put("", evaluator);
        Map<String, Workbook> referencedWorkbooks = TypeUtils.getFieldValue(workbook.getCreationHelper(), "referencedWorkbooks");
        referencedWorkbooks.forEach((name, otherWorkbook) -> {
            FormulaEvaluator otherEvaluator;
            if (otherWorkbook instanceof XSSFWorkbook) {
                otherEvaluator = createFormulaEvaluator((XSSFWorkbook) otherWorkbook);
            } else {
                otherEvaluator = otherWorkbook.getCreationHelper().createFormulaEvaluator();
            }
            evaluatorMap.put(name, otherEvaluator);
        });
        evaluator.setupReferencedWorkbooks(evaluatorMap);
        return evaluator;
    }

    private Map<Integer, Workbook> workbooks = new HashMap<>();
    private Map<Integer, FormulaEvaluator> formulaEvaluators = new HashMap<>();
    private Map<Integer, DataFormatter> dataFormatters = new HashMap<>();

    private Map<Integer, Map<Integer, Sheet>> sheetsCache = new HashMap<>();
    private Map<Integer, Map<String, Row>> rowsCache = new HashMap<>();
    private Map<Integer, Map<String, Cell>> cellsCache = new HashMap<>();

    private POIElementsCache() {
    }
}

