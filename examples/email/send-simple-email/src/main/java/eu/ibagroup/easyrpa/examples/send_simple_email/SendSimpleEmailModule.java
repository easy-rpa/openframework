package eu.ibagroup.easyrpa.examples.send_simple_email;

import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.examples.send_simple_email.task.SendSimpleEmail;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Send Simple Email")
public class SendSimpleEmailModule extends ApModule {

    // change ReadEmail.class to any of the two remaining task-classes
    public TaskOutput run() throws Exception {
        return execute(getInput(), SendSimpleEmail.class).get();
    }
}
