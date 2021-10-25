package eu.ibagroup.easyrpa.examples.email.messages_searching;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.messages_searching.tasks.LookupMessagesWithAttachments;
import eu.ibagroup.easyrpa.examples.email.messages_searching.tasks.LookupMessagesWithKeywords;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Messages Searching")
public class MessagesSearchingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), LookupMessagesWithKeywords.class)
                .thenCompose(execute(LookupMessagesWithAttachments.class)).get();
    }
}
