package eu.easyrpa.openframework.google.services;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * Performs authorization of specific users to work with specific Google API and instantiate corresponding
 * Google API Services for them.
 */
public class GoogleServicesProvider {

    /**
     * Instance of authentication and authorization helper for Google API services
     */
    private GoogleAuth authService;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Default constructor for GoogleServicesProvider.
     * <p>
     * This constructor should be used in case of manual providing of secret information necessary for authorization
     * and instantiation Google API service. E.g.:
     * <pre>
     * String secretJson = new String(Files.readAllBytes(Paths.get("secret.json")), StandardCharsets.UTF_8);
     *
     * GoogleServicesProvider googleServicesProvider = new GoogleServicesProvider().secret("user1", secretJson);
     * Sheets sheetsService = googleServicesProvider.getService(Sheets.class, SheetsScopes.SPREADSHEETS);
     *  ...
     * </pre>
     */
    public GoogleServicesProvider() {
        authService = new GoogleAuth();
    }

    /**
     * Constructs GoogleServicesProvider with provided {@link RPAServicesAccessor}.
     * <p>
     * This constructor is used in case of injecting of this GoogleServicesProvider using {@link Inject} annotation.
     * This is preferable way of working with this class. E.g.:
     * <pre>
     * {@code @Inject}
     *  private GoogleServicesProvider googleServicesProvider;
     *
     *  public void execute() {
     *      ...
     *     Sheets sheetsService = googleServicesProvider.getService(Sheets.class, SheetsScopes.SPREADSHEETS);
     *      ...
     *  }
     * </pre>
     *
     * @param rpaServices instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     */
    @Inject
    public GoogleServicesProvider(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
        authService = new GoogleAuth(rpaServices);
    }

    /**
     * Gets authentication and authorization helper for Google API services.
     *
     * @return reference to related instance of {@link GoogleAuth}.
     */
    public GoogleAuth getAuth() {
        return authService;
    }

    /**
     * Allows to override the way how this code informs the user that it wishes to act on his behalf and obtain
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
     * googleServicesProvider.onAuthorization(url->{
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
    public GoogleServicesProvider onAuthorization(AuthorizationPerformer authorizationPerformer) {
        authService.setAuthorizationPerformer(authorizationPerformer);
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
    public GoogleServicesProvider secret(String vaultAlias) {
        if (rpaServices != null) {
            authService.setUserId(vaultAlias);
            authService.setSecret(rpaServices.getSecret(vaultAlias, String.class));
        }
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
     * @param userId user unique identifier that will be associated with secret information. This is used as key
     *               to store access token in StoredCredentials file.
     * @param secret JSON string with secret information to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public GoogleServicesProvider secret(String userId, String secret) {
        authService.setUserId(userId);
        authService.setSecret(secret);
        return this;
    }

    /**
     * Authorize and gets instance of specific Google service within given scopes.
     *
     * @param serviceClass class of the service.
     * @param scopes       list of scopes requested for the service.
     * @param <T>          type of the service.
     * @return the instance of Google service of specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractGoogleJsonClient> T getService(Class<T> serviceClass, String... scopes) {
        Class<? extends AbstractGoogleJsonClient.Builder> builderClass = null;
        for (Class<?> innerClass : serviceClass.getDeclaredClasses()) {
            if (AbstractGoogleJsonClient.Builder.class.isAssignableFrom(innerClass)) {
                builderClass = (Class<? extends AbstractGoogleJsonClient.Builder>) innerClass;
                break;
            }
        }
        if (builderClass == null) {
            throw new IllegalArgumentException(String.format("The service '%s' does not have necessary builder " +
                    "that extends from 'AbstractGoogleJsonClient.Builder'. " +
                    "It cannot be instantiated using this method.", serviceClass.getName()));
        }

        return (T) getServiceBuilder(builderClass, scopes).build();
    }

    /**
     * Authorize and gets instance of Google service builder that builds specific Google service within given scopes.
     *
     * @param builderClass class of the service.
     * @param scopes       list of scopes requested for the service.
     * @param <T>          type of the service.
     * @return the instance of builder for specified type of Google service.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractGoogleJsonClient.Builder> T getServiceBuilder(Class<T> builderClass, String... scopes) {
        try {
            Constructor<?> constructor = builderClass
                    .getDeclaredConstructor(HttpTransport.class, JsonFactory.class, HttpRequestInitializer.class);
            return (T) constructor.newInstance(authService.getHttpTransport(),
                    authService.getJsonFactory(), authService.authorize(Arrays.asList(scopes)));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("The service builder '%s' does not have necessary " +
                    "constructor. It cannot be instantiated using this method.", builderClass.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("The service builder '%s' " +
                    "instantiation has failed.", builderClass.getName()), e);
        }
    }
}
