# Working with large files

This process example demonstrates how Excel library can work with large Excel files.  

* #### Read sheet table records
```Java
public void execute() {
    String sourceFile = "input_400k.xlsx";
    int startIndex = 350000;
    int endIndex = 360000;

    log.info("Open Excel file located at: {}", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile, true); // second parameter is a special flag 
                                                             // that allows to save memory
    Sheet sheet = doc.getActiveSheet();

    log.info("Find table on sheet '{}'.", sheet.getName());
    Table<Passenger> passengersTable = sheet.findTable(Passenger.class, MatchMethod.EXACT, "Passenger Id");

    log.info("List each record with 1000th PassengerId between '{}' and '{}'.", startIndex, endIndex);
    for (int i = startIndex; i <= endIndex; i++) {
        Passenger p = passengersTable.getRecord(i);
        if (p.getPassengerId() % 1000 == 0) {
            log.info("{}", p);
        }
    }
}
```

* #### Edit sheet table records
```Java
public void execute() {
    String sourceFile = "input_400k.xlsx";
    Integer passengerId = 35500;

    log.info("Open Excel file located at: {}", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile, true); // second parameter is a special flag 
                                                             // that allows to save memory
    Sheet sheet = doc.getActiveSheet();

    log.info("Lookup Passengers table on sheet '{}'", sheet.getName());
    Table<Passenger> passengersTable = activeSheet.findTable(Passenger.class, "Passenger Id", "Name");

    log.info("Lookup record by specific condition in the table");
    Passenger record = passengersTable.findRecord(r -> passengerId.equals(r.getPassengerId()));
 
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
6. Run `main()` method of `WorkingWithLargeFilesModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-large-files

##№ Configuration

All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |
