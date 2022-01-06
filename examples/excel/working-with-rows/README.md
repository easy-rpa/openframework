# Working with sheet rows

This process example show what is possible to do with sheet rows of spreadsheet document using Excel package 
functionality.

* #### Lookup and edit rows

```java
    @Override
    public void execute() {
        String passengerName = "Moran, Mr. James";
        String OUTPUT_FILE_NAME = "rows_edit_result.xlsx";
        String outputFilesDir = "target/output";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet sheet = doc.selectSheet("Editing");

        log.info("Lookup row that contains value '{}' in cells.", passengerName);
        Row row = sheet.findRow(passengerName);
        row.setValue("F", 99999);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Insert new rows

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "rows_insert_result.xlsx";
        String outputFilesDir = "target/output";

        int insertBeforeRow = 4;
        String insertAfterCell = "D10";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet sheet = doc.selectSheet("Inserting");

        List<List<String>> data = new ArrayList<>();
        List<String> dataRow = new ArrayList<>();
        dataRow.add(String.format("Value %d %d", 1, 1));
        data.add(dataRow);

        log.info("Insert rows after cell '{}' of sheet '{}'", insertAfterCell, sheet.getName());
        sheet.insertRows(InsertMethod.AFTER, insertAfterCell, data);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Delete rows

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "rows_delete_result.xlsx";
        String outputFilesDir = "target/output";

        int rowIndex = 8;
        String lookupValue = "keyword1";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet sheet = doc.selectSheet("Deleting");

        log.info("Delete row with num '{}' from sheet '{}'", rowIndex + 1, sheet.getName());
        sheet.removeRow(rowIndex);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

See the full source of this example for more details or check following instructions to run it.

### Running

>:warning: **To be able to build and run this example it's necessary to have an access
>to some instance of EasyRPA Control Server.**

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
4. If necessary, change version of `easy-rpa-engine-parent` in the same `pom.xml` to corresponding version of
   EasyRPA Control Server:
    ```xml
    <parent>
        <groupId>eu.ibagroup</groupId>
        <artifactId>easy-rpa-engine-parent</artifactId>
        <version>[Replace with version of EasyRPA Control Server]</version>
    </parent>
    ```

5. Build it using `mvn clean install` command. This command should be run within directory of this example.
6. Run `main()` method of `WorkingWithRowsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-rows

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |