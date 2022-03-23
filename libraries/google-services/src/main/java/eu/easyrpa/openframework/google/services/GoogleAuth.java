package eu.easyrpa.openframework.google.services;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.easyrpa.openframework.core.utils.TypeUtils;
import eu.easyrpa.openframework.google.services.constants.GServicesConfigParam;
import eu.easyrpa.openframework.google.services.exceptions.GoogleAuthException;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Authentication and authorization helper for Google API services.
 * <p>
 * Helps to perform configuration and retrieving of necessary secret information, authentication on Google server and
 * authorization to using some Google API service on behalf of specific Google account.
 */
public class GoogleAuth {

    private static final String DEFAULT_USER_ID = "user";
    private static final String DEFAULT_TOKEN_STORES_DIRECTORY_PATH = "tokens";
    private static final String DEFAULT_VERIFICATION_CODE_RECEIVER = "localhost:8888";

    /**
     * Reference to used HTTP Transport service.
     */
    private final HttpTransport httpTransport;

    /**
     * Reference to used Json Factory.
     */
    private final JsonFactory jsonFactory;

    /**
     * User unique identifier that is associated with secret information. This is used as key to store access token
     * in StoredCredentials file.
     */
    private String userId;

    /**
     * Secret information necessary to perform authentication on Google server.
     */
    private String secret;

    /**
     * Path to directory where StoredCredentials file should be created and located.
     */
    private String tokenStoresDir;

    /**
     * Host and port of authorization verification code receiver.
     */
    private String verCodeReceiver;

    /**
     * Instance of function that defines specific behavior of authorization step.
     */
    private AuthorizationPerformer authorizationPerformer;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Default constructor for {@code GoogleAuth}.
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
     * Constructs {@code GoogleAuth} with provided {@link RPAServicesAccessor}.
     * <p>
     * This constructor is used in case of injecting of this GoogleAuth using {@link Inject} annotation.
     *
     * @param rpaServices instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     */
    @Inject
    public GoogleAuth(RPAServicesAccessor rpaServices) {
        this();
        this.rpaServices = rpaServices;
    }

    /**
     * @return reference to used HTTP Transport service.
     */
    public HttpTransport getHttpTransport() {
        return httpTransport;
    }

    /**
     * @return reference to used Json Factory.
     */
    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    /**
     * Gets user unique identifier that is associated with secret information.
     * <p>
     * This is used as key to store access token in StoredCredentials file.
     *
     * @return the user id that is associated with secret information.
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
     * Sets user unique identifier that should be associated with secret information.
     *
     * @param userId the user id to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets secret OAuth 2.0 Client JSON that is used for authentication on the Google server.
     * <p>
     * If the secret is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b>{@code "google.services.auth.secret"}</b>.
     * <p>
     * For information regarding how to configure OAuth 2.0 Client see
     * <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a>
     *
     * @return JSON string with used secret information.
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
     * Sets explicitly the secret OAuth 2.0 Client JSON that should be used for authentication on the Google server.
     * <p>
     * For information regarding how to configure OAuth 2.0 Client see
     * <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a>
     *
     * @param secret the JSON string with secret information to use.
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Gets path to directory where StoredCredentials file will be created and located.
     * <p>
     * If the path is not specified explicitly then it will be looked up in configurations parameters of the
     * RPA platform under the key <b>{@code "google.services.auth.token.stores.dir"}</b>.
     *
     * @return the path to directory where StoredCredentials file will be created and located.
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
     * Sets explicitly the path to directory where StoredCredentials file should be created and located.
     *
     * @param tokenStoresDir the path to set.
     */
    public void setTokenStoresDir(String tokenStoresDir) {
        this.tokenStoresDir = tokenStoresDir;
    }

    /**
     * Gets host and port of authorization verification code receiver.
     * <p>
     * If the host and port are not specified explicitly then they will be looked up in configurations parameters of the
     * RPA platform under the key <b>{@code "google.services.auth.verification.code.receiver"}</b>.
     *
     * @return host and port of authorization verification code receiver.
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
     * Sets explicitly host and port of authorization verification code receiver.
     *
     * @param verCodeReceiver the {@code host:port} string to set.
     */
    public void setVerCodeReceiver(String verCodeReceiver) {
        this.verCodeReceiver = verCodeReceiver;
    }

    /**
     * Sets lambda expression or instance of {@link AuthorizationPerformer} that allows to override the way how this
     * code informs the user that it wishes to act on his behalf and obtain corresponding access token from Google.
     * <p>
     * By default it opens a browser on machine where this code is running and locates to OAuth consent page where
     * user should authorize performing of necessary operations. If this code is running on robot's machine performing
     * of authorization by this way is not possible since user won't able to see the browser page.
     * <p>
     * Using this method is possible to overrides this behavior and specify, lets say, sending of notification email
     * with link to OAuth consent page to administrator, who is able to perform authorization on behalf of robot's
     * Google account. In this case robot will be able to access Google services on behalf of his account. Any time
     * when access token is invalid administrator will get such email and let robot to continue his work.
     *
     * @param authorizationPerformer lambda expression or instance of {@link AuthorizationPerformer} that defines
     *                               specific behavior of authorization step.
     */
    public void setAuthorizationPerformer(AuthorizationPerformer authorizationPerformer) {
        this.authorizationPerformer = authorizationPerformer;
    }

    /**
     * Authorizes the code to do actions from given scopes on user behalf.
     * <p>
     * At first it tries to check StoredCredential file. If it contains a valid access token that was granted before
     * then it continues the work with this access token. Otherwise, user will be asked to approve requested access
     * by given scopes.
     *
     * @param scopes list of requested scopes.
     * @return a new instance of Google requests initializer that is responsible for providing of valid access token
     * for each request to Google server within given scopes.
     * @see <a href="https://developers.google.com/identity/protocols/oauth2#expiration">Refresh token expiration</a>
     */
    public HttpRequestInitializer authorize(List<String> scopes) {
        try {
            return new GoogleRequestInitializer(getUserId(), createAuthorizationFlow(getSecret(), scopes));
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

    private static class GoogleRequestInitializer implements HttpRequestInitializer, HttpExecuteInterceptor {

        private String userId;
        private GoogleAuthorizationFlow authorizationFlow;
        private Credential credential;

        public GoogleRequestInitializer(String userId, GoogleAuthorizationFlow authorizationFlow) throws IOException {
            this.userId = userId;
            this.authorizationFlow = authorizationFlow;
            this.credential = authorizationFlow.authorize(userId);
        }

        @Override
        public void initialize(HttpRequest request) {
            request.setInterceptor(this);
            request.setUnsuccessfulResponseHandler(credential);
        }

        @Override
        public void intercept(HttpRequest request) throws IOException {
            try {
                credential.intercept(request);
            } catch (TokenResponseException e) {
                if (e.getDetails().containsValue("invalid_grant")) {
                    authorizationFlow.getFlow().getCredentialDataStore().delete(userId);
                    try {
                        Semaphore semaphore = TypeUtils.getFieldValue(authorizationFlow.getReceiver(), "waitUnlessSignaled");
                        semaphore.acquire();
                    } catch (InterruptedException ignore) {
                    }
                    credential = authorizationFlow.authorize(userId);
                    credential.intercept(request);
                } else {
                    throw e;
                }
            }
        }
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
