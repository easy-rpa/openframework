package eu.ibagroup.easyrpa.examples.googlesheets.sheets_copying;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SheetsCopyingModule.class);
    }
}