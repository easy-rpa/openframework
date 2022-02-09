package eu.ibagroup.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import eu.ibagroup.easyrpa.openframework.google.services.AuthorizationPerformer;
import eu.ibagroup.easyrpa.openframework.google.services.GoogleServicesProvider;

import javax.inject.Inject;

public class GoogleSheets {

    private Sheets service;

    private GoogleServicesProvider googleServicesProvider;

    public GoogleSheets() {
        googleServicesProvider = new GoogleServicesProvider();
    }

    @Inject
    public GoogleSheets(GoogleServicesProvider googleServicesProvider) {
        this.googleServicesProvider = googleServicesProvider;
    }

    public GoogleSheets onAuthorization(AuthorizationPerformer authorizationPerformer) {
        googleServicesProvider.onAuthorization(authorizationPerformer);
        service = null;
        return this;
    }

    public GoogleSheets secret(String vaultAlias) {
        googleServicesProvider.secret(vaultAlias);
        return this;
    }

    public GoogleSheets secret(String userId, String secret) {
        googleServicesProvider.secret(userId, secret);
        service = null;
        return this;
    }

    public SpreadsheetDocument getSpreadsheet(String spreadsheetId) {
        initService();
        return new SpreadsheetDocument(service, spreadsheetId);
    }

    private void initService() {
        if (service == null) {
            service = googleServicesProvider.getService(Sheets.class, SheetsScopes.SPREADSHEETS);
        }
    }
}
