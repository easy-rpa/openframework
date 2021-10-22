package eu.ibagroup.easyrpa.examples.email.template_based_message_creating;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(TemplateBasedMessageCreatingModule.class);
    }
}