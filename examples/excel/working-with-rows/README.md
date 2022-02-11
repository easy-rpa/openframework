# Working with sheet rows

This example demonstrates what is possible to do with sheet rows of Excel file using Excel library.

* #### Lookup and edit rows

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet sheet = doc.selectSheet("Editing");

    Row row = sheet.findRow("Moran, Mr. James");
    row.setValue("F", 99999);

    doc.save();
```

* #### Insert new rows

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet sheet = doc.selectSheet("Inserting");

    List<List<String>> data = Arrays.asList(
        Arrays.asList("Value 1 1", "Value 2 1"),
        Arrays.asList("Value 1 2", "Value 2 2") 
    );

    sheet.insertRows(InsertMethod.AFTER, "D10", data);

    doc.save();
```

* #### Delete rows

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet sheet = doc.selectSheet("Deleting");

    sheet.removeRow(8);

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
6. Run `main()` method of `WorkingWithRowsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-rows

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
