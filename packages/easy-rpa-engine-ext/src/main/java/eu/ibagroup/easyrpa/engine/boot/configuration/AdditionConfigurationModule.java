package eu.ibagroup.easyrpa.engine.boot.configuration;

import eu.ibagroup.easyrpa.engine.annotation.RunConfiguration;
import eu.ibagroup.easyrpa.engine.boot.ConfigurationModule;
import eu.ibagroup.easyrpa.engine.service.impl.EasyRPAServicesProvider;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesProvider;
import eu.ibagroup.easyrpa.utils.InstantiationUtils;
import org.codejargon.feather.Feather;
import org.codejargon.feather.Provides;

import javax.inject.Singleton;

@RunConfiguration
public class AdditionConfigurationModule implements ConfigurationModule {

    @Provides
    @Singleton
    public RPAServicesProvider createRPAServicesProvider(Feather injector) {
        return (RPAServicesProvider) InstantiationUtils.getInstance(injector, EasyRPAServicesProvider.class, true, true);
    }
}
