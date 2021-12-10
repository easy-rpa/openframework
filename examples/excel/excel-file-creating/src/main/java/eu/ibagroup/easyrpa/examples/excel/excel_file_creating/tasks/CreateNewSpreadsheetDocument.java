package eu.ibagroup.easyrpa.examples.excel.excel_file_creating.tasks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.excel.excel_file_creating.entities.Passenger;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.utils.FilePathUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Create New Spreadsheet Document")
@Slf4j
public class CreateNewSpreadsheetDocument extends ApTask {

    @Configuration(value = "sample.data.file")
    private String sampleDataFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {
        String newSheetName = "Passengers";

        log.info("Load sample data from '{}'.", sampleDataFile);
        List<Passenger> data = loadSampleData(sampleDataFile);

        log.info("Create new spreadsheet document.");
        ExcelDocument doc = new ExcelDocument();
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Rename active sheet to '{}'.", newSheetName);
        doc.renameSheet(activeSheet, newSheetName);

        log.info("Put data on the sheet '{}'.", newSheetName);
        activeSheet.insertTable("C3", data);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + "output.xlsx");
        log.info("Save file to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

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

