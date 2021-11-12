package eu.ibagroup.easyrpa.examples.excel.export_to_pdf;

import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;

public class LocalRunner {

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(ExportToPDFModule.class);
    }
}