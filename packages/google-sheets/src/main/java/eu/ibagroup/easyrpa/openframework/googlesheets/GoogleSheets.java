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
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.GoogleSheetsInstanceCreationException;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SpreadsheetNotFound;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.SpreadsheetRequestFailed;
import eu.ibagroup.easyrpa.openframework.googlesheets.internal.GSpreadsheetDocumentElementsCache;

import javax.inject.Inject;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheets {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String DEFAULT_CONFIGURATION_NAME = "google.credentials";

    private String secret;

    private List<String> scopes;

    private Sheets service;

    private RPAServicesAccessor rpaServices;

    @Inject
    public GoogleSheets(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
        String aliasValue = getConfigParam(DEFAULT_CONFIGURATION_NAME);
        this.secret = rpaServices.getSecret(aliasValue, String.class);
    }

    public GoogleSheets() {
    }


    public List<String> getScopes() {
        return scopes == null ? SCOPES : scopes;
    }

    public void setScopes(List<String> scopes) {
        service = null;
        this.scopes = scopes;
    }

    public GoogleSheets scopes(List<String> scopes) {
        setScopes(scopes);
        return this;
    }

    public void setSecret(String secret) {
        service = null;
        this.secret = secret;
    }

    public GoogleSheets secret(String secret) {
        setSecret(secret);
        return this;
    }

    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(DEFAULT_CONFIGURATION_NAME);
            if (secretAlias != null) {
                secret = rpaServices.getSecret(secretAlias, String.class);
            }
        }
        return secret;
    }

    public SpreadsheetDocument getSpreadsheet(String spreadsheetId) {
        connect();
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
        GSpreadsheetDocumentElementsCache.register(spreadsheet.getSpreadsheetId(), spreadsheet);
        return new SpreadsheetDocument(spreadsheet, service);
    }

    public void connect() {
        if (service == null) {
            try {
                NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                this.service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).build();
            } catch (IOException | GeneralSecurityException e) {
                throw new GoogleSheetsInstanceCreationException("Creation failed");
            }
        }
    }

    protected String getConfigParam(String key) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getConfigParam(key);
        } catch (Exception e) {
            //do nothing
        }

        return result;
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        List<String> scopeList = scopes == null ? SCOPES : scopes;
        String secret = this.secret == null ? getSecret() : this.secret;

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
}
