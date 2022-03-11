package eu.easyrpa.examples.excel.export_to_pdf;

import eu.easyrpa.examples.excel.export_to_pdf.tasks.ExportActiveSheetToPDF;
import eu.ibagroup.easyrpa.engine.annotation.ApModuleEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApModule;
import eu.ibagroup.easyrpa.engine.apflow.TaskOutput;
import eu.ibagroup.easyrpa.engine.boot.ApModuleRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApModuleEntry(name = "Export to PDF")
public class ExportToPDFModule extends ApModule {

    public TaskOutput run() throws Exception {
        return execute(getInput(), ExportActiveSheetToPDF.class).get();
    }

    public static void main(String[] args) {
        ApModuleRunner.localLaunch(ExportToPDFModule.class);
    }
}
