# Google Sheets

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Reading of Google spreadsheet](#reading-of-google-spreadsheet)
* [Creating of Google spreadsheet](#creating-of-google-spreadsheet)
* [Other examples](#other-examples)
* [Configuration parameters](#configuration-parameters)

### Description

EasyRPA Open Framework **Google Sheets** library provides functionality to work with Google spreadsheets. It wraps the 
client service `Sheets` of Google Sheets API and works via extended objects model representing Google spreadsheet 
and its elements. The objects model has easy to use interface that is adapted to work within EasyRPA platform or any 
other RPA platform where processes are built using Java program language.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-sheets)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-google-sheets</artifactId>
    <version>1.0.0</version>
</dependency>
```

Additionally, to let the library collaborate with RPA platform make sure that Maven dependency to corresponding adapter 
is added also. 

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>2.3.1</version>
</dependency>
```
In order to work with Google Sheets API it's necessary to have Google Cloud project with configured authentication 
and authorization for using this API. Follow next steps to do everything properly.

1. [Create a Google Cloud project][create_project_link] if it doesn't exist yet. 
2. [Enable Google Sheets API][enable_apis_link] in the Google Cloud project.
3. [Configure OAuth consent screen][configure_oauth_consent_link] to let robot requests the access to Google spreadsheets.
4. [Create OAuth client ID credentials][create_credentials_link] to authenticate the robot on Google Cloud.

Read [how Google Workspace APIs authentication and authorization works][auth_overview_link] and
[Authentication Best Practices][best_practices_link] for some more useful information.

[create_project_link]: https://developers.google.com/workspace/guides/create-project
[enable_apis_link]: https://developers.google.com/workspace/guides/enable-apis
[auth_overview_link]: https://developers.google.com/workspace/guides/auth-overview
[configure_oauth_consent_link]: https://developers.google.com/workspace/guides/configure-oauth-consent
[create_credentials_link]: https://developers.google.com/workspace/guides/create-credentials#oauth-client-id
[best_practices_link]: https://www.google.com/support/enterprise/static/gapps/docs/admin/en/gapps_workspace/Google%20Workspace%20APIs%20-%20Authentication%20Best%20Practices.pdf

### Reading of Google spreadsheet

The Google Sheets API is a RESTful interface that lets you read and modify only values of specific cells or 
cells ranges. It doesn't have any objects mapping functionality that can significantly simplify the work with 
spreadsheet data within RPA processes. This library solves it. Often, the data on sheet is presented using tables. 
Its a specific range of cells where each cell belongs to some column that is titled with value of its top row or rows. 
Lets take a following example that contains table with list of persons:

<p align="center">
  <img src="https://i.postimg.cc/N08SDhHr/google-spreadsheet-persons.png">
</p>

Using provided `@GSheetColumn` annotation it's possible to tie java class attributes with values in specific columns.             
 ```Java
@Data
public class Person{
    @GSheetColumn(name="Person Id")
    private String id;

    @GSheetColumn(name="Name")
    private String name;

    @GSheetColumn(name="Age")
    private Integer age;

    @GSheetColumn(name="Sex")
    private String sex;
}     
```
After annotating of necessary attributes it's very convenient and easy to read data from Google spreadsheet and work 
with it.
```Java
@Inject
private GoogleSheets googleSheets;

public void execute() {
    //Specify Google file ID of the source spreadsheet file by some way
    String sourceSpreadsheetFileId = ...;

    SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);

    Table<Person> personsTable = doc.getActiveSheet().getTable("B3", Person.class);
    for (Person p : personsTable) {
        String personName = p.getName();
        //handling of person data
    }    
}
```

Here, the `GoogleSheets` is a main class of Google Sheets library. It performs authentication on Google Cloud and 
creates authorized service `Sheets` of Google Sheets API. Further, this service is used by `SpreadsheetDocument` 
for reading and modifying of corresponding Google spreadsheet. 

To let the `GoogleSheets` works correctly it should be provided with **OAuth client JSON** necessary for authentication 
on Google Cloud. This JSON can be provided explicitly via `secret()` method but implicit way via `@Inject` annotation as
in example above is recommended. In case of injection of `GoogleSheets` using `@Inject` annotation the 
OAuth client JSON is expected to be defined via configuration parameter of the RPA process with key 
 **`google.services.auth.secret`**. The value of this parameter is an alias of secret vault entry with necessary JSON.
  
 ```properties
 google.services.auth.secret=robot.google.account
 ``` 

 ```properties
 robot.google.account=<secret OAuth client JSON>
 ``` 

The **OAuth client JSON** can be downloaded from Google Cloud Console by the following way:
1. Open the [Google Cloud Console](https://console.cloud.google.com)
2. At the top-left, click **Menu > APIs & Services > Credentials**.
3. Lookup a record with corresponding **OAuth client ID** that is created for working with Google Sheets. 
See instructions above how to do it.
5. In the end of row choose **Download OAuth client** action.
6. Click **DOWNLOAD JSON** button in the opened window.

The downloaded JSON should looks as follows:
```json
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
```

### Creating of Google spreadsheet

The same java object class `Person` that was used for reading Google spreadsheet above can be used for inserting 
new rows into the table:
 ```Java
@Inject
private GoogleSheets googleSheets;

public void execute() {
    //Specify Google file ID of the source spreadsheet file by some way
    String sourceSpreadsheetFileId = ...;

    //Get persons list by some way that should be added into spreadsheet 
    List<Person> newPersons = getPersonsToAdd();
  
    SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFileId);

    Table<Person> personsTable = doc.getActiveSheet().getTable("B3", Person.class);
    personsTable.addRecords(newPersons);
}
```

Or building such table from scratch in the new Google spreadsheet:
```Java
@Inject
private GoogleDrive googleDrive;

@Inject
private GoogleSheets googleSheets;

public void execute() {

    //Get persons list by some way that should be inserted into spreadsheet 
    List<Person> persons = getPersonsList();

    //Create a new spreadsheet file on Drive using Google Drive library 
    Optional<GFileInfo> spreadsheetFile = googleDrive.create(SPREADSHEET_NAME, GFileType.SPREADSHEET);

    if (spreadsheetFile.isPresent()) {
        //Read just created spreadsheet file and insert the table with persons into it
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(spreadsheetFile.get().getId());

        doc.getActiveSheet().insertTable("B3", persons);
    }
}
```
By default all new rows will be rendered with default font and without borders and colors. This can be changed using 
the same annotation `@GSheetColumn` and addition annotation `@GSheetTable`. The difference between `@GSheetColumn` and 
`@GSheetTable` annotations is that `@GSheetColumn` is applied only to cells of corresponding column whereas `@GSheetTable` 
is applied to the whole table. There are two properties `headerStyle` and `cellStyle` for header cell and ordinary cells 
respectively. These properties accepts another annotation `@GSheetCellStyle` that helps to specify specific cell styles 
parameters. 

Lets do some styling:
 
 ```Java
@Data
@GSheetTable(
        headerStyle = @GSheetCellStyle(
                bold = true, background = Colors.LIGHT_GRAY,                    
                hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.CENTER
        ),
        cellStyle = @GSheetCellStyle(
                hAlign = HorizontalAlignment.CENTER, vAlign = VerticalAlignment.TOP
        )     
)
public class Person{
    @GSheetColumn(name="Person Id", width = 10)
    private String id;

    @GSheetColumn(name="Name", width = 20,
            cellStyle = @GSheetCellStyle(
                    hAlign = HorizontalAlignment.LEFT, vAlign = VerticalAlignment.TOP
            ) 
    )
    private String name;

    @GSheetColumn(name="Age", width = 10)
    private Integer age;

    @GSheetColumn(name="Sex", width = 10)
    private String sex;
}     
```

The following result should be gotten after calling of `insertTable()`:

<p align="center">
  <img src="https://i.postimg.cc/gJBgZQyR/google-spreadsheet-persons-2.png">
</p>

### Other examples

Please refer to [Google Sheets Examples](../../examples#google-sheets) to see more examples of using this library.

### Configuration parameters

This library uses **Google Services** library for authorization and creation of underlying `Sheets` class of 
Google Sheets API and hence its configuration parameters are actual for this library too. Please refer to 
[Google Services configuration parameters section](../google-services#configuration-parameters) to see their 
descriptions.  