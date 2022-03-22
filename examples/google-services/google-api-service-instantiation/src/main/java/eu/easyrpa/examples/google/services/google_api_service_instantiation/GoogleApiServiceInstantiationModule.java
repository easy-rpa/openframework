package eu.easyrpa.examples.google.services.google_api_service_instantiation;

import eu.easyrpa.examples.google.services.google_api_service_instantiation.task.CreateGoogleCalendarService;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Google API Service Instantiation")
public class GoogleApiServiceInstantiationModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), CreateGoogleCalendarService.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(GoogleApiServiceInstantiationModule.class);
    }
}