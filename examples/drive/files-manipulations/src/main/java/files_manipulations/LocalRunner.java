package files_manipulations;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;


public class LocalRunner {
    public LocalRunner() {
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(FileManipulationsModule.class);
    }
}
