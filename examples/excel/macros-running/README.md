# Running of macros

This example demonstrates using of Excel library functionality to run embedded to spreadsheet document macros.

```java
    ExcelDocument doc = new ExcelDocument("source.xlsm");

    doc.runMacro("test_macro");

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
6. Run `main()` method of `MacrosRunningModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/macros-running

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to source macro-enabled spreadsheet file. It can be path on local file system or within resources of 
        this module.
    </td></tr>
    <tr><td valign="top"><code>macro.function.name</code></td><td>
         Name of macro function that is need to run.
    </td></tr>
    <tr><td valign="top"><code>output.spreadsheet.file</code></td><td>
        Path on local file system where robot will save modified by macro spreadsheet file.
    </td></tr>    
</table>
