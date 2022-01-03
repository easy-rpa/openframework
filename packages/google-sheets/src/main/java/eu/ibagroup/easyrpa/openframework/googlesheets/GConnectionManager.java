package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.UpdateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GConnectionManager {
    private static List<Request> requests = new ArrayList<>();
    private static boolean isSessionOpened;
    private static SpreadsheetDocument document;

    public static void setDocument(SpreadsheetDocument document) {
        GConnectionManager.document = document;
    }

    public static boolean isSessionOpened() {
        return isSessionOpened;
    }

    public static void openSession(SpreadsheetDocument document) {
        if (!isSessionOpened) {
            setDocument(document);
            isSessionOpened = true;
        }
    }

    public static  void closeSession() {
        commit();
        isSessionOpened = false;
    }

    public static void addCellValue(Cell cell, CellData googleCell) {
        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(document.getActiveSheet().getId())
                        .setStartRowIndex(cell.getRowIndex())
                        .setEndRowIndex(cell.getRowIndex() + 1)
                        .setStartColumnIndex(cell.getColumnIndex())
                        .setEndColumnIndex(cell.getColumnIndex() + 1)
                )
                .setCell(googleCell).setFields("userEnteredValue")));
    }

    public static void addCellFormula(Cell cell, CellData googleCell, String newCellFormula) {
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cell.getRowIndex())
                                .setEndRowIndex(cell.getRowIndex() + 1)
                                .setStartColumnIndex(cell.getColumnIndex())
                                .setEndColumnIndex(cell.getColumnIndex() + 1)
                        )
                        .setCell(googleCell
                                .setUserEnteredValue(new ExtendedValue().setFormulaValue(newCellFormula)))
                        .setFields("userEnteredValue")));
    }

    public static void addCellStyle(Cell cell, GSheetCellStyle gSheetCellStyle) {
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cell.getRowIndex())
                                .setEndRowIndex(cell.getRowIndex()+1)
                                .setStartColumnIndex(cell.getColumnIndex())
                                .setEndColumnIndex(cell.getColumnIndex()+1)
                        )
                        .setCell(cell.getGCell()
                                .setUserEnteredFormat(gSheetCellStyle.getCellFormat()))
                        .setFields("userEnteredValue")));
    }

    public static void addRowValue(Row row, List<RowData> rowDataList) {
        requests.add(new Request().setUpdateCells(new UpdateCellsRequest()
                .setRange(new GridRange()
                        .setSheetId(document.getActiveSheet().getId())
                        .setStartRowIndex(row.getRowIndex())
                        .setEndRowIndex(row.getRowIndex() + 1)
                        .setStartColumnIndex(row.getFirstCellIndex())
                        .setEndColumnIndex(row.getLastCellIndex())
                )
                .setRows(rowDataList)
                .setFields("userEnteredValue")));
    }

    private static void commit() {
        if (requests.size() > 0) {
            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);

            try {
                BatchUpdateSpreadsheetResponse response = document.getService().spreadsheets().batchUpdate(document.getId(), body).execute();
                requests.clear();
            } catch (IOException e) {
                throw new UpdateException(e.getMessage());
            }
        }
    }
}
