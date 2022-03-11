package eu.easyrpa.examples.excel.macros_running;

import eu.easyrpa.examples.excel.macros_running.tasks.RunMacro;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Macros Running")
public class MacrosRunningModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), RunMacro.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MacrosRunningModule.class);
    }
}
