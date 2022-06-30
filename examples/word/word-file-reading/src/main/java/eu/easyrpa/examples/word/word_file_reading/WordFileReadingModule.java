package eu.easyrpa.examples.word.word_file_reading;

import eu.easyrpa.examples.word.word_file_reading.tasks.ReadFileContent;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Word File Reading")
public class WordFileReadingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadFileContent.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WordFileReadingModule.class);
    }
}
