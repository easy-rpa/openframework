# Changing cells style

This process example demonstrates how to specify fonts, colors and borders for cells using Excel package functionality.

```java
    @Override
    public void execute() {
        String outputFilesDir = "target/output";
        
        String cell1Ref = "C3";
        String cell2Ref = "C4";
        String cell3Ref = "C5";
        
        ExcelDocument doc = new ExcelDocument("test.xlsx");

        //change document file path to avoid overwriting of original file after calling of save() method.
        doc.setFilePath(outputFilesDir + File.separator + FilenameUtils.getName(doc.getFilePath()));

        Sheet activeSheet = doc.getActiveSheet();

        log.info("Change style for cell '{}' of  sheet '{}'", cell1Ref, activeSheet.getName());
        Cell cell = activeSheet.getCell(cell1Ref);
        ExcelCellStyle boldRedStyle = cell.getStyle().bold(true).color(ExcelColors.RED.get());
        cell.setStyle(boldRedStyle);

        log.info("Change style for cell '{}' of  sheet '{}'", cell2Ref, activeSheet.getName());
        activeSheet.getCell(cell2Ref).getStyle().italic(true).color(ExcelColors.BLUE.get()).apply();

        log.info("Set new style for cell '{}' of  sheet '{}'", cell3Ref, activeSheet.getName());
        ExcelCellStyle newStyle = new ExcelCellStyle().fontSize(14)
        .fill(FillPatternType.SOLID_FOREGROUND).background(ExcelColors.LIGHT_GREEN.get());
        activeSheet.getCell(cell3Ref).setStyle(newStyle);

        log.info("Save changes for the document.");
        doc.save();

        log.info("Style for cell '{}' has been specified successfully.", cell1Ref);
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
6. Run `main()` method of `CellStylesChangingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/cell-style-changing

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all edited within this process spreadsheet files. |