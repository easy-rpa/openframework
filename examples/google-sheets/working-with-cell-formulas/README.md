# Working with formulas

This example demonstrates using of GoogleSheets library functionality to evaluate and get values of 
Google Spreadsheet files cells that contains formulas.

* #### Edit cell's formulas

```java
//TODO
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    Cell cell = activeSheet.getCell("C5");
    cell.setFormula("C10 + C11 + 100");

    doc.save();
```

* #### Evaluating of cell's formulas

```java
//TODO
    ExcelDocument doc = new ExcelDocument("source.xlsx");
    Sheet activeSheet = doc.getActiveSheet();

    Cell cell = activeSheet.getCell("C5");

    String cellFormula = cell.getFormula();
    Object evaluatedValue = cell.getValue();
```

* #### Evaluating of cell's formulas with links to external Excel files

```java
//TODO
    ExcelDocument doc = new ExcelDocument("test.xlsx");
    doc.linkExternalDocument(new ExcelDocument("sharred.xlsx"));

    Cell cell = doc.getActiveSheet().getCell("C7");
    cell.setFormula("C5 + [sharred.xlsx]Data!B6");
    
    Object evaluatedValue = cell.getValue();
    ...
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
6. Run `main()` method of `WorkingWithCellFormulasModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/excel/working-with-formulas

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>google.services.auth.secret</code></td><td>
        The alias of secret vault entry with OAuth 2.0 Client JSON necessary for authentication on the Google 
        server.<br>
        <br>
        For information regarding how to configure OAuth 2.0 Client see 
        <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a><br>
        <br>         
        In case of running of this example without EasyRPA Control Server, secret vault entries can be specified in the 
        <code>vault.properties</code> file. The value of secret vault entry in this case should be a JSON string with 
        following structure encoded with Base64:<br>
        <pre>
{
    "installed": {
      "client_id": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com",
      "project_id": "XXXXXXX-XXXXXX",
      "auth_uri": "https://accounts.google.com/o/oauth2/auth",
      "token_uri": "https://oauth2.googleapis.com/token",
      "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
      "client_secret": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
      "redirect_uris": [
          "urn:ietf:wg:oauth:2.0:oob",
          "http://localhost"
      ]
    }
}
         </pre>    
    </td></tr>      
    <tr><td valign="top"><code>source.spreadsheet.file.id</code></td><td>
         File ID of source Google Spreadsheet file.<br>
         <br>
         Expected content of the source spreadsheet can be found in <code>'source.xlsx'</code> file located at 
         <code>src/main/resources</code> directory. This file can be used for creation of necessary source Google 
         Spreadsheet in Google Drive.   
    </td></tr>    
</table>