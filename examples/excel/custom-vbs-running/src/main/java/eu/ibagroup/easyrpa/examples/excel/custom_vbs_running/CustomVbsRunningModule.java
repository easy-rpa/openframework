package eu.ibagroup.easyrpa.examples.excel.custom_vbs_running;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.custom_vbs_running.tasks.RunCustomVBScript;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Custom VBS Running")
public class CustomVbsRunningModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), RunCustomVBScript.class).get();
    }
}
