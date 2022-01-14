## Creating of new Google Sheet

Following example creates a Google Sheet in the Google Spreadsheet Document.

```Java
public void execute(){
        String spreadsheetId="id";
        String credentials="secret"
        String sampleDataFilePath="sample_data.json"
        
        log.info("Connecting to Google account with credentials: {}", credentials);
        GoogleSheets service = new GoogleSheets().secret(credentials);
        
        log.info("Load sample data from '{}'.",sampleDataFile);
        List<Passenger> data=loadSampleData(sampleDataFile);

        log.info("Create new sheet with the name '{}' in the document with id '{}'.", sheetName, spreadsheetId);
        SpreadsheetDocument doc = service.getSpreadsheet(spreadsheetId);
        Sheet sheet = doc.createSheet(sheetName);
        
        log.info("Put data on the sheet '{}'.",activeSheet.getName());
        activeSheet.insertTable("C3",data);

        log.info("Spreadsheet document has been saved successfully");
        }
```

See the full source of this example for more details or check following instructions to run it.

### Running

> :warning: **To be able to build and run this example it's necessary to have an access
> to some instance of EasyRPA Control Server.**

Its a fully workable process. To play around with it and run do the following:

1. Download this example using [link][down_git_link].
2. Unpack it somewhere on local file system.
3. Specify URL to the available instance of EasyRPA Control Server in the `pom.xml` of this example:
    ```xml
    <repositories>
        <repository>
            <id>easy-rpa-repository</id>
            <url>[Replace with EasyRPA Control Server URL]/nexus/repository/easyrpa/</url>
        </repository>
    </repositories>
    ```
4. If necessary, change version of `easy-rpa-engine-parent` in the same `pom.xml` to corresponding version of EasyRPA
   Control Server:
    ```xml
    <parent>
        <groupId>eu.ibagroup</groupId>
        <artifactId>easy-rpa-engine-parent</artifactId>
        <version>[Replace with version of EasyRPA Control Server]</version>
    </parent>
    ```

5. Build it using `mvn clean install` command. This command should be run within directory of this example.
6. Run `main()` method of `SheetsCreatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/excel-file-creating

### Configuration

All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `spreadsheet.id` | Id of spreadsheet file that has to be read.|
| `sheet.name` | Sheet name of the spreadsheet that has to be read.|
| `google.credentials` | Vault alias that contains credentials for authentication on the google server.|
| `sample.data.file` | Path to JSON file that contains sample data for this process. It can be path on local file system or within resources of this project|
 