package eu.easyrpa.openframework.google.services.constants;

/**
 * The list of configuration parameter names which can be specified within RPA platform to provide necessary
 * for Google services authorization information.
 */
public class GServicesConfigParam {

    /**
     * Name of configuration parameter with secret information necessary to perform authentication on Google server.
     */
    public static final String AUTH_SECRET = "google.services.auth.secret";

    /**
     * Name of configuration parameter with path to directory where StoredCredentials file should be created and
     * located.
     */
    public static final String AUTH_TOKEN_STORES_DIR = "google.services.auth.token.stores.dir";

    /**
     * Name of configuration parameter with {@code host:port_number} of authorization verification code receiver.
     */
    public static final String AUTH_VERIFICATION_CODE_RECEIVER = "google.services.auth.verification.code.receiver";

    private GServicesConfigParam() {
    }
}