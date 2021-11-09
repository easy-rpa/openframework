package file_dir_info;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {
    public static void main(String[] args) {
        ApModuleRunner.localLaunch(FileDirInfoModule.class);
    }
}
