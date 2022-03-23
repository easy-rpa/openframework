## Creating of new Spreadsheet

This example creates new Google Spreadsheet file using collection of specific Java objects.

```Java
@Inject
private GoogleDrive googleDrive;

@Inject
private GoogleSheets googleSheets;

public void execute() {
    //Create new spreadsheet file
    Optional<GFileInfo> spreadsheetFile = googleDrive.create("Passengers Book", GFileType.SPREADSHEET);

    //Get spreadsheet document related to created file
    SpreadsheetDocument doc = googleSheets.getSpreadsheet(spreadsheetFile.get().getId());

    List<Passenger> data = loadSampleData("passengers.json");
    doc.getActiveSheet().insertTable("C3", data);
}
```
> The `insertTable()` inserts a new table where rows contains data of passed Java objects into it. The specification of 
table header and its cells styling are specified using `@GSheetColumn` annotations within Java class of passed objects.
See the full definition of 
[Passenger](src/main/java/eu/easyrpa/examples/google/sheets/spreadsheet_creating/entities/Passenger.java)
class for all details.
>
See the full source of this example for more details or check following instructions to run it.

### Running

> :warning: **To be able to build and run this example it's necessary to have an access
> to some instance of EasyRPA Control Server.**

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
4. If necessary, change version of `easy-rpa-engine-parent` in the same `pom.xml` to corresponding version of EasyRPA
   Control Server:
    ```xml
    <parent>
        <groupId>eu.ibagroup</groupId>
        <artifactId>easy-rpa-engine-parent</artifactId>
        <version>[Replace with version of EasyRPA Control Server]</version>
    </parent>
    ```

5. Build it using `mvn clean install` command. This command should be run within directory of this example.
6. Run `main()` method of `SpreadsheetCreatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/google-sheets/spreadsheet_creating

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
    <tr><td valign="top"><code>sample.data.file</code></td><td>
        Path to JSON file that contains sample data for this process. It can be path on local file system or within 
        resources of this module.
    </td></tr>
</table>