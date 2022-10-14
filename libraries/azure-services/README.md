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
Outlook/Exchange, People (Outlook contacts) etc. It hides lots of implementation and configuration details behind, that
is very important in case of using it within RPA process. When the business logic of the process should be easy to read
and perceive and implementation details should not interfere to do it.

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

The key feature of Azure Services library is a `Microsoft Graph`. Microsoft Graph is the gateway to data and
intelligence in Microsoft 365. It provides a unified programmability model that you can use to access the tremendous
amount of data in Microsoft 365, Windows, and Enterprise Mobility + Security. Microsoft Graph exposes REST APIs and
client libraries to access data on the following Microsoft cloud services:

* Microsoft 365 core services: Bookings, Calendar, Delve, Excel, Microsoft 365 compliance eDiscovery, Microsoft Search,
  OneDrive, OneNote, Outlook/Exchange, People (Outlook contacts), Planner, SharePoint, Teams, To Do, Viva Insights
* Enterprise Mobility + Security services: Advanced Threat Analytics, Advanced Threat Protection, Azure Active
  Directory, Identity Manager, and Intune
* Windows services: activities, devices, notifications, Universal Print
* Dynamics 365 Business Central services

To simplify building applications that access Microsoft Graph we use `Microsoft Graph SDK`. The SDKs include two
components: a service library and a core library.

The service library contains models and request builders that are generated from Microsoft Graph metadata to provide a
rich, strongly typed, and discoverable experience when working with the many datasets available in Microsoft Graph.

The core library provides a set of features that enhance working with all the Microsoft Graph services. Embedded support
for retry handling, secure redirects, transparent authentication, and payload compression improve the quality of your
application's interactions with Microsoft Graph, with no added complexity, while leaving you completely in control. The
core library also provides support for common tasks such as paging through collections and creating batch requests.

Below you can see the example of using `GraphServiceProvider` class to create an instance of `GraphServiceClient` class
and sending a mail message.

```java
@Inject
private GraphServiceProvider graphServiceProvider;

public void execute(){
    
        GraphServiceClient<Request> graphClient=graphServiceProvider.getGraphServiceClient();

        Message message = new Message();
        
        graphClient.me()
            .sendMail(UserSendMailParameterSet
                .newBuilder()
                .withMessage(message)
                .build())
            .buildRequest()
            .post();
        }
```  

For creating of `GraphServiceProvider` you should provide all information that is necessary for authentication such as
client id, tenant id and list of necessary API permissions. In case of injection of `GraphServiceProvider` using
`@Inject` annotation this information is expected to be defined in configuration parameters of the RPA process under the
following keys:

* client id under **`azure.services.auth.client.id`**
* tenant id under **`azure.services.auth.tenant.id`**,
* list of API permissions under **`azure.services.graph.permission.list`**.

To get that information you are required to do the following steps:

1. [Create personal Microsoft account][create_microsoft_account] if it doesn't exist.
2. [Register new application in Azure Active Directory][create_project_link].
3. [Find your Client id and Tenant id][create_project_link], copy and put them under needed keys in *apm_run.properties*
   file.
4. On the left menu click on **API permissions** and [add permissions that you want][enable_api].

[create_project_link]: https://docs.microsoft.com/en-us/graph/tutorials/java?tabs=aad&tutorial-step=1

[create_microsoft_account]: https://account.microsoft.com/account?lang=en-hk

[enable_api]: https://docs.microsoft.com/en-us/azure/active-directory/develop/quickstart-configure-app-access-web-apis

### Other examples

Please refer to [Azure Services Examples](../../examples#azure-services) to see more examples of using this library.

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
    <tr><td valign="top"><code>azure.services.auth.tenantID</code></td><td>
       The Azure Tenant ID is a Global Unique Identifier (GUID) for your Microsoft 365 Tenant.
        it’s also referred to as the Office 365 Tenant ID.
      The ID is used to identify your tenant, and it’s not your organization name or domain name.<br>
        <br>
        For information regarding how to find your tenant ID see 
        <a href="https://docs.microsoft.com/en-us/graph/tutorials/java?tabs=aad&tutorial-step=1">Azure app registration</a>
        section<br>        
    </td></tr>    
    <tr><td valign="top"><code>azure.services.graphUserScopes</code></td><td>
        Name of configuration parameter with list of necessary API permissions for your app.<br>  
        <br>
        Here you can read some additional information about Microsoft graph permissions:
        <a href="https://docs.microsoft.com/en-us/graph/permissions-reference">Microsoft Graph permissions reference</a>
        <br>
        For information regarding how to set specific permissions you want, see
        <a href="https://docs.microsoft.com/en-us/azure/active-directory/develop/quickstart-configure-app-access-web-apis">Quickstart: Configure a client application to access a web API</a><br>
        <br>
        Exp: user.Read,mail.Read,mail.Send,mail.readwrite 
    </td></tr>
</table> 
