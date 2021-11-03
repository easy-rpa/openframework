package eu.ibagroup.easyrpa.openframework.googledrive.utils;


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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.googledrive.exceptions.GoogleDriveInstanceCreationException;
import eu.ibagroup.easyrpa.openframework.googledrive.exceptions.HttpTransportCreationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveServiceProvider {
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private NetHttpTransport HTTP_TRANSPORT;

    private String filePath;

    private List<String> scopes;

    public GoogleDriveServiceProvider() {
    }

    public List<String> getScopes() {
        return scopes == null ? SCOPES : scopes;
    }

    public GoogleDriveServiceProvider setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    public GoogleDriveServiceProvider setCredentials(String filePath) {
        this.filePath = filePath;
        return this;
    }

    private void setHttpTransport() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException e) {
            throw new HttpTransportCreationException("HttpTransport initialization failed");
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        String path = filePath == null ? CREDENTIALS_FILE_PATH : filePath;
        List<String> scopeList = scopes == null ? SCOPES : scopes;

        InputStream in = GoogleDriveServiceProvider.class.getResourceAsStream(path);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + filePath);
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

    public GoogleDriveService connect() {
        setHttpTransport();
        Drive drive;
        try {
            drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).build();
        } catch (IOException e) {
            throw new GoogleDriveInstanceCreationException("creation failed");
        }
        return new GoogleDriveService(drive);
    }
}
