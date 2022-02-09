package eu.ibagroup.easyrpa.openframework.google.services;

@FunctionalInterface
public interface AuthorizationPerformer {

    /**
     *
     * @param authorizationUrl authorization code request url.
     */
    void perform(String authorizationUrl);
}
