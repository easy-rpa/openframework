# Reading data from given sheet

This process example demonstrates different ways how it's possible to read data from Excel file 
using Excel library.  

* #### Read values of specific cells    
```Java
public void execute() {
    String sourceFile = "input.xlsx";

    log.info("Read Excel document located at: {}", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile);
    Sheet sheet = doc.getActiveSheet();
    
    Object value = sheet.getValue("D8");

    log.info("Value of cell 'D8': {}", value);
}
```
     
* #### Read values of cell range    
```Java
public void execute() {
    String sourceFile = "input.xlsx";
    String topLeftCellRef = "C4";
    String bottomRightCellRef = "M200";

    log.info("Read Excel document located at: {}", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile);
    Sheet sheet = doc.getActiveSheet();

    log.info("Get data range [ {} : {} ] of sheet '{}'.", topLeftCellRef, bottomRightCellRef, sheet.getName());
    List<List<Object>> data = sheet.getRange(topLeftCellRef, bottomRightCellRef);

    log.info("Fetched data:");
    data.forEach(rec -> log.info("{}", rec));
}
```

* #### Read the list of sheet table records     
```Java
public void execute() {
    String sourceFile = "input.xlsx";    
    String topLeftCellOfTableRef = "C3";

    log.info("Read Excel document located at: {}", sourceFile);
    ExcelDocument doc = new ExcelDocument(sourceFile);
    Sheet sheet = doc.getActiveSheet();

    log.info("List records that contains in the table on sheet '{}'.", sheet.getName());
    Table<Passenger> passengersTable = sheet.getTable(topLeftCellOfTableRef, Passenger.class);
    for (Passenger p : passengersTable) {
        log.info("{}", p);
    }
}
```

See the full source of this example for more details or check following instructions to run it.

### Running

> :warning: **To be able to build and run this example it's necessary to have an access
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
6. Run `main()` method of `SheetDataReadingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/sheet-data-reading

### Configuration

All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to spreadsheet file that has to be read. It can be path on local file system or within resources of this project. |
