# Working with pivot tables

This process example shows how to work with pivot tables using Excel package functionality.

* #### Create pivot table

```java
    @Override
    public void execute() {
        String outputFilesDir = "target/output";
        String OUTPUT_FILE_NAME = "create_pivot_table_result.xlsx";

        String sourceSheetName = "Passengers";
        String pivotTablesSheetName = "Pivot Tables";
        String pivotTablePosition = "B5";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet sheet = doc.selectSheet(sourceSheetName);

        log.info("Create new sheet '{}' and put pivot table at position '{}' using data of sheet '{}' as source.",
        pivotTablesSheetName, pivotTablePosition, sheet.getName());
        Table<Object> sourceTable = sheet.findTable(Object.class, "Passenger Id");
        sourceTable.trimLeadingAndTrailingSpaces();

        PivotTableParams ptParams = PivotTableParams.create("Pivot Table 1")
        .position(pivotTablePosition)
        .source(sourceTable)
        .filter("Survived")
        .row("Sex").row("Class")
        .value("Passengers", "Passenger Id", PivotValueSumType.COUNT);

        Sheet pivotTablesSheet = doc.createSheet(pivotTablesSheetName);
        pivotTablesSheet.addPivotTable(ptParams);

        log.info("Pivot table created successfully.");

        excelFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", excelFilePath);
        doc.saveAs(excelFilePath);

        log.info("Excel document is saved successfully.");
    }
```

* #### Read pivot table

```java
    @Override
    public void execute() {
        String pivotTablesSheetName = "Pivot Tables";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");

        log.info("Select sheet with pivot table");
        Sheet ptSheet = doc.selectSheet(pivotTablesSheetName);

        log.info("Find Grand Total cell");
        Cell totalLabelCell = ptSheet.findCell("Grand Total");
        
        Cell totalCell = ptSheet.getCell(totalLabelCell.getRowIndex(), totalLabelCell.getColumnIndex() + 1);
        log.info("Value of Grand Total: {}", totalCell.getValue());
    }
```

* #### Update pivot table

```java
    @Override
    public void execute() {
        String sourceSheetName = "Passengers";
        String pivotTablesSheetName = "Pivot Tables";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        log.info("Select source data sheet '{}'", sourceSheetName);
        Sheet srcSheet = doc.selectSheet(sourceSheetName);

        log.info("Change source data by removing one row");
        srcSheet.removeRow(5);
        Table<Object> sourceTable = srcSheet.findTable(Object.class, "Passenger Id");
        sourceTable.trimLeadingAndTrailingSpaces();

        log.info("Update pivot table and get value of Grand Total");
        Sheet ptSheet = doc.selectSheet(pivotTablesSheetName);
        ptSheet.updatePivotTable(PivotTableParams.create("Pivot Table 1").source(sourceTable));

        Cell totalLabelCell = ptSheet.findCell("Grand Total");
        Cell totalCell = ptSheet.getCell(totalLabelCell.getRowIndex(), totalLabelCell.getColumnIndex() + 1);
        log.info("Value of Grand Total: {}", totalCell.getValue());

        log.info("Save changes");
        doc.save();

        log.info("Excel document is saved successfully.");
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
6. Run `main()` method of `WorkingWithPivotTablesModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-pivot-tables

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |