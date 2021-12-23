## Editing of existing Excel file

This process example demonstrates different ways of editing existing Excel file.  

* #### Edit specific cells on the sheet     
```Java
public void execute() {
    String sourceFile = "source.xlsx";

    log.info("Open spreadsheet document located at '{}' and edit.", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile);
    Sheet dataSheet = doc.selectSheet("Data");

    log.info("Edit cells on sheet '{}'", dataSheet.getName());
    dataSheet.setValue("B2", "Some text");
    dataSheet.setValue("C3", 120);
    dataSheet.setValue("D4", DateTime.now());

    log.info("Save changes");
    doc.save();
}
```

* #### Edit specific cell range of the sheet     
```Java
public void execute() {
    String sourceFile = "source.xlsx";
    List<List<String>> sampleData = getSampleData(20, 100);

    log.info("Open spreadsheet document located at '{}' and edit.", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile);
    Sheet dataSheet = doc.selectSheet("Data");

    log.info("Put range of sample data on sheet '{}'", dataSheet.getName());
    dataSheet.putRange("D11", sampleData);

    log.info("Save changes");
    doc.save();
}
```

* #### Edit sheet table records     
```Java
public void execute() {
    String sourceFile = "source.xlsx";
    String passengerName = "Wheadon, Mr. Edward H";

    log.info("Open spreadsheet document located at '{}' and edit.", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile);
    Sheet activeSheet = doc.getActiveSheet();

    log.info("Lookup Passengers table on sheet '{}'", activeSheet.getName());
    Table<Passenger> passengersTable = activeSheet.findTable(Passenger.class, "Passenger Id", "Name");

    log.info("Lookup record by specific condition in the table");
    Passenger record = passengersTable.findRecord(r -> passengerName.equals(r.getName()));

    log.info("Edit Age of the record.");
    record.setAge(110);

    log.info("Update corresponding record on sheet.");
    passengersTable.updateRecord(record);

    log.info("Save changes");
    doc.save();
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
6. Run `main()` method of `ExcelFileEditingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/excel-file-editing

### Configuration

All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to spreadsheet file that has to be edited. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all edited within this process spreadsheet files. |
