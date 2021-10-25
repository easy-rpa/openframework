package eu.ibagroup.easyrpa.examples.email.attachments_reading;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(AttachmentsReadingModule.class);
    }
}