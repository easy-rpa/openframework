# Sheets manipulating

This process example show how it's possible to perform different actions with sheets Excel package functionality.  

* #### List existing sheets

```java
    @Override
    public void execute() {
        ExcelDocument doc = new ExcelDocument("test.xlsx");

        List<String> sheetNames = doc.getSheetNames();
        log.info("Spreadsheet document has following sheets: {}", sheetNames);
    }
```

* #### Activate sheet

```java
@Override
public void execute() {
        int sheetIndex = 1;
        String sheetName = "Summary";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet before any action: {}", activeSheet.getName());

        log.info("Activate sheet.");
        activeSheet = doc.selectSheet(sheetName);

        log.info("Active sheet after activation: {}", activeSheet.getName());

        log.info("Active sheet using index {}.", sheetIndex);
        activeSheet = doc.selectSheet(sheetIndex);

        log.info("Active sheet after activation: {}", activeSheet.getName());
    }
```

* #### Rename sheet

```java
@Override
public void execute() {
        String newSheetName = "Renamed Sheet";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Current name of active sheet: '{}'. Rename it to '{}'.", activeSheet.getName(), newSheetName);
        activeSheet.rename(newSheetName);
        log.info("Sheet has been renamed successfully. Current name of active sheet: '{}'", activeSheet.getName());

        String OUTPUT_FILE_NAME = "sheet_rename_result.xlsx";
        String outputFilesDir = "target/output";
        
        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Move sheet

```java
@Override
public void execute() {
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet name: '{}'", activeSheet.getName());

        int newPosition = doc.getSheetNames().size() - 1;
        activeSheet.moveTo(newPosition);

        log.info("Sheet '{}' has been moved to '{}' position successfully.", activeSheet.getName(), newPosition);

        String OUTPUT_FILE_NAME = "sheet_rename_result.xlsx";
        String outputFilesDir = "target/output";
        
        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Clone sheet

```java
@Override
public void execute() {
        String clonedSheetName = "Cloned Sheet";

        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Active sheet name: '{}'", activeSheet.getName());

        Sheet clonedSheet = activeSheet.cloneAs(clonedSheetName);
        log.info("Sheet '{}' has been cloned successfully. Current name of cloned sheet '{}'", activeSheet.getName(), clonedSheet.getName());

        String OUTPUT_FILE_NAME = "sheet_rename_result.xlsx";
        String outputFilesDir = "target/output";
        
        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Delete sheet

```java
@Override
public void execute() {     
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        List<String> sheetNames = doc.getSheetNames();
        String lastSheetName = sheetNames.get(sheetNames.size() - 1);

        log.info("Delete sheet with name '{}'.", lastSheetName);
        doc.removeSheet(lastSheetName);

        log.info("Sheet '{}' has been deleted successfully.", lastSheetName);

        String OUTPUT_FILE_NAME = "sheet_rename_result.xlsx";
        String outputFilesDir = "target/output";

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
6. Run `main()` method of `SheetsManipulatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/sheets-manipulating


## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all edited within this process spreadsheet files. |