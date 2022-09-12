package eu.easyrpa.openframework.calendar.test;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.engine.boot.configuration.DevelopmentConfigurationModule;
import eu.ibagroup.easyrpa.engine.boot.configuration.StandaloneConfigurationModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Test module for Holiday extension")
public class TestModule extends ApModule {
    @Override
    public TaskOutput run() throws Exception {
        return execute(getInput(),TestTask.class).get();
    }

    public static void main(String[] args){
        ApModuleRunner.localLaunch(TestModule.class, new DevelopmentConfigurationModule());
    }
}
