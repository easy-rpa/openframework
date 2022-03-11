package eu.easyrpa.examples.excel.image_inserting.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Put Image on Sheet")
@Slf4j
public class PutImageOnSheet extends ApTask {

    private static final String PATH_TO_IMAGE = "image.png";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.spreadsheet.file")
    private String outputSpreadsheetFile;

    @Override
    public void execute() {
        log.info("Put image on sheet of spreadsheet document located at '{}'", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Put image '{}' on  sheet '{}'", PATH_TO_IMAGE, activeSheet.getName());
        activeSheet.addImage(PATH_TO_IMAGE, "C6");
        log.info("Image has been put successfully.");

        log.info("Save changes to '{}'.", outputSpreadsheetFile);
        doc.saveAs(outputSpreadsheetFile);

        log.info("Spreadsheet document is saved successfully.");
    }
}
