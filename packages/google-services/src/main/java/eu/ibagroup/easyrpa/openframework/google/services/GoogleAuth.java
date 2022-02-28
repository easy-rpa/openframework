package eu.ibagroup.easyrpa.openframework.google.services;

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
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.google.services.constants.GServicesConfigParam;
import eu.ibagroup.easyrpa.openframework.google.services.exceptions.GoogleAuthException;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class GoogleAuth {

    private static final String DEFAULT_USER_ID = "user";
    private static final String DEFAULT_TOKEN_STORES_DIRECTORY_PATH = "tokens";
    private static final String DEFAULT_VERIFICATION_CODE_RECEIVER = "localhost:8888";

    private final HttpTransport httpTransport;

    private final JsonFactory jsonFactory;

    private String userId;

    private String secret;

    private String tokenStoresDir;

    private String verCodeReceiver;

    private AuthorizationPerformer authorizationPerformer;

    private RPAServicesAccessor rpaServices;

    /**
     * Construct instance of <code>GoogleAuth</code> class using HTTP trusted transport and json factory.
     */
    public GoogleAuth() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            jsonFactory = JacksonFactory.getDefaultInstance();
        } catch (Exception e) {
            throw new GoogleAuthException("initialization of Google authorization service has failed.", e);
        }
    }

    /**
     * Construct instance of <code>GoogleAuth</code> using configuration parameters from accessor.
     *
     * @param rpaServices the service to provide parameters.
     */
    @Inject
    public GoogleAuth(RPAServicesAccessor rpaServices) {
        this();
        this.rpaServices = rpaServices;
    }

    /**
     * @return reference to HTTP Transport.
     */
    public HttpTransport getHttpTransport() {
        return httpTransport;
    }

    /**
     * @return reference to Json Factory.
     */
    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    /**
     * @return the user id from configuration parameters.
     */
    public String getUserId() {
        if (userId == null) {
            userId = getConfigParam(GServicesConfigParam.AUTH_SECRET);
            if (userId == null) {
                secret = DEFAULT_USER_ID;
            }
        }
        return userId;
    }

    /**
     * Set user id.
     *
     * @param userId new user id.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the secret from configuration parameters.
     */
    public String getSecret() {
        if (secret == null) {
            String secretAlias = getConfigParam(GServicesConfigParam.AUTH_SECRET);
            if (secretAlias != null) {
                secret = rpaServices.getSecret(secretAlias, String.class);
            }
        }
        return secret;
    }

    /**
     * Set new secret.
     *
     * @param secret new secret.
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * @return the path to StoredCredential token from configuration parameters.
     */
    public String getTokenStoresDir() {
        if (tokenStoresDir == null) {
            tokenStoresDir = getConfigParam(GServicesConfigParam.AUTH_TOKEN_STORES_DIR);
            if (tokenStoresDir == null) {
                tokenStoresDir = DEFAULT_TOKEN_STORES_DIRECTORY_PATH;
            }
        }
        return tokenStoresDir;
    }

    /**
     * Set path to StoredCredential token.
     *
     * @param tokenStoresDir new path.
     */
    public void setTokenStoresDir(String tokenStoresDir) {
        this.tokenStoresDir = tokenStoresDir;
    }

    /**
     * Get host and port for local server receiver from configuration parameters.
     *
     * @return host:port string value.
     */
    public String getVerCodeReceiver() {
        if (verCodeReceiver == null) {
            verCodeReceiver = getConfigParam(GServicesConfigParam.AUTH_VERIFICATION_CODE_RECEIVER);
            if (verCodeReceiver == null) {
                verCodeReceiver = DEFAULT_VERIFICATION_CODE_RECEIVER;
            }
        }
        return verCodeReceiver;
    }

    /**
     * Set new <code>host:port</code> string value as local server receiver.
     *
     * @param verCodeReceiver new <code>host:port</code> value
     */
    public void setVerCodeReceiver(String verCodeReceiver) {
        this.verCodeReceiver = verCodeReceiver;
    }

    /**
     * Set authorization performer.
     *
     * @param authorizationPerformer new authorization performer.
     */
    public void setAuthorizationPerformer(AuthorizationPerformer authorizationPerformer) {
        this.authorizationPerformer = authorizationPerformer;
    }

    /**
     * Attempts to create StoredCredential token with given scopes.
     * <p>
     * If StoredCredential token is not present, human will be asked to approve requested access by given scopes.
     * If scopes have been changed, or package is updated then human will be asked again.
     *
     * @param scopes list of requested scopes.
     * @return Credential object
     * @see com.google.api.services.drive.DriveScopes
     * @see <a href="https://developers.google.com/identity/protocols/oauth2#expiration">Refresh token expiration</a>
     */
    public Credential authorize(List<String> scopes) {
        try {
            return createAuthorizationFlow(getSecret(), scopes).authorize(getUserId());
        } catch (Exception e) {
            throw new GoogleAuthException("Google authorization has failed.", e);
        }
    }

    private GoogleAuthorizationFlow createAuthorizationFlow(String secret, List<String> scopes) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new StringReader(secret));
        DataStoreFactory dataStoreFactory = new FileDataStoreFactory(new java.io.File(getTokenStoresDir()));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(httpTransport, jsonFactory, clientSecrets, scopes)
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
