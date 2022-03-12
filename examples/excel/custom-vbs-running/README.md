# Running of custom VB script

This example demonstrates using of Excel library functionality to to run custom VB script for some spreadsheet document.

```java
    ExcelDocument doc = new ExcelDocument("test.xlsx");
    doc.runScript("custom.vbs");
    doc.save();
```

*custom.vbs* 

```vbs
Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

Set sheet1 = workbook.Sheets("Sheet1")
sheet1.Select
Set cellB6 = sheet1.Range("B6")
cellB6.Select
cellB6.Value = "TEST"
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
6. Run `main()` method of `CustomVbsRunningModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/excel/custom-vbs-running

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to source spreadsheet file. It can be path on local file system or within resources of this module.
    </td></tr>
    <tr><td valign="top"><code>vb.script.file</code></td><td>
        Path to VBS file that contains script to run. It can be path on local file system or within resources of 
        this module.
    </td></tr>    
</table>
