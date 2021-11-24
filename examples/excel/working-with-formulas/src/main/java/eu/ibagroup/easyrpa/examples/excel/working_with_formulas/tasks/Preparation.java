package eu.ibagroup.easyrpa.examples.excel.working_with_formulas.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Preparation")
@Slf4j
public class Preparation extends ApTask {

    @Configuration(value = "shared.spreadsheet.file")
    private String sharedSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Output
    private String sharedSpreadsheetFilePath;

    @Override
    public void execute() {
        log.info("Prepare shared spreadsheet file '{}' to be linked to the source one.", sharedSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sharedSpreadsheetFile);

        sharedSpreadsheetFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + FilenameUtils.getName(sharedSpreadsheetFile));
        log.info("Save linked spreadsheet file to '{}'.", sharedSpreadsheetFilePath);
        doc.saveAs(sharedSpreadsheetFilePath);

        log.info("Spreadsheet file is saved successfully.");
    }
}
