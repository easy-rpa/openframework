package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.CellData;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Sheet;

import java.io.IOException;

public class Document {
    private GoogleSheets service;

    private String spreadsheetId;

    public Document(GoogleSheets service, String spreadsheetId) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
    }

    public Sheet getActiveSheet(){
        return service.getSpreadsheet(spreadsheetId).getActiveSheet();
    }

    public CellData getCellData(String cell1Ref) throws IOException {
        return service.getCellData(spreadsheetId, cell1Ref);
    }

    public void setCellData(String cell1Ref, CellData cellData) throws IOException {
        service.setCellData(spreadsheetId, cell1Ref, cellData);
    }
}
