package eu.ibagroup.easyrpa.examples.send_simple_email;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SendSimpleEmailModule.class);
    }
}