package services;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import okhttp3.Request;
import services.constants.GraphServicesConfigParam;

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
public class AzureAuth {

    private static final String DEFAULT_TENANT_ID= "common";

    /**
     * An instance of GraphServiceClient object to make requests against the service.
     * You can use a single client instance for the lifetime of the application.
     */
    private GraphServiceClient<Request> userClient;

    /**
     * Application unique identifier that is associated with an application that assists with client / server
     * OAuth 2.0 authentication
     */
    private String clientId;

    /**
     * The Azure Tenant ID is a Global Unique Identifier (GUID) for your Microsoft 365 Tenant.
     * It’s also referred to as the Office 365 Tenant ID.
     * The ID is used to identify your tenant and it’s not your organization name or domain name.
     */
    private String authTenantId;

    /**
     * This parameter is a space-separated list of delegated permissions that the app is requesting.
     */
    private List<String> graphUserScopes;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;


    /**
     * Default constructor for {@code AzureAuth}.
     */
    public AzureAuth(){

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
    public AzureAuth(RPAServicesAccessor rpaServices) {
        this();
        this.rpaServices = rpaServices;
    }

     /**
     * @return reference to used Client ID.
     */
    public String getClientId() {
        if(clientId == null){
            clientId = getConfigParam(GraphServicesConfigParam.AUTH_CLIENT_ID);
        }
        return clientId;
    }

    /**
     * @return reference to used Tenant ID.
     */
    public String getAuthTenantId(){
        if (authTenantId == null) {
            authTenantId = getConfigParam(GraphServicesConfigParam.AUTH_SECRET);
            if (authTenantId == null) {
                authTenantId = DEFAULT_TENANT_ID;
            }
        }
        return authTenantId;
    }

    /**
     * @return reference to used User scopes of Azure API permissions.
     */
    public List<String> getGraphUserScopes() throws Exception {
        if (graphUserScopes == null) {
            graphUserScopes = Arrays.asList(Objects.requireNonNull(
                    getConfigParam(GraphServicesConfigParam.API_PERMISSION_LIST)).split(","));
        }
        return graphUserScopes;
    }

    private DeviceCodeCredential setDeviceCodeCredential(Consumer<DeviceCodeInfo> challenge){
        return new DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(authTenantId)
                .challengeConsumer(challenge)
                .build();
    }

    public GraphServiceClient<Request> initializeGraphForUserAuth(Consumer<DeviceCodeInfo> challenge) throws Exception {

        clientId = getConfigParam("app.clientId");
        authTenantId = getConfigParam("app.authTenant");
        graphUserScopes = Arrays.asList(getConfigParam("app.graphUserScopes").split(","));

        final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(graphUserScopes,
                        setDeviceCodeCredential(challenge));

        userClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();

        return userClient;
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

    /**
     * @return the user id that is associated with secret information.
     */
    public  User getUser() throws Exception {
        // Ensure client isn't null
        if (userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return userClient.me()
                .buildRequest()
                .select("displayName,mail,userPrincipalName")
                .get();
    }

}
