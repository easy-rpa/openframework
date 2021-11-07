package eu.ibagroup.easyrpa.examples.email.folders_listing;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(FoldersListingModule.class);
    }
}