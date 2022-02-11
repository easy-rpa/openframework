# Working with merged cells

This process example demonstrates Excel package functionality to merge/unmerge cell groups and read/edit its values.

* #### Read value from merged cells

```java
    @Override
    public void execute() {
        String outputFilesDir = "target/output";
        String OUTPUT_FILE_NAME = "edit_merged_cells_result.xlsx";

        String cellRef = "L7";
        String newValue = "Value of merged cell is changed.";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        Cell cell = activeSheet.getCell(cellRef);

        CellRange mergedRegion = cell.getMergedRegion();
        log.info("Cell '{}' is a part of merged region: {}", cellRef, mergedRegion.formatAsString());

        log.info("Value of merged cell '{}': {}", cellRef, cell.getValue());

        log.info("Change value of merged cell '{}' to '{}'.", cellRef, newValue);
        cell.setValue(newValue);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Merge/unmerge cells

```java
    @Override
    public void execute() {
        String outputFilesDir = "target/output";
        String OUTPUT_FILE_NAME = "merged_cells_result.xlsx";

        String cellRegionToMerge = "B2:D2";
        String cellRegionToUnMerge = "I6:L11";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Merge cells '{}' on sheet '{}'.", cellRegionToMerge, activeSheet.getName());
        Cell topLeftCellOfMergedRegion = activeSheet.mergeCells(cellRegionToMerge);
        topLeftCellOfMergedRegion.getStyle().hAlign(HorizontalAlignment.CENTER).vAlign(VerticalAlignment.CENTER).apply();

        log.info("Unmerge cells '{}'.", cellRegionToUnMerge);
        activeSheet.unmergeCells(cellRegionToUnMerge);

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
6. Run `main()` method of `WorkingWithMergedCellsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-merged-cells


## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |