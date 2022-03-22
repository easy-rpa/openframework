package eu.easyrpa.examples.google.services.passing_authorization_only_by_robot;

import eu.easyrpa.examples.google.services.passing_authorization_only_by_robot.task.ListAllFiles;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Passing Authorization by Robot")
public class PassingAuthorizationByRobotModule extends ApModule {

    public TaskOutput run() throws Exception {
        return this.execute(this.getInput(), ListAllFiles.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(PassingAuthorizationByRobotModule.class);
    }
}