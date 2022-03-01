package eu.ibagroup.easyrpa.openframework.google.services;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * Performs authorization of specific users to work with specific Google API and instantiate corresponding
 * Google API Services for them.
 */
public class GoogleServicesProvider {

    private GoogleAuth authService;

    private RPAServicesAccessor rpaServices;

    public GoogleServicesProvider() {
        authService = new GoogleAuth();
    }

    /**
     * Construct Google Service Provider using configuration parameters.
     *
     * @param rpaServices the service to provide parameters.
     */
    @Inject
    public GoogleServicesProvider(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
        authService = new GoogleAuth(rpaServices);
    }

    /**
     * @return reference to GoogleAuth instance.
     */
    public GoogleAuth getAuth() {
        return authService;
    }

    /**
     * Set authorization performer.
     *
     * @param authorizationPerformer new authorization performer.
     * @return a self reference.
     */
    public GoogleServicesProvider onAuthorization(AuthorizationPerformer authorizationPerformer) {
        authService.setAuthorizationPerformer(authorizationPerformer);
        return this;
    }

    /**
     * Set user is and secret by given secret vault alias.
     *
     * @param vaultAlias the vault with user id and secret.
     * @return a self reference.
     */
    public GoogleServicesProvider secret(String vaultAlias) {
        if (rpaServices != null) {
            authService.setUserId(vaultAlias);
            authService.setSecret(rpaServices.getSecret(vaultAlias, String.class));
        }
        return this;
    }

    /**
     * Set user id and secret by given values.
     *
     * @param userId new user id.
     * @param secret new secret.
     * @return a self reference.
     */
    public GoogleServicesProvider secret(String userId, String secret) {
        authService.setUserId(userId);
        authService.setSecret(secret);
        return this;
    }

    /**
     * Get Google Service for given <code>serviceClass</code> and scopes.
     *
     * @param serviceClass class of the service.
     * @param scopes       list of scopes requested for the service.
     * @param <T>          type of the service.
     * @return the service of specified type.
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
     * Get builder wrapper of Google Service for given <code>serviceClass</code> and scopes.
     *
     * @param builderClass class of the service.
     * @param scopes       list of scopes requested for the service.
     * @param <T>          type of the service.
     * @return the builder wrapper for service of specified type.
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
