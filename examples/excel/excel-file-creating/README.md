## Creating of new Excel file

This example creates new Excel file on local file system using collection of specific Java objects.

```Java
    ExcelDocument doc = new ExcelDocument();

    List<Passenger> data = loadSampleData("passengers.json");
    doc.getActiveSheet().insertTable("C3", data);

    doc.saveAs("passengers.xlsx");
```

> The `insertTable()` inserts a new table where rows contains data of passed Java objects into it. The specification of 
table header and its cells styling are specified using `@ExcelColumn` annotations within Java class of passed objects.
See the full definition of 
[Passenger](src/main/java/eu/easyrpa/examples/excel/excel_file_creating/entities/Passenger.java)
class for all details.

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
6. Run `main()` method of `ExcelFileCreatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/excel-file-creating

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>sample.data.file</code></td><td>
        Path to JSON file that contains sample data for this process. It can be path on local file system or within 
        resources of this module.
    </td></tr>
    <tr><td valign="top"><code>output.files.dir</code></td><td>
        Path to directory on local file system where robot will put all created within this process example 
        spreadsheet files. 
    </td></tr>    
</table>
