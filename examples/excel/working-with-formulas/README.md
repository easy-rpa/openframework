# Working with formulas

This example demonstrates using of Excel library functionality to evaluate and get values of Excel files cells that 
contains formulas.

* #### Edit cell's formulas

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    Cell cell = activeSheet.getCell("C5");
    cell.setFormula("C10 + C11 + 100");

    doc.save();
```

* #### Evaluating of cell's formulas

```java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    Cell cell = activeSheet.getCell("C5");

    String cellFormula = cell.getFormula();
    Object evaluatedValue = cell.getValue();
```

* #### Evaluating of cell's formulas with links to external Excel files

```java
    ExcelDocument doc = new ExcelDocument("test.xlsx");
    doc.linkExternalDocument(new ExcelDocument("sharred.xlsx"));

    Cell cell = doc.getActiveSheet().getCell("C7");
    cell.setFormula("C5 + [sharred.xlsx]Data!B6");
    
    Object evaluatedValue = cell.getValue();
    ...
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

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to the source spreadsheet file. It can be path on local file system or within resources of this module.
    </td></tr>
    <tr><td valign="top"><code>shared.spreadsheet.file</code></td><td>
        Path to the shared spreadsheet file that has data used in formulas of source file.  It can be path on local 
        file system or within resources of this module. 
    </td></tr>  
    <tr><td valign="top"><code>output.files.dir</code></td><td>
        Path to directory on local file system where robot will put all modified within this process example spreadsheet 
        files. 
    </td></tr>    
</table>