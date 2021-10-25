package eu.ibagroup.easyrpa.examples.email.message_waiting;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MessageWaitingModule.class);
    }
}