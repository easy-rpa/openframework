package eu.ibagroup.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.google.services.AuthorizationPerformer;
import eu.ibagroup.easyrpa.openframework.google.services.GoogleServicesProvider;

import javax.inject.Inject;
import java.io.InputStream;

/**
 * Service that provides convenient way to work with Google Sheets API.
 */
public class GoogleSheets {

    /**
     * Instance of Google Sheets service.
     */
    private Sheets sheetsService;

    /**
     * Helper to perform authorization for using of Google Sheets API on behalf of specific user and instantiation
     * of Google Sheets service.
     */
    private GoogleServicesProvider googleServicesProvider;

    /**
     * Default constructor for GoogleSheets.
     * <p>
     * This constructor should be used in case of manual providing of secret information necessary for authorization
     * and instantiation Google Sheets service. E.g.:
     * <pre>
     * String spreadsheetFileId = ...;
     * String secretJson = new String(Files.readAllBytes(Paths.get("secret.json")), StandardCharsets.UTF_8);
     *
     * GoogleSheets googleSheets = new GoogleSheets().secret("user1", secretJson);
     * SpreadsheetDocument doc = googleSheets.getSpreadsheet(spreadsheetFileId);
     *  ...
     * </pre>
     */
    public GoogleSheets() {
        googleServicesProvider = new GoogleServicesProvider();
    }

    /**
     * Constructs GoogleSheets with provided {@link GoogleServicesProvider}.
     * <p>
     * This constructor is used in case of injecting of this GoogleSheets using <code>@Inject</code> annotation.
     * In this case {@code GoogleServicesProvider} also will be initialised via injecting of
     * {@link RPAServicesAccessor} that is used for getting of necessary secret information from secret vault of
     * RPA platform. E.g.:
     * <pre>
     * {@code @Inject}
     *  private GoogleSheets googleSheets;
     *
     *  public void execute() {
     * String spreadsheetFileId = ...;
     * SpreadsheetDocument doc = googleSheets.getSpreadsheet(spreadsheetFileId);
     *  ...
     *  }
     * </pre>
     *
     * @param googleServicesProvider instance of {@link GoogleServicesProvider} that helps with authorization and
     *                               instantiation of Google Sheets service.
     * @see Inject
     */
    @Inject
    public GoogleSheets(GoogleServicesProvider googleServicesProvider) {
        this.googleServicesProvider = googleServicesProvider;
    }

    /**
     * Allows to override the way how this service informs the user that it wishes to act on his behalf and obtain
     * corresponding access token from Google.
     * <p>
     * By default it opens a browser on machine where this code is running and locates to OAuth consent page where
     * user should authorize performing of necessary operations. If this code is running on robot's machine performing
     * of authorization by this way is not possible since user won't able to see the browser page.
     * <p>
     * Using this method is possible to overrides this behavior and specify, lets say, sending of notification email
     * with link to OAuth consent page to administrator, who is able to perform authorization on behalf of robot's
     * Google account. In this case robot will be able to access Google services on behalf of his account. Any time
     * when access token is invalid administrator will get such email and let robot to continue his work. E.g.:
     * <pre>
     * <code>@Inject</code>
     * SomeAuthorizationRequiredEmail authorizationRequiredEmail;
     * ...
     *
     * googleSheets.onAuthorization(url->{
     *    authorizationRequiredEmail.setConsentPage(url).send();
     * });
     *
     * ...
     * </pre>
     *
     * @param authorizationPerformer lambda expression or instance of {@link AuthorizationPerformer} that defines
     *                               specific behavior of authorization step.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleSheets onAuthorization(AuthorizationPerformer authorizationPerformer) {
        googleServicesProvider.onAuthorization(authorizationPerformer);
        sheetsService = null;
        return this;
    }

    /**
     * Sets explicitly the alias of secret vault entry with OAuth 2.0 Client JSON necessary for authentication on the
     * Google server.
     * <p>
     * For information regarding how to configure OAuth 2.0 Client see
     * <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a>
     *
     * @param vaultAlias the alias of secret vault entry with OAuth 2.0 Client JSON to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleSheets secret(String vaultAlias) {
        googleServicesProvider.secret(vaultAlias);
        return this;
    }

    /**
     * Sets explicitly the secret OAuth 2.0 Client JSON necessary for authentication on the Google server.
     * <p>
     * The OAuth 2.0 Client JSON look like the following:
     * <pre>
     * {
     *     "installed": {
     *       "client_id": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com",
     *       "project_id": "XXXXXXX-XXXXXX",
     *       "auth_uri": "https://accounts.google.com/o/oauth2/auth",
     *       "token_uri": "https://oauth2.googleapis.com/token",
     *       "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
     *       "client_secret": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
     *       "redirect_uris": [
     *           "urn:ietf:wg:oauth:2.0:oob",
     *           "http://localhost"
     *       ]
     *     }
     * }
     * </pre>
     * For information regarding how to configure OAuth 2.0 Client see
     * <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a><br>
     *
     * @param userId user unique identifier that will be associated with secret information. This is is used as key
     *               to store access token in StoredCredentials file.
     * @param secret JSON string with secret information to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleSheets secret(String userId, String secret) {
        googleServicesProvider.secret(userId, secret);
        sheetsService = null;
        return this;
    }

    /**
     * Gets content of Google spreadsheet file with specified file ID.
     *
     * @param spreadsheetId the Google file ID of necessary Google spreadsheet to get.
     * @return instance of {@link SpreadsheetDocument} representing the Google spreadsheet.
     */
    public SpreadsheetDocument getSpreadsheet(String spreadsheetId) {
        initService();
        return new SpreadsheetDocument(sheetsService, spreadsheetId);
    }

    /**
     * Gets content of Google spreadsheet file as XLSX file.
     *
     * @param spreadsheetId the Google file ID of necessary Google spreadsheet to get.
     * @return the input stream with content of Google spreadsheet file in XLSX format.
     */
    public InputStream getSpreadsheetAsXLSX(String spreadsheetId) {
        //TODO Implement this.
        // Use service.getRequestFactory().buildGetRequest().execute() to let this service to provide  Oauth token automatically
        // on background via interceptor
//        try {
//            HttpResponse response = service.getRequestFactory().buildGetRequest(new GenericUrl("asas")).execute();
//        }catch (Exception e){
//            //
//        }
//        var formattedDate = Utilities.formatDate(new Date(), "CET", "yyyy-MM-dd' 'HH:mm");
//        var name = "Backup Copy " + formattedDate;
//        var destination = DriveApp.getFolderById("1vFL98cgKdMHLNLSc542pUt4FMRTthUvL");
//
//        // Added
//        var sheetId = "2SqIXLiic6-gjI2KwQ6OIgb-erbl3xqzohRgE06bfj2c";
//        var url = "https://docs.google.com/spreadsheets/d/" + sheetId + "/export?format=xlsx&access_token=" + ScriptApp.getOAuthToken();
//        var blob = UrlFetchApp.fetch(url).getBlob().setName(name + ".xlsx"); // Modified
//        var res = UrlFetchApp.fetch(url, {headers: {Authorization: "Bearer " + ScriptApp.getOAuthToken()}});
//        destination.createFile(blob);
        throw new UnsupportedOperationException();
    }

    /**
     * Using {@link GoogleServicesProvider} performs authentication on Google server, authorization of using Google
     * Sheets API and instantiation of Google Sheets service.
     */
    private void initService() {
        if (sheetsService == null) {
            sheetsService = googleServicesProvider.getService(Sheets.class, SheetsScopes.SPREADSHEETS);
        }
    }
}
