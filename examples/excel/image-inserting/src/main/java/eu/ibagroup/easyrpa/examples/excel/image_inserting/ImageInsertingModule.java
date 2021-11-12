package eu.ibagroup.easyrpa.examples.excel.image_inserting;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.excel.image_inserting.tasks.PutImageOnSheet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Image Inserting")
public class ImageInsertingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), PutImageOnSheet.class).get();
    }
}
