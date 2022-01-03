package eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading.tasks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.googlesheets.sheets_data_reading.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.googlesheets.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApTaskEntry(name = "Read Sheet Data")
@Slf4j
public class ReadSheetData extends ApTask {

    @Configuration(value = "sample.data.file")
    private String sampleDataFile;

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Configuration(value = "sheet.name")
    private String sheetName;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {

        log.info("get by default sheet to '{}' for spreadsheet with id: {}", sheetName, spreadsheetId);
        SpreadsheetDocument spreadsheetDocument = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheetDocument.selectSheet(sheetName);

        log.info("Current name of active sheet: '{}'.", activeSheet.getName());

        Row row = activeSheet.getRow(1);
        List<Double> list = row.getValues(Double.class);

        Column column = activeSheet.getColumn(3);

        List<Date> list1 = column.getRange(0,7,Date.class);
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
