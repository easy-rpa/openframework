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
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.GoogleSheetsInstanceCreationException;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SpreadsheetNotFound;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SpreadsheetRequestFailed;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.UpdateException;
import eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.Spreadsheet;

import javax.inject.Inject;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheets {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    // provides read and write access by default
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private NetHttpTransport HTTP_TRANSPORT;

    private String credString;

    private List<String> scopes;

    private Sheets service;

    @Inject
    public GoogleSheets(RPAServicesAccessor rpaServices) {
        this.credString = rpaServices.getSecret("google.credentials", String.class);
        connect();
    }

    public GoogleSheets() {
    }

    public List<String> getScopes() {
        return scopes == null ? SCOPES : scopes;
    }

    public GoogleSheets setScopes(List<String> scopes) {
        service = null;
        this.scopes = scopes;
        return this;
    }

    public GoogleSheets setSecret(String secret) {
        service = null;
        this.credString = secret;
        return this;
    }

    public Spreadsheet getSpreadsheet(String spreadsheetId) {
        com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet;
        try {
            spreadsheet = service.spreadsheets().get(spreadsheetId).setIncludeGridData(true).execute();
        } catch (IOException e) {
            throw new SpreadsheetNotFound("Spreadsheet with such id not found");
        }
        if (spreadsheet == null) {
            throw new SpreadsheetRequestFailed("Some errors occurred");
        }
        return new Spreadsheet(spreadsheet, this);
    }

    public Spreadsheet copySheet(String spreadsheetId, int sheetId, String destSpreadsheetId) {
        //incorrect idea!

        CopySheetToAnotherSpreadsheetRequest requestBody = new CopySheetToAnotherSpreadsheetRequest();
        requestBody.setDestinationSpreadsheetId(destSpreadsheetId);

        Sheets.Spreadsheets.SheetsOperations.CopyTo request;
        SheetProperties response = null;
        try {
            request = service.spreadsheets().sheets().copyTo(spreadsheetId, sheetId, requestBody);
            response = request.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response);
        return null;
    }

    public BatchUpdateSpreadsheetResponse update(Spreadsheet spreadsheet) {
        List<Request> requests = spreadsheet.getRequests();
        if (requests.size() > 0) {
            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);

            try {
                BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheet.getId(), body).execute();
                requests.clear();
                return response;
            } catch (IOException e) {
                throw new UpdateException(e.getMessage());
            }
        }
        //return null if there were no updates
        return null;
    }

    private void connect() {
        if (service == null) {
            try {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                this.service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).build();
            } catch (IOException | GeneralSecurityException e) {
                throw new GoogleSheetsInstanceCreationException("creation failed");
            }
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        List<String> scopeList = scopes == null ? SCOPES : scopes;

        InputStream in = new ByteArrayInputStream(credString.getBytes());
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
}
