package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.List;
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
        cache.dataFormatters.put(excelDocumentId, new DataFormatter());
        cache.sheetsCache.put(excelDocumentId, new HashMap<>());
        if (POISaveMemoryExtension.isInitialized()) {
            FormulaEvaluator fe = workbook instanceof XSSFWorkbook
                    ? createFormulaEvaluator((XSSFWorkbook) workbook)
                    : workbook.getCreationHelper().createFormulaEvaluator();
            cache.formulaEvaluators.put(excelDocumentId, fe);
        } else {
            cache.formulaEvaluators.put(excelDocumentId, workbook.getCreationHelper().createFormulaEvaluator());
            cache.rowsCache.put(excelDocumentId, new HashMap<>());
            cache.cellsCache.put(excelDocumentId, new HashMap<>());
        }
        cache.readMergedRegions(excelDocumentId, workbook);
    }

    public static void unregister(int excelDocumentId) {
        POIElementsCache cache = getInstance();
        cache.formulaEvaluators.remove(excelDocumentId);
        cache.dataFormatters.remove(excelDocumentId);
        cache.sheetsCache.remove(excelDocumentId);
        if (!POISaveMemoryExtension.isInitialized()) {
            cache.rowsCache.remove(excelDocumentId);
            cache.cellsCache.remove(excelDocumentId);
        }
        cache.mergedRegionsCache.remove(excelDocumentId);
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

    public static Integer getMergedRegionIndex(int excelDocumentId, String cellId) {
        return getInstance().mergedRegionsCache.get(excelDocumentId).get(cellId);
    }

    public static void addMergedRegion(int excelDocumentId, int sheetIndex, int regionIndex, CellRangeAddress region) {
        POIElementsCache cache = getInstance();
        Map<String, Integer> mergedRegionsCache = cache.mergedRegionsCache.get(excelDocumentId);
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                mergedRegionsCache.put(getId(sheetIndex, i, j), regionIndex);
            }
        }
    }

    public static void removeMergedRegions(int excelDocumentId, List<Integer> regionIndexes) {
        POIElementsCache cache = getInstance();
        Map<String, Integer> mergedRegionsCache = cache.mergedRegionsCache.get(excelDocumentId);
        Map<String, Integer> newMergedRegions = new HashMap<>();
        for (String cellId : mergedRegionsCache.keySet()) {
            Integer index = mergedRegionsCache.get(cellId);
            if (!regionIndexes.contains(index)) {
                int count = 0;
                for (Integer rI : regionIndexes) {
                    if (index > rI) count++;
                }
                newMergedRegions.put(cellId, index - count);
            }
        }
        mergedRegionsCache.clear();
        mergedRegionsCache.putAll(newMergedRegions);
    }

    public static void clearRowsAndCellsCache(int excelDocumentId) {
        if (!POISaveMemoryExtension.isInitialized()) {
            POIElementsCache cache = getInstance();
            cache.rowsCache.get(excelDocumentId).clear();
            cache.cellsCache.get(excelDocumentId).clear();
        }
    }

    public static String getId(int sheetIndex, int rowIndex) {
        return sheetIndex + "|" + rowIndex;
    }

    public static String getId(int sheetIndex, int rowIndex, int columnIndex) {
        return sheetIndex + "|" + rowIndex + "|" + columnIndex;
    }

    /**
     * @return unique Id for Excel Document.
     */
    public static int generateExcelDocumentId() {
        return Integer.parseInt((int) (Math.random() * 100) + "" + (System.currentTimeMillis() % 1000000));
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

    private Map<Integer, Map<String, Integer>> mergedRegionsCache = new HashMap<>();

    private POIElementsCache() {
    }

    private void readMergedRegions(int excelDocumentId, Workbook workbook) {
        Map<String, Integer> docMergedRegionsCache = new HashMap<>();
        mergedRegionsCache.put(excelDocumentId, docMergedRegionsCache);
        for (Sheet sheet : workbook) {
            int sheetIndex = workbook.getSheetIndex(sheet.getSheetName());
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int regionIndex = 0; regionIndex < mergedRegions.size(); regionIndex++) {
                CellRangeAddress mergedRegion = mergedRegions.get(regionIndex);
                for (int i = mergedRegion.getFirstRow(); i <= mergedRegion.getLastRow(); i++) {
                    for (int j = mergedRegion.getFirstColumn(); j <= mergedRegion.getLastColumn(); j++) {
                        docMergedRegionsCache.put(getId(sheetIndex, i, j), regionIndex);
                    }
                }
            }
        }
    }
}

