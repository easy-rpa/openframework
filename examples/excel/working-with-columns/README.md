# Working with sheet columns

This example demonstrates what is possible to do with sheet columns of Excel file using Excel library.

* #### Read column cells

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    Column columnD = activeSheet.getColumn("D");
    for (Cell cell : columnD) {
        ...
    }
```

* #### Add/insert new columns

**IMPORTANT:** Excel library uses MS Excel functionality to perform inserting of column. To run this example without 
errors, MS Excel application MUST be installed on machine where this example is going to be run. 

```java
    String startWithRow = "C3";        
    List<String> columnData = Arrays.asList("Value 1", "Value 2");
    
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    activeSheet.addColumn(startWithRow, columnData);

    activeSheet.insertColumn(InsertMethod.AFTER, "C", startWithRow, columnData);

    doc.save();
```

* #### Move columns

**IMPORTANT:** Excel library uses MS Excel functionality to perform moving of column. To run this example without 
errors, MS Excel application MUST be installed on machine where this example is going to be run. 

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();
    
    String columnToMove = "D";
    String moveBeforeColumn = "F";
    activeSheet.moveColumn(columnToMove, InsertMethod.BEFORE, moveBeforeColumn);

    doc.save();
```

* #### Delete columns

**IMPORTANT:** Excel library uses MS Excel functionality to perform removing of column. To run this example without 
errors, MS Excel application MUST be installed on machine where this example is going to be run. 

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    activeSheet.removeColumn("D");

    doc.save();
```

* #### Sort table columns

**IMPORTANT:** Excel library uses MS Excel functionality to perform sorting of column. To run this example without 
errors, MS Excel application MUST be installed on machine where this example is going to be run. 

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();
    
    int columnIndexToSort = 1;
    Table<Object> table = activeSheet.findTable(Object.class, "Name");
    table.trimLeadingAndTrailingSpaces();
    table.sort(columnIndexToSort, SortDirection.DESC);

    doc.save();
```

* #### Filter table columns

**IMPORTANT:** Excel library uses MS Excel functionality to perform filtering of column. To run this example without 
errors, MS Excel application MUST be installed on machine where this example is going to be run. 

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    Table<Object> table = activeSheet.findTable(Object.class, "Name");
    table.trimLeadingAndTrailingSpaces();
    table.filter(0, "^1..").filter(5, "1");

    doc.save();
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

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/excel/working-with-columns

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to the source spreadsheet file. It can be path on local file system or within resources of this module.
    </td></tr>
    <tr><td valign="top"><code>output.files.dir</code></td><td>
        Path to directory on local file system where robot will put all modified within this process example spreadsheet 
        files. 
    </td></tr>    
</table>
