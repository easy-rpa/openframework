# Reading data from given sheet

This example demonstrates different ways of reading data from Excel file using Excel library.  

* #### Read values of specific cells    
```Java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet sheet = doc.getActiveSheet();
    
    Object d8Value = sheet.getValue("D8");
    
    ...
```
     
* #### Read values of cell range    
```Java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet sheet = doc.getActiveSheet();

    List<List<Object>> data = sheet.getRange("C4", "M200");
    
    ...
```

* #### Read the list of sheet table records

Using `@ExcelColumn` annotation it's possible to tie Java class fields with values in specific columns of table 
from Excel file.             
 ```Java
@Data
public class Passenger {

    @ExcelColumn(name = "Passenger Id")
    private Integer passengerId;

    @ExcelColumn(name = "Name")
    private String name;

    @ExcelColumn(name = "Sex")
    private String sex;

    @ExcelColumn(name = "Age")
    private Integer age;

    ...
}     
```

After annotating of necessary fields the reading of data from Excel file looks as follows:    
```Java
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet sheet = doc.getActiveSheet();

    String topLeftCellOfTable = "C3";
    Table<Passenger> passengersTable = sheet.getTable(topLeftCellOfTable, Passenger.class);

    for (Passenger p : passengersTable) {
        ...
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

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/excel/sheet-data-reading

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>source.spreadsheet.file</code></td><td>
        Path to spreadsheet file that has to be read. It can be path on local file system or within resources of this 
        module.
    </td></tr>        
</table>