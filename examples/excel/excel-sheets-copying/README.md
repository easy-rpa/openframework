# Copying of sheet from one Excel file to another

This example demonstrates copying of sheet from one Excel file to another with preserving of all original styles.  

```Java
    ExcelDocument src = new ExcelDocument("source.xlsx");
    ExcelDocument target = new ExcelDocument("sheet_copy_target.xlsx");

    Sheet targetSheet = target.createSheet("Passengers");

    src.selectSheet("Passengers").copy(targetSheet);

    target.save();
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
6. Run `main()` method of `SheetsCopyingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/excel/excel-sheets-copying

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to source spreadsheet file. It can be path on local file system or within resources of this module.
    </td></tr>        
    <tr><td valign="top"><code>source.sheet.name</code></td><td>
        Name of sheet in the source spreadsheet file that has to be copied.
    </td></tr>
    <tr><td valign="top"><code>target.spreadsheet.file</code></td><td>
        Path to target spreadsheet file where the sheet has to be copied. It can be path on local file system or within 
        resources of this module.
    </td></tr>
    <tr><td valign="top"><code>output.files.dir</code></td><td>
        Path to directory on local file system where robot will put all modified within this process spreadsheet files.
    </td></tr>
</table>
