# Working with sheet columns

This process example show what is possible to do with sheet columns of spreadsheet document using Excel package 
functionality.

* #### Read column cells

```java
    @Override
    public void execute() {
        String columnToReadRef = "D";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Read cells of column '{}'", columnToReadRef);
        Column column = activeSheet.getColumn(columnToReadRef);
        for (Cell cell : column) {
            Object value = cell.getValue();
            log.info("Cell value at row '{}': {} ({})", cell.getRowIndex(), value, (value != null ? value.getClass() : "null"));
        }
    }
```

* #### Add/insert new columns

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "column_insert_result.xlsx";
        String outputFilesDir = "target/output";
        String insertAfterColumn = "C";
        String startWithRow = "C3";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        List<String> data = new ArrayList<>();
        data.add(String.format("Value %d", 1));

        log.info("Add column to the end of sheet '{}'", activeSheet.getName());
        activeSheet.addColumn(startWithRow, data);

        log.info("Insert column after column '{}' of sheet '{}'", insertAfterColumn, activeSheet.getName());
        activeSheet.insertColumn(InsertMethod.AFTER, insertAfterColumn, startWithRow, data);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Move columns

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "column_move_result.xlsx";
        String outputFilesDir = "target/output";
        
        String columnToMoveRef = "D";
        String moveBeforeColumn = "F";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Move column '{}' before column '{}' of sheet '{}'", columnToMoveRef, moveBeforeColumn, activeSheet.getName());
        activeSheet.moveColumn(columnToMoveRef, InsertMethod.BEFORE, moveBeforeColumn);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Delete columns

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "column_delete_result.xlsx";
        String outputFilesDir = "target/output";

        String columnToDeleteRef = "D";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Delete column '{}' from sheet '{}'", columnToDeleteRef, activeSheet.getName());
        activeSheet.removeColumn(columnToDeleteRef);

        log.info("Column '{}' has been deleted successfully.", columnToDeleteRef);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Sort table columns

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "column_sort_result.xlsx";
        String outputFilesDir = "target/output";

        int columnIndexToSort = 1;
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Find table on sheet '{}' and sort it's '{}' column", activeSheet.getName(), columnIndexToSort);
        Table<Object> table = activeSheet.findTable(Object.class, "Name");
        table.trimLeadingAndTrailingSpaces();
        table.sort(columnIndexToSort, SortDirection.DESC);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + OUTPUT_FILE_NAME);
        log.info("Save changes to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);

        log.info("Spreadsheet document is saved successfully.");
    }
```

* #### Filter table columns

```java
    @Override
    public void execute() {
        String OUTPUT_FILE_NAME = "column_filter_result.xlsx";
        String outputFilesDir = "target/output";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Find table on sheet '{}' and filter it's rows", activeSheet.getName());
        Table<Object> table = activeSheet.findTable(Object.class, "Name");
        table.trimLeadingAndTrailingSpaces();
        table.filter(0, "^1..").filter(5, "1");

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
6. Run `main()` method of `WorkingWithColumnsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-columns

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.