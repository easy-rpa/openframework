# Azure Services

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [API client service authorization and instantiation](#api-client-service-authorization-and-instantiation)
* [Other examples](#other-examples)
* [Configuration parameters](#configuration-parameters)

### Description

EasyRPA Open Framework **Azure Services** library provides functionality to perform authentication, authorization and
instantiation of [Azure API client services](https://docs.microsoft.com/en-us/azure/app-service) like OneDrive, OneNote, 
Outlook/Exchange, People (Outlook contacts) etc. It hides lots of implementation and configuration details behind 
that is very important in case of using it within RPA process. When the business logic of the process should be easy 
to read and perceive and implementation details should not interfere to do it.  

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-services)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-azure-services</artifactId>
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

### API client service authorization and instantiation

The key feature of Google Services library is a `GoogleServiceProvider`. It's helper class that covers all steps
related to Google Workspace API authorization within requested scope and instantiation of corresponding
API client service. As result, using this class it's possible to create a new authorized instance of API client
service with one line of code.

Below the example of using `GoogleServiceProvider` to create a new instance of `Calendar` client service of
Google Calendar API.  

### Other examples

Please refer to [Google Services Examples](../../examples#google-services) to see more examples of using this library.

### Configuration parameters

Below the full list of possible parameters that the Azure Services library expects in configuration parameters of the
RPA process.
<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>azure.services.auth.clientID</code></td><td>
      Name of configuration parameter with clientID of your Azure app registration.<br>
      <br>
      For information regarding how to find your Client ID see 
      <a href="https://docs.microsoft.com/en-us/graph/tutorials/java?tabs=aad&tutorial-step=1">Azure app registration</a><br>
      <br>
    </td></tr>  
    <tr><td valign="top"><code>azure.services.auth.secret</code></td><td>
       Name of configuration parameter with secret information necessary to perform authentication on Azure server..<br>
        <br>
        Exp: <code>C:\Users\Default\AppData\Local\Google\tokens</code><br>
        <br>
        For information regarding persisting of OAuth 2.0 access tokens see 
        <a href="https://developers.google.com/api-client-library/java/google-api-java-client/oauth2#data_store">Data Store</a>
        section<br>        
    </td></tr>    
    <tr><td valign="top"><code>azure.services.graphUserScopes</code></td><td>
        Name of configuration parameter with list of necessary API permissions for your app.<br>                 
        <br>
        Exp: user.read,mail.read,mail.send,mail.readwrite</code> 
    </td></tr>
</table> 
