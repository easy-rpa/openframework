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
import services.exception.GraphAuthException;

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
     * The ID is used to identify your tenant, and it’s not your organization name or domain name.
     */
    private String authTenantId;

    /**
     * This parameter is a space-separated list of delegated permissions that the app is requesting.
     */
    private List<String> permissionList;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * A TokenCredential implementation which authenticates a user using the device code flow, and provides access
     * tokens for that user account.
     */
    private DeviceCodeCredential deviceCodeCredential;

    /**
     * Instance of User, which represents an Azure Active Directory (Azure AD) user account
     */
    private User user;


    /**
     * Default constructor for {@code AzureAuth}.
     */
    public GraphServiceProvider() {

    }

    /**
     * Constructor with parameters for {@code AzureAuth}.
     *
     * @param clientId application unique identifier.
     * @param authTenantId a Global Unique Identifier (GUID) for your Microsoft 365 Tenant.
     * @param permissionsList a space-separated list of delegated permissions that the app is requesting.
     */
    public GraphServiceProvider(String clientId, String authTenantId, String permissionsList){
        this.clientId = clientId;
        this.authTenantId = authTenantId;
        this.permissionList = Arrays.asList(permissionsList.split(","));
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
    public String getClientId() {
        if (clientId == null) {
            clientId = getConfigParam(GraphServicesConfigParam.AUTH_CLIENT_ID);
        }
        return clientId;
    }

    /**
     * Sets app unique identifier, which is located in AzureActiveDirectory
     *
     * @param clientId Application unique identifier that is associated with an application
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return reference to used Tenant ID.
     */
    public String getAuthTenantId() {
        if (authTenantId == null) {
            authTenantId = getConfigParam(GraphServicesConfigParam.AUTH_TENANT_ID);
            if (authTenantId == null) {
                authTenantId = DEFAULT_TENANT_ID;
            }
        }
        return authTenantId;
    }

    /**
     * Sets authTenantId
     *
     * @param authTenantId is a Global Unique Identifier (GUID) for your Microsoft 365 Tenant
     */
    public void setAuthTenantId(String authTenantId) {
        this.authTenantId = authTenantId;
    }

    /**
     * @return reference to used User scopes of Azure API permissions.
     */
    public List<String> getPermissionList() {
        if (permissionList == null) {
            permissionList = Arrays.asList(Objects.requireNonNull(
                    getConfigParam(GraphServicesConfigParam.API_PERMISSION_LIST)).split(","));
        }
        return permissionList;
    }

    /**
     * Sets graph api permission list
     *
     * @param permissionList is a space-separated list of delegated permissions that the app is requesting
     */
    public void setPermissionList(String permissionList) {
        this.permissionList = Arrays.asList(permissionList.split(","));
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
     *         GraphServiceClient<Request> userClient = graphServiceProvider.getGraphServiceClient(
     *                   challenge -> System.out.println(challenge.getMessage()));
     *        ...
     *     }
     *  </pre>
     *
     *
     * @return An instance of GraphServiceClient object to make requests against the service.
     */
    public GraphServiceClient<Request> getGraphServiceClient() {

        final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(getPermissionList(),
                        deviceCodeCredential(challenge -> System.out.println(challenge.getMessage())));

        this.userClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();

        return userClient;
    }

    /**
     * @return the user id that is associated with secret information.
     */
    public User getUser() {
        try {
            this.user = userClient.me()
                    .buildRequest()
                    .get();

        } catch (Exception e) {
            throw new GraphAuthException("Graph has not been initialized for user auth", e);
        }
        return user;
    }

    /**
     * Builds an instance of DeviceCodeCredential
     *
     * @param challenge prints a URL and special device code to sigh in
     * @return an instance of DeviceCodeCredential
     */
    private DeviceCodeCredential deviceCodeCredential(Consumer<DeviceCodeInfo> challenge) {
        this.deviceCodeCredential = new DeviceCodeCredentialBuilder()
                .clientId(getClientId())
                .tenantId(getAuthTenantId())
                .challengeConsumer(challenge)
                .build();
        return deviceCodeCredential;
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
