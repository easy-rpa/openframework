# Google Sheets

## Table of Contents
* [Description](#description)
* [Quick start](#quick-start)
* [Configuration](#configuration)
* [How To](#how-to)
* [Example](#example)

## Description

Component which provides functionality related to Google Sheets.

## Quick start

To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-google-sheets)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-google-sheets</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

After that you need build the project via Maven command:

```java
mvn clean install
```
After execution of this command you should see such message:

![Screenshot-1.png](https://i.postimg.cc/s2Dmc3w1/Screenshot-1.png)

## Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

| Parameter     | Value                                                                    |
| ------------- |--------------------------------------------------------------------------|
| `spreadsheet.id.copyFrom` |                                                                          |
| `spreadsheet.id.copyTo` |                                                                          |
| `sheet.name` | Name of the sheet to manipulate                                          |
| `spreadsheet.id` |  |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

## How to

* **Copy Sheet Between Spreadsheets**
```java
package eu.ibagroup.easyrpa.examples.googlesheets.sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.Spreadsheet;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Copy Sheet between Spreadsheets")
@Slf4j
public class CopySheetBetweenSpreadsheets extends ApTask {

    @Configuration(value = "spreadsheet.id.copyFrom")
    private String spreadsheetIdFrom;

    @Configuration(value = "spreadsheet.id.copyTo")
    private String spreadsheetIdTo;

    @Configuration(value = "sheet.name")
    private String sheetName;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        log.info("Copy sheet '{}' from spreadsheet with id '{}' to '{}'", sheetName, spreadsheetIdFrom, spreadsheetIdTo);

        Spreadsheet spreadsheetFrom = service.getSpreadsheet(spreadsheetIdFrom);
        Spreadsheet spreadsheetTo = service.getSpreadsheet(spreadsheetIdTo);

        Sheet sourceSheet = spreadsheetFrom.selectSheet(sheetName);

        spreadsheetTo.copySheet(sourceSheet);
        log.info("Sheet '{}' has been copied successfully.", sheetName);
    }
}
```
* **Create New Google Sheets Document**
```java
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
import eu.ibagroup.easyrpa.openframework.googlesheets.Spreadsheet;
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

        List<Object> headerRow = new ArrayList();
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

        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheet.getActiveSheet();

        String docName = spreadsheet.getName();

        log.info("docName = "+ docName);

        ValueRange res = service.getValues(spreadsheetId, "A1:D4");

        service.updateValues(spreadsheetId, "A15", "RAW", tableData);
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
```

* **Delete sheet**
```java
package eu.ibagroup.easyrpa.examples.googlesheets.sheets_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googlesheets.GoogleSheets;
import eu.ibagroup.easyrpa.openframework.googlesheets.Spreadsheet;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Delete Sheet")
@Slf4j
public class DeleteSheet extends ApTask {

    @Configuration(value = "spreadsheet.id")
    private String spreadsheetId;

    @Inject
    private GoogleSheets service;

    @Override
    public void execute() {
        log.info("Delete the last sheet from spreadsheet document with id: {}", spreadsheetId);
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        List<String> sheetNames = spreadsheet.getSheetNames();
        String lastSheetName = sheetNames.get(sheetNames.size() - 1);

        log.info("Delete sheet with name '{}'.", lastSheetName);
        spreadsheet.removeSheet(lastSheetName);

        log.info("Sheet '{}' has been deleted successfully.", lastSheetName);
    }
}
```

## Example

For more code examples please refer to corresponding [article](https://github.com/dzyap/openframework/tree/main/examples#google-sheets). 