package eu.ibagroup.easyrpa.engine.service.impl;

import eu.ibagroup.easyrpa.engine.service.ConfigurationService;
import eu.ibagroup.easyrpa.engine.service.VaultService;
import eu.ibagroup.easyrpa.openframework.core.model.RPASecretCredentials;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesProvider;

import javax.inject.Inject;

public class EasyRPAServicesProvider implements RPAServicesProvider {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private VaultService vaultService;

    @Override
    public String getParam(String key) {
        return configurationService.get(key);
    }

    @Override
    public <T> T getSecret(String alias, Class<T> cls) {
        return vaultService.getSecret(alias, cls);
    }

    @Override
    public RPASecretCredentials getCredentials(String alias) {
        return vaultService.getSecret(alias, RPASecretCredentials.class);
    }
}
