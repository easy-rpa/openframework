# Sending of simple Outlook email message

This example sends simple Outlook email message.

```Java
    @Inject
    private GraphServiceProvider graphServiceProvider;

    @Override
    public void execute()  {
        GraphServiceClient<Request> graphClient = graphServiceProvider.getGraphServiceClient();

final Message message = new Message();
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        String[] recipients = simpleEmailRecipients.split(",");
        for(String emailRecipient: recipients) {
        Recipient toRecipient = new Recipient();
        toRecipient.emailAddress = new EmailAddress();
        toRecipient.emailAddress.address = emailRecipient;
        toRecipientsList.add(toRecipient);
        }
        message.toRecipients = toRecipientsList;
        
        graphClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();
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
6. Run `main()` method of `OutlookMessageSendingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/azure-services/outlook-message-sending
### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

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
   <tr><td valign="top"><code>mail.recipients</code></td><td>
        Name of configuration parameter with email addresses of recipients of this email message.<br>  
        <br>
        Exp: user1@email.com,user2@email.com
    </td></tr>
      <tr><td valign="top"><code>mail.subject</code></td><td>
        Name of configuration parameter with subject of your email message.
      <br>
    </td></tr>
     <tr><td valign="top"><code>mail.body</code></td><td>
        Name of configuration parameter with text body of your email message.
      <br>  
     </td></tr>
</table> 