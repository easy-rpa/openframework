package eu.ibagroup.easyrpa.openframework.googleauth;

@FunctionalInterface
public interface AuthorizationPerformer {

    /**
     *
     * @param authorizationUrl authorization code request url.
     */
    void perform(String authorizationUrl);
}
