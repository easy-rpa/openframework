package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_creating.tasks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_creating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.core.utils.FilePathUtils;
import eu.ibagroup.easyrpa.openframework.google.drive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileType;
import eu.ibagroup.easyrpa.openframework.google.sheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.google.sheets.Sheet;
import eu.ibagroup.easyrpa.openframework.google.sheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApTaskEntry(name = "Create New Spreadsheet Document")
@Slf4j
public class CreateNewSpreadsheetDocument extends ApTask {

    private static final String SPREADSHEET_NAME = "EasyRPA Example. Passengers Book";
    private static final String SHEET_NAME = "Passengers";

    @Configuration(value = "sample.data.file")
    private String sampleDataFile;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {

        log.info("Load sample data from '{}'.", sampleDataFile);
        List<Passenger> data = loadSampleData(sampleDataFile);

        log.info("Create new spreadsheet file.");
        Optional<GFileInfo> spreadsheetFile = googleDrive.create(SPREADSHEET_NAME, GFileType.SPREADSHEET);

        if (spreadsheetFile.isPresent()) {
            log.info("Spreadsheet file is created successfully. Get related document.");
            SpreadsheetDocument doc = googleSheets.getSpreadsheet(spreadsheetFile.get().getId());
            Sheet sheet = doc.getActiveSheet();
            sheet.rename(SHEET_NAME);

            log.info("Put data on the sheet '{}'.", sheet.getName());
            sheet.insertTable("C3", data);

            log.info("Creation of spreadsheet document completed successfully.");
        }
    }

    private List<Passenger> loadSampleData(String jsonFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            TypeFactory typeFactory = TypeFactory.defaultInstance();
            JavaType resultType = typeFactory.constructCollectionType(ArrayList.class, Passenger.class);
            return objectMapper.readValue(FilePathUtils.getFile(jsonFilePath), resultType);
        } catch (IOException e) {
            throw new RuntimeException("Loading of sample data has failed.", e);
        }
    }
}

