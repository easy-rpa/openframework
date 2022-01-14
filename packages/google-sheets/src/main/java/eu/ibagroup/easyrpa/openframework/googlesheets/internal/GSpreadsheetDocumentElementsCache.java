package eu.ibagroup.easyrpa.openframework.googlesheets.internal;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.GSheetCellStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GSpreadsheetDocumentElementsCache {

    private static GSpreadsheetDocumentElementsCache INSTANCE;

    private static GSpreadsheetDocumentElementsCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GSpreadsheetDocumentElementsCache();
        }
        return INSTANCE;
    }

    public static void register(String gSheetDocumentId, Spreadsheet spreadsheet) {
        GSpreadsheetDocumentElementsCache cache = getInstance();
        cache.spreadsheets.put(gSheetDocumentId, spreadsheet);
        cache.spreadsheetsStyles.put(gSheetDocumentId, new GSheetCellStyle(spreadsheet.getProperties().getDefaultFormat()));
        cache.sheetsCache.put(gSheetDocumentId, new HashMap<>());
        cache.rowsCache.put(gSheetDocumentId, new HashMap<>());
        cache.cellsCache.put(gSheetDocumentId, new HashMap<>());
    }

    public static void unregister(String gSheetDocumentId) {
        GSpreadsheetDocumentElementsCache cache = getInstance();
        cache.spreadsheetsStyles.remove(gSheetDocumentId);
        cache.sheetsCache.remove(gSheetDocumentId);
        cache.rowsCache.remove(gSheetDocumentId);
        cache.cellsCache.remove(gSheetDocumentId);
        cache.spreadsheets.remove(gSheetDocumentId);
    }

    public static GSheetCellStyle getSpreadsheetStyle(String gSheetDocumentId) {
        return getInstance().spreadsheetsStyles.get(gSheetDocumentId);
    }

    public static void setSpreadsheetStyle(String gSheetDocumentId, GSheetCellStyle style) {
        getInstance().spreadsheetsStyles.put(gSheetDocumentId, style);
    }

    public static com.google.api.services.sheets.v4.model.Sheet getGSheet(String gSheetDocumentId, int sheetIndex) {
        GSpreadsheetDocumentElementsCache cache = getInstance();
        Map<Integer, com.google.api.services.sheets.v4.model.Sheet> sheetsCache = cache.sheetsCache.get(gSheetDocumentId);
        Sheet googleSheet = sheetsCache.get(sheetIndex);
        if (googleSheet == null) {
            googleSheet = cache.spreadsheets.get(gSheetDocumentId).getSheets().get(sheetIndex);
            sheetsCache.put(sheetIndex, googleSheet);
        }
        return googleSheet;
    }

    public static RowData getGRow(String gSheetDocumentId, String rowId, int sheetIndex, int rowIndex) {
        GSpreadsheetDocumentElementsCache cache = getInstance();
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
        GSpreadsheetDocumentElementsCache cache = getInstance();
        Map<String, CellData> cellsCache = cache.cellsCache.get(gSheetDocumentId);
        CellData gSheetCell = cellsCache.get(cellId);
        if (gSheetCell == null) {
           List<CellData> rowValues =  cache.spreadsheets.get(gSheetDocumentId)
                    .getSheets().get(sheetIndex)
                    .getData().get(0)
                    .getRowData().get(rowIndex)
                    .getValues();
           if(columnIndex < rowValues.size()) {
               gSheetCell = rowValues.get(columnIndex);
               if (gSheetCell != null) {
                   cellsCache.put(cellId, gSheetCell);
               }
           }
        }
        return gSheetCell;
    }

    public static void clearRowsAndCellsCache(String excelDocumentId) {
        GSpreadsheetDocumentElementsCache cache = getInstance();
        cache.rowsCache.get(excelDocumentId).clear();
        cache.cellsCache.get(excelDocumentId).clear();
    }

    private Map<String, Spreadsheet> spreadsheets = new HashMap<>();
    private Map<String, GSheetCellStyle> spreadsheetsStyles = new HashMap<>();

    private Map<String, Map<Integer, com.google.api.services.sheets.v4.model.Sheet>> sheetsCache = new HashMap<>();
    private Map<String, Map<String, RowData>> rowsCache = new HashMap<>();
    private Map<String, Map<String, CellData>> cellsCache = new HashMap<>();

    private GSpreadsheetDocumentElementsCache() {
    }
}

