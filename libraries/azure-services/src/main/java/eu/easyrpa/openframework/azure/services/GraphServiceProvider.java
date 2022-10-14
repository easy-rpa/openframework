package eu.easyrpa.openframework.azure.services;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import okhttp3.Request;
import eu.easyrpa.openframework.azure.services.constants.GraphServicesConfigParam;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Authentication and authorization helper for Azure API services.
 * <p>
 * Helps to perform configuration and retrieving of necessary secret information, authentication on Azure server and
 * authorization to using some Azure API service on behalf of specific Microsoft account.
 */
public class GraphServiceProvider {

    private static final String DEFAULT_TENANT_ID = "common";

    /**
     * Application unique identifier that is associated with an application that assists with client / server
     * OAuth 2.0 authentication.
     */
    private String azureClientId;

    /**
     * The Azure Tenant ID is a Global Unique Identifier (GUID) for your Microsoft 365 Tenant.
     * It’s also referred to as the Office 365 Tenant ID.
     * The ID is used to identify your tenant, and it’s not your organization name or domain name.
     */
    private String azureTenantId;

    /**
     * This parameter is a list of delegated permissions that the app is requesting.
     */
    private List<String> azurePermissions;

    /**
     * Enables authentication to Azure Active Directory using a device code that the user can enter
     * into https://microsoft.com/devicelogin
     */
    private DeviceCodeCredential deviceCodeCredential;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Default constructor for {@code AzureAuth}.
     */
    public GraphServiceProvider() {

    }

    /**
     * Constructs {@code AzureAuth} with provided {@link RPAServicesAccessor}.
     * <p>
     * This constructor is used in case of injecting of this AzureAuth using {@link Inject} annotation.
     *
     * @param rpaServices instance of {@link RPAServicesAccessor} that allows to use provided by RPA platform services
     *                    like configuration, secret vault etc.
     */
    @Inject
    public GraphServiceProvider(RPAServicesAccessor rpaServices) {
        this();
        this.rpaServices = rpaServices;
    }

    /**
     * @return reference to used Client ID.
     */
    public String getAzureClientId() {
        if (azureClientId == null) {
            azureClientId = getConfigParam(GraphServicesConfigParam.AZURE_CLIENT_ID);
        }
        return azureClientId;
    }

    /**
     * Sets app unique identifier, which is located in AzureActiveDirectory.
     *
     * @param azureClientId Application unique identifier that is associated with an application.
     */
    public void setAzureClientId(String azureClientId) {
        this.azureClientId = azureClientId;
    }

    /**
     * @return reference to used Tenant ID.
     */
    public String getAzureTenantId() {
        if (azureTenantId == null) {
            azureTenantId = getConfigParam(GraphServicesConfigParam.AZURE_TENANT_ID);
            if (azureTenantId == null) {
                azureTenantId = DEFAULT_TENANT_ID;
            }
        }
        return azureTenantId;
    }

    /**
     * Sets authTenantId.
     *
     * @param azureTenantId is a Global Unique Identifier (GUID) for your Microsoft 365 Tenant.
     */
    public void setAzureTenantId(String azureTenantId) {
        this.azureTenantId = azureTenantId;
    }

    /**
     * @return reference to used User scopes of Azure API permissions.
     */
    public List<String> getAzurePermissions() {
        if (azurePermissions == null) {
            azurePermissions = Arrays.asList(Objects.requireNonNull(
                    getConfigParam(GraphServicesConfigParam.AZURE_PERMISSIONS)).split(";"));
        }
        return azurePermissions;
    }

    /**
     * Sets graph api permission list.
     *
     * @param azurePermissions is a String that contains delegated permissions that the app is requesting.
     */
    public void setAzurePermissions(String azurePermissions) {
        this.azurePermissions = Arrays.asList(azurePermissions.split(";"));
    }

    /**
     * Sets explicitly the alias of secret vault entry with AzureTenantId necessary for authentication on the
     * Azure server.
     * <p>
     * For information regarding how to find your tenant ID see.
     * <a href="https://docs.microsoft.com/en-us/graph/tutorials/java?tabs=aad&tutorial-step=1">Azure app registration</a>
     *
     * @param vaultAlias the alias of secret vault entry azureTenantID to use.
     * @return this object to allow joining of methods calls into chain.
     */
    public GraphServiceProvider secret(String vaultAlias) {
        if (rpaServices != null) {
            this.setAzureClientId(vaultAlias);
            this.setAzureTenantId(rpaServices.getSecret(vaultAlias, String.class));
        }
        return this;
    }

    /**
     * Sets graph api permissions.
     * <p>
     * For information regarding how to set specific permissions you want, see
     * <a href="https://docs.microsoft.com/en-us/azure/active-directory/develop/quickstart-configure-app-access-web-apis">Quickstart: Configure a client application to access a web API</a>
     *
     * @param azurePermissions is a string of delegated permissions that the app is requesting.
     * @return this object to allow joining of methods calls into chain.
     */
    public GraphServiceProvider azurePermissions(String azurePermissions) {
        this.setAzurePermissions(azurePermissions);
        return this;
    }

    /**
     * Allows to override the way how this code informs the user that it wishes to act on his behalf and obtain
     * corresponding access token from Google.
     * <p>
     * By default, it prints message in console this code is running and locates to OAuth consent page where
     * user should authorize performing of necessary operations. If this code is running on robot's machine performing
     * of authorization by this way is not possible since user won't able to see the browser page.
     * <p>
     * Using this method is possible to overrides this behavior and specify, lets say, sending of notification email
     * with link to OAuth consent page to administrator, who is able to perform authorization on behalf of robot's
     * Google account. In this case robot will be able to access Google services on behalf of his account. Any time
     * when access token is invalid administrator will get such email and let robot continue his work. E.g.:
     * <pre>
     * <code>@Inject</code>
     * SomeAuthorizationRequiredEmail authorizationRequiredEmail;
     * ...
     *
     * graphServiceProvider.onAuthorization(challenge->{
     *    authorizationRequiredEmail.setConsentPage(challenge.getUrl()).send();
     * });
     *
     * ...
     * </pre>
     *
     * @param challengeConsumer lambda expression or instance of {@link Consumer<DeviceCodeInfo>} that defines
     *                               specific behavior of authorization step.
     * @return this object to allow joining of methods calls into chain.
     */
    public GraphServiceProvider onAuthorization(Consumer<DeviceCodeInfo> challengeConsumer) {
        this.deviceCodeCredential = new DeviceCodeCredentialBuilder()
                .clientId(getAzureClientId())
                .tenantId(getAzureTenantId())
                .challengeConsumer(challengeConsumer)
                .build();
        return this;
    }

    /**
     * Build and return a GraphServiceClient object to make requests against the service.
     *
     * <p>
     * This is a preferable way of working with this class. E.g.:
     * <pre>
     *   {@code @Inject}
     *     private GraphServiceProvider graphServiceProvider;
     *
     *     public void execute() {
     *          ...
     *         GraphServiceClient<Request> userClient = graphServiceProvider.getGraphServiceClient();
     *        ...
     *     }
     *  </pre>
     *
     * @return An instance of GraphServiceClient object to make requests against the service.
     */
    public GraphServiceClient<Request> getGraphServiceClient() {

        if (this.deviceCodeCredential == null) {
            this.deviceCodeCredential = new DeviceCodeCredentialBuilder()
                    .clientId(getAzureClientId())
                    .tenantId(getAzureTenantId())
                    .build();
        }

        final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(getAzurePermissions(), deviceCodeCredential);

        return GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
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

}
