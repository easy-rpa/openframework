# Google Drive

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Getting of file from Drive](#getting-of-file-from-drive)
* [Other examples](#other-examples)
* [Configuration parameters](#configuration-parameters)

### Description

EasyRPA Open Framework **Google Drive** library provides functionality to work with files and folders located on 
Google Drive. It wraps the client service `Drive` of Google Drive API and works via extended objects model representing 
files and folders. Using this library the work with Google Drive becomes easy and requires a few code to implement 
all necessary actions within RPA.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-drive)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-google-drive</artifactId>
    <version>1.0</version>
</dependency>
```

Additionally, to let the library collaborate with RPA platform make sure that Maven dependency to corresponding adapter 
is added also. 

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>1.0</version>
</dependency>
```

### Getting of file from Drive

The Google Drive library has really easy to use and perceive functionality. It separates necessary configuration steps 
from actual code that allows to keep it clear. Lets consider the case of getting specific file from Google Drive by 
its name.    
```Java
@Inject
private GoogleDrive googleDrive;

public void execute() {
    
    Optional<GFile> summaryReportFile = googleDrive.getFile("Summary Report");

    if (summaryReportFile.isPresent()) {
       InputStream is = summaryReportFile.get().getContent();
       ...
   }
}
```

The `GoogleDrive` is a main class of Google Drive library. It provides all necessary to work with Google Drive
functions. But before calling of any its function the underlay service `Drive` of Google Drive API should be authorized
and created. To do it the `GoogleDrive` should be provided with **OAuth client JSON** necessary for authentication on 
Google Cloud. This JSON can be provided explicitly via `secret()` method but implicit way via `@Inject` annotation 
as in example above is recommended. In case of injection of `GoogleDrive` using `@Inject` annotation the OAuth client 
JSON is expected to be defined via configuration parameter of the RPA process with key **`google.services.auth.secret`**. 
The value of this parameter is an alias of secret vault entry with necessary JSON. 
 ```properties
 google.services.auth.secret=robot.google.account
 ``` 

 ```properties
 robot.google.account=<secret OAuth client JSON>
 ``` 

How to get this OAuth client JSON? In order to work with Google Drive API it's necessary to have Google Cloud 
project with configured authentication and authorization for using this API. Follow next steps to do everything 
properly.

1. [Create a Google Cloud project][create_project_link] if it doesn't exist yet. 
2. [Enable Google Drive API][enable_apis_link] in the Google Cloud project.
3. [Configure OAuth consent screen][configure_oauth_consent_link] to let robot requests the access to Google Drive.
4. [Create OAuth client ID credentials][create_credentials_link] to authenticate the robot on Google Cloud.

Read [how Google Workspace APIs authentication and authorization works][auth_overview_link] and
[Authentication Best Practices][best_practices_link] for some more useful information.

[create_project_link]: https://developers.google.com/workspace/guides/create-project
[enable_apis_link]: https://developers.google.com/workspace/guides/enable-apis
[auth_overview_link]: https://developers.google.com/workspace/guides/auth-overview
[configure_oauth_consent_link]: https://developers.google.com/workspace/guides/configure-oauth-consent
[create_credentials_link]: https://developers.google.com/workspace/guides/create-credentials#oauth-client-id
[best_practices_link]: https://www.google.com/support/enterprise/static/gapps/docs/admin/en/gapps_workspace/Google%20Workspace%20APIs%20-%20Authentication%20Best%20Practices.pdf

After creating of OAuth client ID the corresponding OAuth client JSON can be downloaded by the following way:
1. Open the [Google Cloud Console](https://console.cloud.google.com)
2. At the top-left, click **Menu > APIs & Services > Credentials**.
3. Lookup a record with created previously **OAuth client ID**.
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

### Other examples

Please refer to [Google Drive Examples](../../examples#google-drive) to see more examples of using this library.

### Configuration parameters

This library uses **Google Services** library for authorization and creation of underlying `Drive` class of 
Google Drive API and hence its configuration parameters are actual for this library too. Please refer to 
[Google Services configuration parameters section](../google-services#configuration-parameters) to see their 
descriptions.  