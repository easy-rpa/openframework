package eu.ibagroup.easyrpa.examples.excel.custom_vbs_running;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(CustomVbsRunningModule.class);
    }
}