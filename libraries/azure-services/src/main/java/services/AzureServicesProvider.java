package services;

import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;

public class AzureServicesProvider {
    private AzureAuth graphService;
    private RPAServicesAccessor rpaServices;

    public AzureServicesProvider(){

    }

    public AzureServicesProvider(RPAServicesAccessor rpaServices){
        this.rpaServices = rpaServices;
        graphService = new AzureAuth(rpaServices);
    }
}
