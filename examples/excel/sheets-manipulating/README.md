# Sheets manipulating

This example demonstrates using of Excel library functionality to perform different actions with sheets of Excel file.  

* #### List existing sheets

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");

    List<String> sheetNames = doc.getSheetNames();
    
    ...
```

* #### Activate sheet

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");

    //Get currrent active sheet
    Sheet activeSheet = doc.getActiveSheet();
    
    //Activate another sheet by name
    activeSheet = doc.selectSheet("Summary");

    //Activate another sheet by index
    activeSheet = doc.selectSheet(1);
```

* #### Rename sheet

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();
    
    String newSheetName = "Renamed Sheet";
    activeSheet.rename(newSheetName);
    
    doc.save();
```

* #### Move sheet

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    int newPosition = doc.getSheetNames().size() - 1;
    activeSheet.moveTo(newPosition);

    doc.save();
```

* #### Clone sheet

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();
    
    String clonedSheetName = "Cloned Sheet";
    Sheet clonedSheet = activeSheet.cloneAs(clonedSheetName);
    ...               

    doc.save();
```

* #### Delete sheet

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");

    List<String> sheetNames = doc.getSheetNames();
    String lastSheetName = sheetNames.get(sheetNames.size() - 1);

    doc.removeSheet(lastSheetName);

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
6. Run `main()` method of `SheetsManipulatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/sheets-manipulating

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to the source spreadsheet file. It can be path on local file system or within resources of this module.
    </td></tr>
    <tr><td valign="top"><code>output.files.dir</code></td><td>
        Path to directory on local file system where robot will put all edited within this process example spreadsheet 
        files. 
    </td></tr>    
</table>
