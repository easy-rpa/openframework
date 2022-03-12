# Google Sheets

## Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Copy Sheet Between Spreadsheets](#copy-sheet-between-spreadsheets)
* [Update Google Sheets Document with data](#update-google-sheets-document-with-data)
* [Example](#example)

## Description

Component which provides functionality related to Google Sheets.

## Usage

To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-sheets)

```java
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-google-sheets</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```


## Copy Sheet Between Spreadsheets

In this example we will copy a sheet from one document to another. First, to do so, we need configure our parameters:

**apm_run.properties**

| Parameter     | Value                           |
| ------------- |---------------------------------|
| `spreadsheet.id.copyFrom` | id of spreadsheet to copy from  |
| `spreadsheet.id.copyTo` | id of spreadsheet to copy                                |
| `sheet.name` | Name of the sheet to manipulate |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

Note: all necessary configuration files can be found in `src/main/resources` directory.

On order to manipulate with Google Sheets we have to inject 'GoogleSheets' object:

```java
    @Inject
    private GoogleSheets service;
```

After that we get the two Google documents by id specified in configuration parameters:

```java
        Spreadsheet spreadsheetFrom = service.getSpreadsheet(spreadsheetIdFrom);
        Spreadsheet spreadsheetTo = service.getSpreadsheet(spreadsheetIdTo)
```

Next step - get sheet content to copy:

```java
        Sheet sourceSheet = spreadsheetFrom.selectSheet(sheetName);
```

And finally we can clone this sheet to another Google document:

```java
        spreadsheetTo.copySheet(sourceSheet);
```

## Update Google Sheets Document with data

In this section we will create a new Google Sheet document from scratch.
As in previous example we have to specify few configuration parameters:

**apm_run.properties**

| Parameter     | Value                                                       |
| ------------- |-------------------------------------------------------------|
| `sample.data.file` | the file which will be used as input data for a new document |
| `spreadsheet.id` | id of spreadsheet                                   |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

Note: all necessary configuration files can be found in `src/main/resources` directory.

In a next step we as usual inject object of 'GoogleSheets':

```java
    @Inject
    private GoogleSheets service;
```

For test data which will be filled inside Google Sheet we are going to use a simple json file:

![passegenrs.png](https://i.postimg.cc/qqz0FLbP/passegenrs.png)

To populate a sheet we create list of objects. First object in this list - headers:

```java
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
```

Finally using function 'updateValues' we update Google sheet with data loaded from sample Json file:

```java
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheet.getActiveSheet();

        String docName = spreadsheet.getName();

        log.info("docName = "+ docName);

        ValueRange res = service.getValues(spreadsheetId, "A1:D4");

        service.updateValues(spreadsheetId, "A15", "RAW", tableData);
```

## Example

For more code examples please refer to corresponding [article](https://github.com/easy-rpa/openframework/tree/main/examples#google-sheets). 