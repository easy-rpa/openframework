# Working with formulas

This process example shows how to evaluate and get values of cells that contains formulas.

* #### Edit cell's formulas

```java
    @Override
    public void execute() {
        String outputFilesDir = "target/output";
    
        String cellWithFormulaRef = "C5";
        String newFormula = "C10 + C11 + 100";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Edit formula for cell '{}' of sheet '{}'", cellWithFormulaRef, activeSheet.getName());

        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        cell.setFormula(newFormula);
        log.info("Formula for cell '{}' has been changed/set successfully.", cellWithFormulaRef);

        updatedSpreadsheetFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + FilenameUtils.getName("test.xlsx"));
        log.info("Save changes to '{}'.", updatedSpreadsheetFilePath);
        doc.saveAs(updatedSpreadsheetFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Evaluating of cell's formulas

```java
    @Override
    public void execute() {
        String outputFilesDir = "target/output";

        String cellWithFormulaRef = "C5";
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Get value of cell with formula '{}' the same way as for simple cell.", cellWithFormulaRef);
        Cell cell = activeSheet.getCell(cellWithFormulaRef);
        log.info("Evaluated value of cell '{}': {}; Formula: {}", cellWithFormulaRef, cell.getValue(), cell.getFormula());
    }
```

* #### Evaluating of cell's formulas with links to external Excel files

```java
    @Override
    public void execute() {
        String updatedSpreadsheetFilePath;
        String sharedSpreadsheetFilePath;
        
        String targetCellRef = "C7";
        String newFormulaWithExternalLink = String.format("C5 + [%s]Data!B6", FilenameUtils.getName(sharedSpreadsheetFilePath));
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Link document '{}' to the source document '{}'.", sharedSpreadsheetFilePath, updatedSpreadsheetFilePath);
        doc.linkExternalDocument(new ExcelDocument(sharedSpreadsheetFilePath));

        log.info("Set formula '{}' to cell '{}' and get it's value.", newFormulaWithExternalLink, targetCellRef);
        Cell cell = activeSheet.getCell(targetCellRef);
        cell.setFormula(newFormulaWithExternalLink);
        log.info("Evaluated value of cell '{}': {}", targetCellRef, cell.getValue());

        log.info("Save changes");
        doc.save();

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
6. Run `main()` method of `WorkingWithFormulasModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-formulas

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `shared.spreadsheet.file` | Path to the shared spreadsheet file that has data used in formulas of source file.  It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |