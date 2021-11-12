package eu.ibagroup.easyrpa.examples.excel.image_inserting.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Put Image on Sheet")
@Slf4j
public class PutImageOnSheet extends ApTask {

    private static final String PATH_TO_IMAGE = "image.png";

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {

        String fromCellRef = "A2";
        String toCellRef = "D5";

        log.info("Put image on sheet of spreadsheet document located at '{}'", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Put image '{}' on  sheet '{}' from  cell '{}' to cell '{}'", PATH_TO_IMAGE, activeSheet.getName(), fromCellRef, toCellRef);
        activeSheet.addImage(PATH_TO_IMAGE, fromCellRef, toCellRef);

        log.info("Save changes for the document.");
        doc.save();

        log.info("Image has been put successfully.");
    }
}
