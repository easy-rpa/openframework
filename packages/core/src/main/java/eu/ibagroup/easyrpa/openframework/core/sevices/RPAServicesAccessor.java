package eu.ibagroup.easyrpa.openframework.core.sevices;

import eu.ibagroup.easyrpa.openframework.core.model.RPASecretCredentials;

public interface RPAServicesAccessor {

    String getConfigParam(String key);

    <T> T getSecret(String alias, Class<T> cls);

    RPASecretCredentials getCredentials(String alias);
}
