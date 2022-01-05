package eu.ibagroup.easyrpa.openframework.googlesheets.internal;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.util.HashMap;
import java.util.Map;

public class GSheetElementsCache {

    private static GSheetElementsCache INSTANCE;

    private static GSheetElementsCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GSheetElementsCache();
        }
        return INSTANCE;
    }

    public static void register(String gSheetDocumentId, Spreadsheet spreadsheet) {
        GSheetElementsCache cache = getInstance();
        cache.spreadsheets.put(gSheetDocumentId, spreadsheet);
        cache.dataFormatters.put(gSheetDocumentId, new DataFormatter());
        cache.sheetsCache.put(gSheetDocumentId, new HashMap<>());
        cache.rowsCache.put(gSheetDocumentId, new HashMap<>());
        cache.cellsCache.put(gSheetDocumentId, new HashMap<>());
    }

    public static void unregister(String gSheetDocumentId) {
        GSheetElementsCache cache = getInstance();
        cache.dataFormatters.remove(gSheetDocumentId);
        cache.sheetsCache.remove(gSheetDocumentId);
        cache.rowsCache.remove(gSheetDocumentId);
        cache.cellsCache.remove(gSheetDocumentId);
        cache.spreadsheets.remove(gSheetDocumentId);
    }

    public static DataFormatter getDataFormatter(String gSheetDocumentId) {
        return getInstance().dataFormatters.get(gSheetDocumentId);
    }

    public static void setDataFormatter(String gSheetDocumentId, DataFormatter dataFormatter) {
        getInstance().dataFormatters.put(gSheetDocumentId, dataFormatter);
    }

    public static com.google.api.services.sheets.v4.model.Sheet getGSheet(String gSheetDocumentId, int sheetIndex) {
        GSheetElementsCache cache = getInstance();
        Map<Integer, com.google.api.services.sheets.v4.model.Sheet> sheetsCache = cache.sheetsCache.get(gSheetDocumentId);
        Sheet googleSheet = sheetsCache.get(sheetIndex);
        if (googleSheet == null) {
            googleSheet = cache.spreadsheets.get(gSheetDocumentId).getSheets().get(sheetIndex);
            sheetsCache.put(sheetIndex, googleSheet);
        }
        return googleSheet;
    }

    public static RowData getGRow(String gSheetDocumentId, String rowId, int sheetIndex, int rowIndex) {
        GSheetElementsCache cache = getInstance();
        Map<String, RowData> rowsCache = cache.rowsCache.get(gSheetDocumentId);
        RowData gSheetRow = rowsCache.get(rowId);
        if (gSheetRow == null) {
            gSheetRow = cache.spreadsheets.get(gSheetDocumentId)
                    .getSheets().get(sheetIndex)
                    .getData().get(0)
                    .getRowData().get(rowIndex);
            if (gSheetRow != null) {
                rowsCache.put(rowId, gSheetRow);
            }
        }
        return gSheetRow;
    }

    public static RowData getGRow(String gSheetDocumentId, int sheetIndex, int rowIndex) {
        String rowId = sheetIndex + "|" + rowIndex;
        return getGRow(gSheetDocumentId, rowId, sheetIndex, rowIndex);
    }

    public static CellData getGCell(String gSheetDocumentId, String cellId, int sheetIndex, int rowIndex, int columnIndex) {
        GSheetElementsCache cache = getInstance();
        Map<String, CellData> cellsCache = cache.cellsCache.get(gSheetDocumentId);
        CellData gSheetCell = cellsCache.get(cellId);
        if (gSheetCell == null) {
            gSheetCell = cache.spreadsheets.get(gSheetDocumentId)
                    .getSheets().get(sheetIndex)
                    .getData().get(0)
                    .getRowData().get(rowIndex)
                    .getValues().get(columnIndex);
            if (gSheetCell != null) {
                cellsCache.put(cellId, gSheetCell);
            }
        }
        return gSheetCell;
    }

    public static void clearRowsAndCellsCache(String excelDocumentId) {
        GSheetElementsCache cache = getInstance();
        cache.rowsCache.get(excelDocumentId).clear();
        cache.cellsCache.get(excelDocumentId).clear();
    }

    private Map<String, Spreadsheet> spreadsheets = new HashMap<>();
    private Map<String, DataFormatter> dataFormatters = new HashMap<>();

    private Map<String, Map<Integer, com.google.api.services.sheets.v4.model.Sheet>> sheetsCache = new HashMap<>();
    private Map<String, Map<String, RowData>> rowsCache = new HashMap<>();
    private Map<String, Map<String, CellData>> cellsCache = new HashMap<>();

    private GSheetElementsCache() {
    }
}

