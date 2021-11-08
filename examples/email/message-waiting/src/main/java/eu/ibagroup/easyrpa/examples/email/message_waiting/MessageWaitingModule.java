package eu.ibagroup.easyrpa.examples.email.message_waiting;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.message_waiting.tasks.WaitMessagesWithKeywords;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Message Waiting")
public class MessageWaitingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), WaitMessagesWithKeywords.class).get();
    }
}
