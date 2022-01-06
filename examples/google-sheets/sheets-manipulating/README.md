# Sheets manipulating

This process example show how it's possible to perform different actions with sheets GoogleSheets package functionality.

* ### Delete sheet
```Java
    public void execute() {
        GoogleSheets service = new GoogleSheets();
        
        String spreadsheetId = "testDelete";
        
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        List<String> sheetNames = spreadsheet.getSheetNames();
        String lastSheetName = sheetNames.get(sheetNames.size() - 1);

        log.info("Delete sheet with name '{}'.", lastSheetName);
        spreadsheet.removeSheet(lastSheetName);
    }
```

* ### Rename sheet
```Java
    public void execute() {
        GoogleSheets service = new GoogleSheets();

        String newSheetName = "RenamedSheet";
        String spreadsheetId = "test";
        
        log.info("Rename active by default sheet to '{}' for spreadsheet with id: {}", newSheetName, spreadsheetId);
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheet.getActiveSheet();

        log.info("Current name of active sheet: '{}'. Rename it to '{}'.", activeSheet.getName(), newSheetName);
        activeSheet.rename(newSheetName);
        log.info("Sheet has been renamed successfully. Current name of active sheet: '{}'", activeSheet.getName());

        spreadsheet.commit();
    }
```

* ### Clone sheet
```Java
    public void execute() {
        GoogleSheets service = new GoogleSheets();

        String clonedSheetName = "Cloned Sheet";
        String spreadsheetId = "test";

        log.info("Clone active by default sheet for spreadsheet with id: {}", spreadsheetId);
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheet.getActiveSheet();

        log.info("Active sheet name: '{}'", activeSheet.getName());

        Sheet clonedSheet = spreadsheet.cloneSheet(activeSheet.getName());
        log.info("Sheet '{}' has been cloned successfully.", clonedSheet.getName());

        log.info("Rename cloned sheet to '{}'.", clonedSheetName);
        clonedSheet.rename(clonedSheetName);

        spreadsheet.commit();
    }
```

* ### Activate sheet
```Java
    public void execute() {
        GoogleSheets service = new GoogleSheets();
        
        int sheetIndex = 1;
        String sheetName = "Sheet1";
        String spreadsheetId = "test";

        log.info("Activate sheet with name '{}' for spreadsheet document with id: {}", sheetName, spreadsheetId);
        Spreadsheet spreadsheet = service.getSpreadsheet(spreadsheetId);
        Sheet activeSheet = spreadsheet.getActiveSheet();

        log.info("Active sheet before any action: {}", activeSheet.getName());

        log.info("Activate sheet.");
        activeSheet = spreadsheet.selectSheet(sheetName);

        log.info("Active sheet after activation: {}", activeSheet.getName());

        log.info("Active sheet using index {}.", sheetIndex);
        activeSheet = spreadsheet.selectSheet(sheetIndex);

        log.info("Active sheet after activation: {}", activeSheet.getName());
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
6. Run `main()` method of `SheetsManipulatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/google-sheets/sheets-manipulating

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `spreadsheet.id` | A unique spreadsheet id that can be found in the table properties on google drive. |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64.<br> |