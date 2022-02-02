package eu.ibagroup.easyrpa.openframework.googleauth.constants;

/**
 * The list of configuration parameter names which can be specified within RPA platform to provide necessary
 * for Google services authorization information.
 */
public class GAuthConfigParam {

    public static final String SECRET = "google.auth.secret";

    public static final String TOKEN_STORES_DIR = "google.auth.token.stores.dir";

    public static final String VERIFICATION_CODE_RECEIVER = "google.auth.verification.code.receiver";

    private GAuthConfigParam() {
    }
}