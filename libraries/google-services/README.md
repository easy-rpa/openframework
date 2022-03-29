# Google Services

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [API client service authorization and instantiation](#api-client-service-authorization-and-instantiation)
* [Other examples](#other-examples)
* [Configuration parameters](#configuration-parameters)

### Description

The **Google Services** library provides functionality to perform authentication, authorization and instantiation of 
[Google Workspace API client services](https://developers.google.com/workspace/products) like Drive, Sheets,
Calendar etc. It hides lots of implementation and configuration details behind that is very important in case of using
 it within RPA process. When the business logic of the process should be easy to read and perceive and implementation 
details should not interfere to do it.  

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-services)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-google-services</artifactId>
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

### API client service authorization and instantiation

The key feature of Google Services library is a `GoogleServiceProvider`. It's helper class that covers all steps 
related to Google Workspace API authorization within requested scope and instantiation of corresponding 
API client service. As result, using this class it's possible to create a new authorized instance of API client 
service with one line of code. 

Below the example of using `GoogleServiceProvider` to create a new instance of `Calendar` client service of
Google Calendar API.  
```java
@Inject
private GoogleServicesProvider googleServicesProvider;

public void execute() {
    ...        
    Calendar calendar = googleServicesProvider.getService(Calendar.class, CalendarScopes.CALENDAR_EVENTS);
    ...
}
```  
For creating of `GoogleServicesProvider` the **OAuth client JSON** should be provided as information necessary 
for authentication on Google Cloud. In case of injection of `GoogleServicesProvider` using `@Inject` annotation 
this information is expected to be defined in configuration parameters of the RPA process under the key
 **`google.services.auth.secret`**.  The value of this parameter is an alias of secret vault entry with 
 OAuth client JSON. 
 
 ```properties
 google.services.auth.secret=robot.google.account
 ``` 

 ```properties
 robot.google.account=<secret OAuth client JSON>
 ``` 

How to get this OAuth client JSON? In order to work with Google Workspace API it's necessary to have Google Cloud 
project with configured authentication and authorization for using corresponding API. Follow next steps to do 
everything properly.

1. [Create a Google Cloud project][create_project_link] if it doesn't exist yet. 
2. [Enable the APIs that are going to be used][enable_apis_link] in the Google Cloud project.
3. [Configure OAuth consent screen][configure_oauth_consent_link] to let robot requests the access to necessary data.
4. [Create OAuth client ID credentials][create_credentials_link] to authenticate the robot.

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

Please refer to [Google Services Examples](../../examples#google-services) to see more examples of using this library.

### Configuration parameters

Below the full list of possible parameters that the Email library expects in configuration parameters of the 
RPA process.
<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>google.services.auth.secret</code></td><td>
      The alias of secret vault entry with OAuth client JSON necessary for authentication on Google Cloud.<br>
      <br>
      For information regarding how to configure OAuth client see 
      <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a><br>
      <br>        
      The value of secret vault entry should be a JSON with following structure:
      <pre>{
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
    }}
       </pre>    
    </td></tr>  
    <tr><td valign="top"><code>google.services.auth.token.stores.dir</code></td><td>
        Path to directory where StoredCredentials file should be created and located. The StoredCredentials file is 
        used to persist Google credential's access and refresh tokens that are necessary for accessing of Google Cloud.<br>
        <br>
        Exp: <code>C:\Users\Default\AppData\Local\Google\tokens</code><br>
        <br>
        For information regarding persisting of OAuth 2.0 access tokens see 
        <a href="https://developers.google.com/api-client-library/java/google-api-java-client/oauth2#data_store">Data Store</a>
        section<br>        
    </td></tr>    
    <tr><td valign="top"><code>google.services.auth.code.receiver</code></td><td>
        The host name or IP-address with port number of authorization code receiver on the robot machine. As soon as
        OAuth consent screen is confirmed the authorization code is generated and should be returned back to the 
        robot to let him continue the work. The robot opens a socket on the machine where he works and waits 
        response with authorization code.<br>
        <br>
        By default, the value of this parameter is <code>localhost:8888</code>. It means that consents steps can be 
        completed only on the same machine where robot is working that in case of unattended robots work is 
        not possible to do. To solve it the value of this parameter should be exact IP-address or host name of robot 
        machine. After that consents steps can be completed on another machine in the same network. For example, 
        the consent screen can be opened by human who will authorize robot to do some work.<br>                 
        <br>
        Exp: <code>172.156.65.78:8888</code> 
    </td></tr>
</table> 