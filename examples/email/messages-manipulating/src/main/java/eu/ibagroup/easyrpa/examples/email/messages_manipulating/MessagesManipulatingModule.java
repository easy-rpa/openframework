package eu.ibagroup.easyrpa.examples.email.messages_manipulating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.messages_manipulating.tasks.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Messages Manipulating")
public class MessagesManipulatingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), GetUnreadMessage.class)
                .thenCompose(execute(MarkMessageAsRead.class))
                .thenCompose(execute(ForwardMessage.class))
                .thenCompose(execute(ReplyToMessage.class))
                .thenCompose(execute(DeleteMessage.class))
                .get();
    }
}
