package eu.easyrpa.examples.email.attachments_reading;

import eu.easyrpa.examples.email.attachments_reading.tasks.ReadMessagesWithAttachments;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Attachments Reading")
public class AttachmentsReadingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ReadMessagesWithAttachments.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(AttachmentsReadingModule.class);
    }
}
