package eu.ibagroup.easyrpa.openframework.core.sevices;

import eu.ibagroup.easyrpa.openframework.core.model.RPASecretCredentials;

/**
 * Allows to use within EasyRPA Open Framework provided by RPA platform services like configuration, secret vault, etc.
 * <p>
 * Each RPA platform where EasyRPA Open Framework are going to be used should provide own implementation
 * of this interface.
 */
public interface RPAServicesAccessor {

    String getConfigParam(String key);

    <T> T getSecret(String alias, Class<T> cls);

    RPASecretCredentials getCredentials(String alias);
}
