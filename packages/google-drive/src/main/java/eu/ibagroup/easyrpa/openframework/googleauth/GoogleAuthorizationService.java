package eu.ibagroup.easyrpa.openframework.googleauth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.googleauth.constants.GAuthConfigParam;
import eu.ibagroup.easyrpa.openframework.googleauth.exceptions.GoogleAuthException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

public class GoogleAuthorizationService {

    private static final String DEFAULT_USER_ID = "user";
    private static final String DEFAULT_TOKEN_STORES_DIRECTORY_PATH = "tokens";
    private static final String DEFAULT_VERIFICATION_CODE_RECEIVER = "localhost:8888";

    private HttpTransport httpTransport;

    private JsonFactory jsonFactory;

    private String userId;

    private String secret;

    private String tokenStoresDir;

    private String verCodeReceiver;

    private AuthorizationPerformer authorizationPerformer;

    private RPAServicesAccessor rpaServices;

    public GoogleAuthorizationService() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            jsonFactory = JacksonFactory.getDefaultInstance();
        } catch (Exception e) {
            throw new GoogleAuthException("initialization of Google authorization service has failed.", e);
        }
    }

    public GoogleAuthorizationService(RPAServicesAccessor rpaServices) {
        this();
        this.rpaServices = rpaServices;
    }

    public HttpTransport getHttpTransport() {
        return httpTransport;
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public String getUserId() {
        if (userId == null) {
            userId = getConfigParam(GAuthConfigParam.SECRET);
            if (userId == null) {
                secret = DEFAULT_USER_ID;
            }
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(GAuthConfigParam.SECRET);
            if (secretAlias != null) {
                secret = rpaServices.getSecret(secretAlias, String.class);
            }
        }
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTokenStoresDir() {
        if (tokenStoresDir == null) {
            tokenStoresDir = getConfigParam(GAuthConfigParam.TOKEN_STORES_DIR);
            if (tokenStoresDir == null) {
                tokenStoresDir = DEFAULT_TOKEN_STORES_DIRECTORY_PATH;
            }
        }
        return tokenStoresDir;
    }

    public void setTokenStoresDir(String tokenStoresDir) {
        this.tokenStoresDir = tokenStoresDir;
    }

    public String getVerCodeReceiver() {
        if (verCodeReceiver == null) {
            verCodeReceiver = getConfigParam(GAuthConfigParam.VERIFICATION_CODE_RECEIVER);
            if (verCodeReceiver == null) {
                verCodeReceiver = DEFAULT_VERIFICATION_CODE_RECEIVER;
            }
        }
        return verCodeReceiver;
    }

    public void setVerCodeReceiver(String verCodeReceiver) {
        this.verCodeReceiver = verCodeReceiver;
    }

    public void setAuthorizationPerformer(AuthorizationPerformer authorizationPerformer) {
        this.authorizationPerformer = authorizationPerformer;
    }

    public Credential getCredentials() {
        try {
            return createAuthorizationFlow(getSecret()).authorize(getUserId());
        } catch (Exception e) {
            throw new GoogleAuthException("Google authorization has failed.", e);
        }
    }

    private GoogleAuthorizationFlow createAuthorizationFlow(String secret) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new StringReader(secret));
        DataStoreFactory dataStoreFactory = new FileDataStoreFactory(new java.io.File(getTokenStoresDir()));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(httpTransport, jsonFactory, clientSecrets, Collections.singletonList(DriveScopes.DRIVE))
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();

        String[] receiverHostAndPort = getVerCodeReceiver().split(":");
        String receiverHost = receiverHostAndPort[0];
        int receiverPort = receiverHostAndPort.length > 1 ? Integer.parseInt(receiverHostAndPort[1]) : 8888;

        VerificationCodeReceiver receiver = new LocalServerReceiver.Builder()
                .setHost(receiverHost).setPort(receiverPort).build();

        return new GoogleAuthorizationFlow(flow, receiver);
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found or {@link RPAServicesAccessor} is not defined.
     */
    private String getConfigParam(String key) {
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

    private class GoogleAuthorizationFlow extends AuthorizationCodeInstalledApp {

        public GoogleAuthorizationFlow(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
            super(flow, receiver);
        }

        @Override
        protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) {
            String url = authorizationUrl.build();
            if (authorizationPerformer != null) {
                authorizationPerformer.perform(url);
            } else {
                browse(url);
            }
        }
    }
}
