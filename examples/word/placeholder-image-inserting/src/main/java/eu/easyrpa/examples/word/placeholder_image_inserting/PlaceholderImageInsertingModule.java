package eu.easyrpa.examples.word.placeholder_image_inserting;

import eu.easyrpa.examples.word.placeholder_image_inserting.tasks.InsertImage;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Word File Creating")
public class PlaceholderImageInsertingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), InsertImage.class).get();

    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(PlaceholderImageInsertingModule.class);
    }
}