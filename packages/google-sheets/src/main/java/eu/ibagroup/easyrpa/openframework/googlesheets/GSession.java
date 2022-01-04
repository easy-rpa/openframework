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

public class GSession {
    private List<Request> requests = new ArrayList<>();

    public void addCellValueRequest(Cell cell, CellData googleCell, SpreadsheetDocument document) {
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

    public void addCellFormulaRequest(Cell cell, CellData googleCell, String newCellFormula, SpreadsheetDocument document) {
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

    public void addCellStyle(Cell cell, GSheetCellStyle gSheetCellStyle, SpreadsheetDocument document) {
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cell.getRowIndex())
                                .setEndRowIndex(cell.getRowIndex() + 1)
                                .setStartColumnIndex(cell.getColumnIndex())
                                .setEndColumnIndex(cell.getColumnIndex() + 1)
                        )
                        .setCell(cell.getGCell()
                                .setUserEnteredFormat(gSheetCellStyle.getCellFormat()))
                        .setFields("userEnteredValue")));
    }

    public void addRowValue(Row row, List<RowData> rowDataList, SpreadsheetDocument document) {
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

    public void commit(SpreadsheetDocument document) {
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
