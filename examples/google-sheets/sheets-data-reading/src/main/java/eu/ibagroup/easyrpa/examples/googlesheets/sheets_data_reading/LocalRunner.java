package eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(SheetsManipulatingModule.class);
    }
}