package eu.easyrpa.examples.email.message_sending_with_attachments;

import eu.easyrpa.examples.email.message_sending_with_attachments.tasks.SendMessageWithAttachment;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Message Sending with Attachments")
public class MessageSendingWithAttachmentsModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), SendMessageWithAttachment.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MessageSendingWithAttachmentsModule.class);
    }
}
