package eu.ibagroup.easyrpa.openframework.google.services;

/**
 * Allows to override the way how the code informs the user that it wishes to act on his behalf and obtain
 * corresponding access token from Google.
 * <p>
 * By default it opens a browser on machine where this code is running and locates to OAuth consent page where
 * user should authorize performing of necessary operations. If this code is running on robot's machine performing
 * of authorization by this way is not possible since user won't able to see the browser page.
 * <p>
 * Implementation of {@link #perform(String)} overrides this behavior and specify, lets say, sending of notification
 * email with link to OAuth consent page to administrator, who is able to perform authorization on behalf of robot's
 * Google account. In this case robot will be able to access Google services on behalf of his account. Any time
 * when access token is invalid administrator will get such email and let robot to continue his work.
 */
@FunctionalInterface
public interface AuthorizationPerformer {

    /**
     * Specifies behavior of how the code informs the user that it wishes to act on his behalf and obtain corresponding
     * access token from Google.
     *
     * @param authorizationUrl authorization code request url.
     */
    void perform(String authorizationUrl);
}
