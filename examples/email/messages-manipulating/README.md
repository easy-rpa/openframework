# Mailbox messages manipulating

This example demonstrates using of Email library functionality to perform different actions with email messages.

* #### Forward message
```java
@Inject
private EmailClient emailClient;

@Inject
private EmailSender emailSender;

public void execute() {
    String sourceMessageId = "123";
    String forwardMessageRecipients = "user@example.com";
    String forwardMessageText = "This email has been forwarded by robot.";

    log.info("Forwarding of message with id '{}'.", sourceMessageId);
    EmailMessage message = emailClient.fetchMessage(sourceMessageId);
    
    log.info("Forward the message to '{}'.", forwardedEmailRecipients);
    emailSender.send(message.forwardMessage(true).recipients(forwardMessageRecipients).text(forwardMessageText));
}
```

* #### Reply on message
```java
@Inject
private EmailClient emailClient;

@Inject
private EmailSender emailSender;

public void execute() {
    String sourceMessageId = "123";
    String replyMessageText = "Robot replied to this email.";

    log.info("Replying on message with id '{}'.", sourceMessageId);
    EmailMessage message = emailClient.fetchMessage(sourceMessageId);

    emailSender.send(message.replyMessage(true).html(replyMessageText));
}
```

* #### Mark message as Read/Unread
```java
@Inject
private EmailClient emailClient;

public void execute() {
    String sourceMessageId = "123";

    EmailMessage message = emailClient.fetchMessage(messageId);
    message.markRead();
    
    emailClient.updateMessage(message);
}
```

* #### Delete message
```java
@Inject
private EmailClient emailClient;

public void execute() {
    String sourceMessageId = "123";
    EmailMessage message = emailClient.fetchMessage(messageId);
    
    emailClient.deleteMessage(message);
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
6. Run `main()` method of `MessagesManipulatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/email/messages-manipulating

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>outbound.email.server</code></td><td>
        The host name or IP-address of outbound email server. 
    </td></tr>
    <tr><td valign="top"><code>outbound.email.protocol</code></td><td>
        The name of protocol which should be used for interaction with outbound email server. 
    </td></tr>
    <tr><td valign="top"><code>outbound.email.secret</code></td><td>
        The alias of secret vault entry with credentials for authentication on the outbound email server. In case of 
        running of this example without EasyRPA Control Server, secret vault entries can be specified in the 
        <code>vault.properties</code> file. The value of secret vault entry in this case should be a JSON string with 
        following structure encoded with Base64:<br>
        <br>
        <code>{ "user": "sender@example.com", "password": "passphrase" }</code>    
    </td></tr>
    <tr><td valign="top"><code>inbound.email.server</code></td><td>
        The host name or IP-address of inbound email server. 
    </td></tr>
    <tr><td valign="top"><code>inbound.email.protocol</code></td><td>
        The name of protocol which should be used for interaction with inbound email server. 
    </td></tr>
    <tr><td valign="top"><code>inbound.email.secret</code></td><td>
        The alias of secret vault entry with credentials for authentication on the inbound email server. In case of 
        running of this example without EasyRPA Control Server, secret vault entries can be specified in the 
        <code>vault.properties</code> file. The value of secret vault entry in this case should be a JSON string with 
        following structure encoded with Base64:<br>
        <br>
        <code>{ "user": "user@example.com", "password": "passphrase" }</code>    
    </td></tr>
    <tr><td valign="top"><code>email.sender.name</code></td><td>
        The name of sender that will be displayed in the field "From:" of email message.
    </td></tr>
    <tr><td valign="top"><code>forwarded.email.recipients</code></td><td>
        The list of email addresses delimited with <code>;</code> whom the email message will be forwarded.<br> 
        <br>
        Exp: <code>user1@example.com</code> or <code>user1@example.com;user2@example.com;user3@example.com</code>     
    </td></tr>
</table> 
