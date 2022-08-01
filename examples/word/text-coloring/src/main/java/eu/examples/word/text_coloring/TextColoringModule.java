package eu.examples.word.text_coloring;

import eu.examples.word.text_coloring.tasks.CreateTextColor;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Word File Creating")
public class TextColoringModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), CreateTextColor.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(TextColoringModule.class);
    }
}