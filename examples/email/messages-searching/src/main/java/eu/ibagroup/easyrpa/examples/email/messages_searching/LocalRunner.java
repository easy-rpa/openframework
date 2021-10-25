package eu.ibagroup.easyrpa.examples.email.messages_searching;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(MessagesSearchingModule.class);
    }
}