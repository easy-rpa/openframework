package eu.easyrpa.examples.azure.services.outlook_message_reading;

import eu.easyrpa.examples.azure.services.outlook_message_reading.tasks.ReadMessagesTask;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

@ApModuleEntry(name="bruh")
public class OutlookMessageReadingModule extends ApModule {
    @Override
    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadMessagesTask.class).get();
    }

    public static  void main(String[] args){
        ApModuleRunner.localLaunch(OutlookMessageReadingModule.class);
    }
}
