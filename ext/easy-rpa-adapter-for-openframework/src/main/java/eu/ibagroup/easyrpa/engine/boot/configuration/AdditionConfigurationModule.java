package eu.ibagroup.easyrpa.engine.boot.configuration;

import eu.ibagroup.easyrpa.engine.annotation.RunConfiguration;
import eu.ibagroup.easyrpa.engine.boot.ConfigurationModule;
import eu.ibagroup.easyrpa.engine.service.impl.EasyRPAServicesAccessor;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.utils.InstantiationUtils;
import org.codejargon.feather.Feather;
import org.codejargon.feather.Provides;

import javax.inject.Singleton;

@RunConfiguration
public class AdditionConfigurationModule implements ConfigurationModule {

    @Provides
    @Singleton
    public RPAServicesAccessor createRPAServicesProvider(Feather injector) {
        return InstantiationUtils.getInstance(injector, EasyRPAServicesAccessor.class, true, true);
    }
}
