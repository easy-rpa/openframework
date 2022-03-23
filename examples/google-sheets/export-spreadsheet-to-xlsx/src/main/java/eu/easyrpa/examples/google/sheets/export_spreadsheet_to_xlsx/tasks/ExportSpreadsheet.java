package eu.easyrpa.examples.google.sheets.export_spreadsheet_to_xlsx.tasks;

import eu.easyrpa.openframework.core.utils.FilePathUtils;
import eu.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@ApTaskEntry(name = "Export Spreadsheet")
@Slf4j
public class ExportSpreadsheet extends ApTask {

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() throws IOException {
        log.info("Read spreadsheet with ID: {}", sourceSpreadsheetFileId);
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);

        log.info("Download spreadsheet content as XLSX file");
        InputStream excelFileContent = doc.exportAsXLSX();

        String excelFilePath = FilePathUtils.normalizeFilePath(outputFilesDir + File.separator + FilenameUtils.getName(doc.getName()) + ".xlsx");
        log.info("Save XLSX file to '{}'", excelFilePath);
        Files.copy(excelFileContent, Paths.get(excelFilePath), StandardCopyOption.REPLACE_EXISTING);

        log.info("XLSX file saved successfully");
    }
}
