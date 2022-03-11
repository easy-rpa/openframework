package eu.easyrpa.examples.excel.export_to_pdf.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import eu.easyrpa.openframework.excel.exceptions.VBScriptExecutionException;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Export Active Sheet to PDF")
@Slf4j
public class ExportActiveSheetToPDF extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.pdf.file")
    private String outputPdfFile;

    @Override
    public void execute() {
        log.info("Export active sheet of spreadsheet document located at '{}'", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        try {
            log.info("Export sheet '{}' to PDF file located at '{}'", activeSheet.getName(), outputPdfFile);
            activeSheet.exportToPDF(outputPdfFile);

            log.info("Sheet has been exported successfully.");

        } catch (VBScriptExecutionException e) {
            throw new RuntimeException("Exporting of sheet '{}' to PDF has failed.", e);
        }
    }
}
