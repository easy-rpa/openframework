# Exporting Excel file to PDF

This process example demonstrates how to export spreadsheet document to PDF file using Excel package functionality.

**IMPORTANT:** Excel package uses MS Excel functionality to perform exporting to PDF. To run this process without 
errors it's required the MS Excel to be installed on machine where the process is running. 

```java
    @Override
    public void execute() {
        String sourceSpreadsheetFile = "test.xlsx";
        String outputPdfFile = "test.pdf";
    
        log.info("Export active sheet of spreadsheet document located at '{}'", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();
        
        log.info("Export sheet '{}' to PDF file located at '{}'", activeSheet.getName(), outputPdfFile);
        activeSheet.exportToPDF(outputPdfFile);

        log.info("Sheet has been exported successfully.");
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
6. Run `main()` method of `ExportToPDFModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/export-to-pdf

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.pdf.file` | Path on local file system where spreadsheet document is going to be exported. |