package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.api.services.sheets.v4.model.ValueRange;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "List Existing Sheets")
@Slf4j
public class CreateNewGoogleSheetsDocument extends ApTask {

    @Configuration(value = "sample.data.file")
    private String sampleDataFile;

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    GoogleSheets service;

    @Override
    public void execute() throws IOException {
        String newSheetName = "Passengers";

        log.info("Load sample data from '{}'.", sampleDataFile);
        List<Passenger> data = loadSampleData(sampleDataFile);

        List<List<Object>> tableData = new ArrayList<>();

        List<Object> headerRow = new ArrayList<>();
        headerRow.add("Passenger Id");
        headerRow.add("Name");
        headerRow.add("Sex");
        headerRow.add("Age");
        headerRow.add("Survived");
        headerRow.add("Class");
        headerRow.add("Siblings on board");
        headerRow.add("Parch");
        headerRow.add("Ticket");
        headerRow.add("Fare");
        headerRow.add("Cabin");
        headerRow.add("Embarked");

        tableData.add(headerRow);

        for(Passenger passenger : data){
            tableData.add(passenger.toObjectList());
        }

        SpreadsheetDocument spreadsheetDocument = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheetDocument.getActiveSheet();

        String docName = spreadsheetDocument.getName();

        log.info("docName = "+ docName);

        ValueRange res = service.getValues(spreadsheetId, "A1:D4");

        service.updateValues(spreadsheetId, "A15", "RAW", tableData);
   //     GSheetColor color = new GSheetColor("#FF44FF");
//        GSheetColor color = new GSheetColor(new java.awt.Color(128,128,128,255));
//        service.setBackground(spreadsheetId, "B17:D20", GSheetColors.DARK_GRAY.get());
//        service.setTextColor(spreadsheetId, "C25:E30", GSheetColors.RED.get());

    }

    private List<Passenger> loadSampleData(String jsonFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            TypeFactory typeFactory = TypeFactory.defaultInstance();
            JavaType resultType = typeFactory.constructCollectionType(ArrayList.class, Passenger.class);
            return objectMapper.readValue(getFile(jsonFilePath), resultType);
        } catch (IOException e) {
            throw new RuntimeException("Loading of sample data has failed.", e);
        }
    }

    private File getFile(String path) {
        try {
            return new File(this.getClass().getResource(path.startsWith("/") ? path : "/" + path).toURI());
        } catch (Exception e) {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException(String.format("File '%s' is not exist.", path));
            }
            return file;
        }
    }
}
