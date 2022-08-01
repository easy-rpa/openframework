package eu.easyrpa.examples.word.working_with_variables;

import eu.easyrpa.examples.word.working_with_variables.tasks.ReplaceVariables;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Working With Variables")
public class WorkingWithVariablesModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReplaceVariables.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithVariablesModule.class);
    }
}

