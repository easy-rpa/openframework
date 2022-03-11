package eu.easyrpa.examples.email.message_waiting;

import eu.easyrpa.examples.email.message_waiting.tasks.WaitMessagesWithKeywords;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Message Waiting")
public class MessageWaitingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), WaitMessagesWithKeywords.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MessageWaitingModule.class);
    }
}
