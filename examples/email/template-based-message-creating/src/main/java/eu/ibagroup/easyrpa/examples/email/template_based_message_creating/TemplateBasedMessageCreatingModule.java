package eu.ibagroup.easyrpa.examples.email.template_based_message_creating;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.email.template_based_message_creating.tasks.SendEmailMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Template Based Message Creating")
public class TemplateBasedMessageCreatingModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), SendEmailMessage.class).get();
    }
}
