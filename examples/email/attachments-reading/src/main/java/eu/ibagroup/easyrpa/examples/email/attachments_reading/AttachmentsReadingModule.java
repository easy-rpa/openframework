package eu.ibagroup.easyrpa.examples.email.attachments_reading;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.attachments_reading.tasks.ReadMessagesWithAttachments;
import eu.ibagroup.easyrpa.examples.email.attachments_reading.tasks.WaitMessagesWithKeywords;
import eu.ibagroup.easyrpa.examples.email.message_waiting.tasks.LookupMessagesWithAttachments;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Attachments Reading")
public class AttachmentsReadingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadMessagesWithAttachments.class).get();
    }
}
