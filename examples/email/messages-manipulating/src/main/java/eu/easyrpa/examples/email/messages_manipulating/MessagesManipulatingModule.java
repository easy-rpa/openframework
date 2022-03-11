package eu.easyrpa.examples.email.messages_manipulating;

import eu.easyrpa.examples.email.messages_manipulating.tasks.*;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Messages Manipulating")
public class MessagesManipulatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        TaskOutput taskOutput = execute(getInput(), GetUnreadMessage.class).get();

        if (taskOutput.get("messageId") == null) {
            return taskOutput;
        } else {
            return execute(taskOutput, MarkMessageAsRead.class)
                    .thenCompose(execute(ForwardMessage.class))
                    .thenCompose(execute(ReplyToMessage.class))
                    .thenCompose(execute(DeleteMessage.class))
                    .get();
        }
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MessagesManipulatingModule.class);
    }
}
