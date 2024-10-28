package eu.easyrpa.examples.email.template_based_message_creating;

import eu.easyrpa.examples.email.template_based_message_creating.tasks.SendEmailMessage;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Template Based Message Creating")
public class TemplateBasedMessageCreatingModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), SendEmailMessage.class).get();
    }

}