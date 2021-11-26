package eu.ibagroup.easyrpa.examples.excel.working_with_large_files;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(WorkingWithLargeFilesModule.class);
    }
}