package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.googlesheets.constants.BatchUpdateCellStyles;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.CopySheetException;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.GoogleSheetsInstanceCreationException;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SpreadsheetNotFound;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SpreadsheetRequestFailed;
import eu.ibagroup.easyrpa.openframework.googlesheets.internal.GSheetElementsCache;

import javax.inject.Inject;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleSheets {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String DEFAULT_ALIAS_VALUE = "google.credentials";

    private String secret;

    private List<String> scopes;

    private Sheets service;

    private String alias;

    @Inject
    public GoogleSheets(RPAServicesAccessor rpaServices) {
        this.secret = rpaServices.getSecret(DEFAULT_ALIAS_VALUE, String.class);
    }

    public GoogleSheets() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        service = null;
        this.alias = alias;
        //this.credString =
    }

    public GoogleSheets alias(String alias){
        setAlias(alias);
        return this;
    }

    public List<String> getScopes() {
        return scopes == null ? SCOPES : scopes;
    }

    public void setScopes(List<String> scopes) {
        service = null;
        this.scopes = scopes;
    }

    public GoogleSheets scopes(List<String> scopes){
        setScopes(scopes);
        return this;
    }

    public void setSecret(String secret) {
        service = null;
        this.secret = secret;
    }
    public GoogleSheets secret(String secret){
        setSecret(secret);
        return this;
    }

    public SpreadsheetDocument getSpreadsheet(String spreadsheetId) {
        com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet;
        try {
            Sheets.Spreadsheets.Get s = service.spreadsheets().get(spreadsheetId);
            s.getSpreadsheetId();
            spreadsheet = service.spreadsheets().get(spreadsheetId).setIncludeGridData(true).execute();
        } catch (IOException e) {
            throw new SpreadsheetNotFound("Spreadsheet with such id not found", e);
        }
        if (spreadsheet == null) {
            throw new SpreadsheetRequestFailed("Some errors occurred");
        }
        GSheetElementsCache.register(spreadsheet.getSpreadsheetId(),spreadsheet);
        return new SpreadsheetDocument(spreadsheet, service);
    }

    public void copySheet(SpreadsheetDocument spreadsheetDocumentFrom, Sheet sheet, SpreadsheetDocument spreadsheetDocumentTo) {
        CopySheetToAnotherSpreadsheetRequest requestBody = new CopySheetToAnotherSpreadsheetRequest();
        requestBody.setDestinationSpreadsheetId(spreadsheetDocumentTo.getId());
        try {
            service.spreadsheets().sheets().copyTo(spreadsheetDocumentFrom.getId(), sheet.getId(), requestBody).execute();
        } catch (IOException e) {
            throw new CopySheetException(e.getMessage());
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        List<String> scopeList = scopes == null ? SCOPES : scopes;

        InputStream in = new ByteArrayInputStream(secret.getBytes());
        if (in == null) {
            throw new FileNotFoundException("Credentials not found: ");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopeList)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public ValueRange getValues(String spreadsheetId, String range) throws IOException {
        Sheets service = this.service;
        ValueRange result = service.spreadsheets().values().get(spreadsheetId, range).execute();
        int numRows = result.getValues() != null ? result.getValues().size() : 0;
        System.out.printf("%d rows retrieved.", numRows);
        return result;
    }

    public CellData getCellData(String spreadsheetId, String cellRef)
            throws IOException {
        CellRef ref = new CellRef(cellRef);

        Sheets service = this.service;
//        service.spreadsheets().

        return service.spreadsheets().get(spreadsheetId).setIncludeGridData(true).execute().getSheets().get(0).getData().get(0).getRowData().get(ref.getRow()).getValues().get(ref.getCol());
    }

    public UpdateValuesResponse updateValues(String spreadsheetId, String range,
                                             String valueInputOption, List<List<Object>> _values)
            throws IOException {
        connect();
        Sheets service = this.service;
        List<List<Object>> values;
        values = _values;
        ValueRange body = new ValueRange()
                .setValues(values);

        return service.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption(valueInputOption)
                .execute();
    }

    public BatchUpdateValuesResponse batchUpdateValues(String spreadsheetId, String range,
                                                       String valueInputOption,
                                                       List<List<Object>> values)
            throws IOException {
        connect();
        Sheets service = this.service;

        fixNullValues(values);

        List<ValueRange> data = new ArrayList<>();
        data.add(new ValueRange()
                .setRange(range)
                .setValues(values));
        // Additional ranges to update ...

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption(valueInputOption)
                .setData(data);
        BatchUpdateValuesResponse result =
                service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();

        System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
        return result;
    }

    private void fixNullValues(List<List<Object>> values) {
        for(List row : values){
            for(int i = 0; i< row.size(); i++){
                if(row.get(i) == null){
                    row.set(i, "");
                }
            }
        }
    }

    public BatchUpdateValuesResponse insertRows(String spreadsheetId, String range,
                                                String valueInputOption,
                                                List<?> data)throws IOException {

        if (!(data.get(0) instanceof List)) {
            data = Collections.singletonList(data);
        }
        return batchUpdateValues(spreadsheetId, range, valueInputOption, (List<List<Object>>) data);
    }

    public void setBackground(String spreadsheetId, String range, GSheetColor color) throws IOException {
        connect();
        CellRange rng = new CellRange(range);
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                                .setCell(new CellData()
                                        //.setUserEnteredValue( new ExtendedValue().setStringValue("cell text"))
                                        .setEffectiveValue(new ExtendedValue().setStringValue("cell text2"))
                                        .setUserEnteredFormat(new CellFormat()
                                                .setBackgroundColor(color.toNativeColor()
                                                )
                                                .setTextFormat(new TextFormat()
                                                        .setFontSize(15)
                                                        .setBold(Boolean.TRUE)
                                                )
                                        )
                                )
                                .setRange(new GridRange()
                                        .setSheetId(getSpreadsheet(spreadsheetId).getActiveSheet().getId())
                                        .setStartRowIndex(rng.getFirstRow())
                                        .setEndRowIndex(rng.getLastRow()+1)
                                        .setStartColumnIndex(rng.getFirstCol())
                                        .setEndColumnIndex(rng.getLastCol()+1)
                                )
                                .setFields("userEnteredFormat.backgroundColor,userEnteredFormat.textFormat ")
                        //.setFields("*")
                )
        );
        BatchUpdateSpreadsheetRequest bodyReq =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, bodyReq).execute();
    }

    public void setTextColor(String spreadsheetId, String range, GSheetColor color) throws IOException {
        connect();
        CellRange rng = new CellRange(range);
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                                .setCell(new CellData()
                                        //.setUserEnteredValue( new ExtendedValue().setStringValue("cell text"))
                                        .setEffectiveValue(new ExtendedValue().setStringValue("cell text2"))
                                        .setUserEnteredFormat(new CellFormat()
                                                .setTextFormat(new TextFormat()
                                                        .setForegroundColor(color.toNativeColor())
                                                )
                                        )
                                )
                                .setRange(new GridRange()
                                        .setSheetId(getSpreadsheet(spreadsheetId).getActiveSheet().getId())
                                        .setStartRowIndex(rng.getFirstRow())
                                        .setEndRowIndex(rng.getLastRow()+1)
                                        .setStartColumnIndex(rng.getFirstCol())
                                        .setEndColumnIndex(rng.getLastCol()+1)
                                )
                                .setFields("userEnteredFormat.textFormat.foregroundColor")
                        //.setFields("*")
                )
        );

        BatchUpdateSpreadsheetRequest bodyReq =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId ,bodyReq).execute();

    }

    public void setCellData(String spreadsheetId, String cell1Ref, CellData cellData) throws IOException {
        connect();
        CellRef ref = new CellRef(cell1Ref);
        List<Request> requests = new ArrayList<>();
        System.out.println(cell1Ref);
        requests.add(new Request()
                        .setRepeatCell(new RepeatCellRequest()
                                        .setCell(cellData)
                                        .setRange(new GridRange()
                                                .setSheetId(getSpreadsheet(spreadsheetId).getActiveSheet().getId())
                                                .setStartRowIndex(ref.getRow())
                                                .setEndRowIndex(ref.getRow()+1)
                                                .setStartColumnIndex(ref.getCol())
                                                .setEndColumnIndex(ref.getCol()+1)
                                        )
                                        .setFields(BatchUpdateCellStyles.ALL_CELL_STYLE_FIELDS)
//                        .setFields("*")
                        )
        );

        BatchUpdateSpreadsheetRequest bodyReq =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId ,bodyReq).execute();
    }

    private void connect() {
        if (service == null) {
            try {
                NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                this.service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).build();
            } catch (IOException | GeneralSecurityException e) {
                throw new GoogleSheetsInstanceCreationException("Creation failed");
            }
        }
    }

}
