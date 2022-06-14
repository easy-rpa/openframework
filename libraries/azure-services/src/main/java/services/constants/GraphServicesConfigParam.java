package services.constants;

import java.util.List;

/**
 * The list of configuration parameter names which can be specified within RPA platform to provide necessary
 * for Azure services authorization information.
 */
public class GraphServicesConfigParam {
    /**
     * Name of configuration parameter with secret information necessary to perform authentication on Azure server.
     */
    public static final String AUTH_SECRET = "azure.services.auth.secret";

    /**
     * Name of configuration parameter with clientID of your Azure app registration.
     */
    public static final String AUTH_CLIENT_ID = "azure.services.auth.clientID";

    /**
     * Name of configuration parameter with list of necessary API permissions for your app.
     */
    public static final String API_PERMISSION_LIST = "azure.services.graphUserScopes";

    private GraphServicesConfigParam() {}
}
