## Creating of new Excel file

Following example creates a XLSX file on the local file system. 

```Java
public void execute() {
    String sampleDataFile = "passengers.json";
    String outputFilePath = "passengers.xlsx";

    log.info("Load sample data from '{}'.", sampleDataFile);
    List<Passenger> data = loadSampleData(sampleDataFile);

    log.info("Create new spreadsheet document.");
    ExcelDocument doc = new ExcelDocument();
    Sheet activeSheet = doc.getActiveSheet();

    log.info("Put data on the sheet '{}'.", activeSheet.getName());
    activeSheet.insertTable("C3", data);

    log.info("Save file to '{}'.", outputFilePath);
    doc.saveAs(outputFilePath);
}
```

See the full source of this example for more details or check following instructions to run it.

### Running

> :heavy_exclamation_mark: **PREREQUISITES:** To be able to build and run this example it's necessary to have an access
>to some instance of EasyRPA Control Server.   

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
4. If necessary, change version of `easy-rpa-engine-parent` in the same `pom.xml` to the version of 
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

All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `sample.data.file` | Path to JSON file that contains sample data for this process. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all created within this process spreadsheet files. |
 