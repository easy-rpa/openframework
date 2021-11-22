package eu.ibagroup.easyrpa.openframework.excel.internal;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;

public class PoiElementsCache {

    private static PoiElementsCache INSTANCE;

    private static PoiElementsCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PoiElementsCache();
        }
        return INSTANCE;
    }

    public static void register(int excelDocumentId, Workbook workbook) {
        PoiElementsCache cache = getInstance();
        cache.workbooks.put(excelDocumentId, workbook);
        cache.formulaEvaluators.put(excelDocumentId, workbook.getCreationHelper().createFormulaEvaluator());
        cache.dataFormatters.put(excelDocumentId, new DataFormatter());
        cache.sheetsCache.put(excelDocumentId, new HashMap<>());
        cache.rowsCache.put(excelDocumentId, new HashMap<>());
        cache.cellsCache.put(excelDocumentId, new HashMap<>());
    }

    public static void unregister(int excelDocumentId) {
        PoiElementsCache cache = getInstance();
        cache.formulaEvaluators.remove(excelDocumentId);
        cache.dataFormatters.remove(excelDocumentId);
        cache.sheetsCache.remove(excelDocumentId);
        cache.rowsCache.remove(excelDocumentId);
        cache.cellsCache.remove(excelDocumentId);
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
        PoiElementsCache cache = getInstance();
        Map<Integer, Sheet> sheetsCache = cache.sheetsCache.get(excelDocumentId);
        Sheet poiSheet = sheetsCache.get(sheetIndex);
        if (poiSheet == null) {
            poiSheet = cache.workbooks.get(excelDocumentId).getSheetAt(sheetIndex);
            sheetsCache.put(sheetIndex, poiSheet);
        }
        return poiSheet;
    }

    public static Row getPoiRow(int excelDocumentId, String rowId, int sheetIndex, int rowIndex) {
        PoiElementsCache cache = getInstance();
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
        PoiElementsCache cache = getInstance();
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
        PoiElementsCache cache = getInstance();
        cache.rowsCache.get(excelDocumentId).clear();
        cache.cellsCache.get(excelDocumentId).clear();
    }

    private Map<Integer, Workbook> workbooks = new HashMap<>();
    private Map<Integer, FormulaEvaluator> formulaEvaluators = new HashMap<>();
    private Map<Integer, DataFormatter> dataFormatters = new HashMap<>();

    private Map<Integer, Map<Integer, Sheet>> sheetsCache = new HashMap<>();
    private Map<Integer, Map<String, Row>> rowsCache = new HashMap<>();
    private Map<Integer, Map<String, Cell>> cellsCache = new HashMap<>();

    private PoiElementsCache() {
    }
}

