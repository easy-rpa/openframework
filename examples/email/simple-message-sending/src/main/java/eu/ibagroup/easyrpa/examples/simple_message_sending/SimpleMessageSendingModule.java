package eu.ibagroup.easyrpa.examples.simple_message_sending;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.simple_message_sending.task.SendEmailMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Simple Message Sending")
public class SimpleMessageSendingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), SendEmailMessage.class).get();
    }
}
