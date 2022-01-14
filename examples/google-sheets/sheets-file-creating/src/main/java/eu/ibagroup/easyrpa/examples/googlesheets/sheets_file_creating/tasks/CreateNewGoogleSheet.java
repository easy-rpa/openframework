package eu.ibagroup.easyrpa.examples.googlesheets.sheets_file_creating.tasks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_file_creating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.openframework.googlesheets.utils.FilePathUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Create new Google Sheet")
@Slf4j
public class CreateNewGoogleSheet extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Configuration(value = "sheet.name")
    private String sheetName;

    @Configuration(value = "sample.data.file")
    private String sampleDataFile;

    @Inject
    GoogleSheets service;

    @Override
    public void execute() throws IOException {
        log.info("Load sample data from '{}'.", sampleDataFile);
        List<Passenger> data = loadSampleData(sampleDataFile);

        log.info("Create new sheet with the name '{}' in the document with id '{}'.", sheetName, spreadsheetId);
        SpreadsheetDocument doc = service.getSpreadsheet(spreadsheetId);
        Sheet sheet = doc.createSheet(sheetName);

        log.info("Put data on the sheet '{}'.", sheetName);
        sheet.insertTable("C3", data);

        log.info("Spreadsheet document has been saved successfully");
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
