package eu.easyrpa.examples.email.messages_searching;

import eu.easyrpa.examples.email.messages_searching.tasks.LookupMessagesWithAttachments;
import eu.easyrpa.examples.email.messages_searching.tasks.LookupMessagesWithKeywords;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Messages Searching")
public class MessagesSearchingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), LookupMessagesWithKeywords.class)
                .thenCompose(execute(LookupMessagesWithAttachments.class)).get();
    }
}
