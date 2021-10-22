package eu.ibagroup.easyrpa.examples.email.simple_message_sending;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SimpleMessageSendingModule.class);
    }
}