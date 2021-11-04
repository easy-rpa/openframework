package eu.ibagroup.easyrpa.examples.email.messages_manipulating;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MessagesManipulatingModule.class);
    }
}