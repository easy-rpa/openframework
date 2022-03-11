package eu.easyrpa.examples.email.simple_message_sending;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.easyrpa.examples.email.simple_message_sending.tasks.SendEmailMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Simple Message Sending")
public class SimpleMessageSendingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), SendEmailMessage.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SimpleMessageSendingModule.class);
    }
}
