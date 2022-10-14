package eu.easyrpa.examples.azure.services.outlook_message_sending;

import eu.easyrpa.examples.azure.services.outlook_message_sending.tasks.SendOutlookMessage;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Simple Message Sending")
public class OutlookMessageSendingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), SendOutlookMessage.class).get();
    }

    public static void main(String[] args){ApModuleRunner.localLaunch(OutlookMessageSendingModule.class);}
}
