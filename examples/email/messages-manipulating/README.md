# Mailbox messages manipulating

This process example show how it's possible to perform different actions with email messages using Email package 
functionality.

* #### Forward message
```java
    public void execute() {
        EmailClient emailClient = new EmailClient();
        String messageId = "testEmail";
        
        EmailMessage message = emailClient.fetchMessage(messageId);
        
        EmailSender emailSender = new EmailSender();
        String FORWARDED_MESSAGE_TEXT = "This email has been forwarded by EasyRPA robot.";
        String forwardedEmailRecipients = "test@gmail.com";
        emailSender.send(message.forwardMessage(true).recipients(forwardedEmailRecipients).html(FORWARDED_MESSAGE_TEXT));
    }
```

* #### Reply on message
```java
    public void execute() {
        EmailClient emailClient = new EmailClient();
        String messageId = "testEmail";
        EmailMessage message = emailClient.fetchMessage(messageId);

        EmailSender emailSender = new EmailSender();
        String REPLY_MESSAGE_TEXT = "EasyRPA robot replied to this email.";

        emailSender.send(message.replyMessage(true).html(REPLY_MESSAGE_TEXT));
    }
```

* #### Mark messages as Read/Unread
```java
    public void execute() {
        EmailClient emailClient = new EmailClient();
        
        String messageId = "testEmail";
        EmailMessage message = emailClient.fetchMessage(messageId);
        
        message.markRead();
        message.markUnRead();
    }
```

* #### Delete message
```java
    public void execute() {
        EmailClient emailClient=new EmailClient();

        String messageId="testEmail";
        EmailMessage message=emailClient.fetchMessage(messageId);
        
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

## Configuration
All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `outbound.email.server` | Host name and port of email server for outbound emails |
| `outbound.email.protocol` | Protocol which is used by email server for outbound emails |
| `outbound.email.secret` | Vault alias that contains credentials for authentication on the email server for outbound emails |
| `inbound.email.server` | Host name and port of inbound email server |
| `inbound.email.protocol` | Protocol which is used by inbound email server |
| `inbound.email.secret` | Vault alias that contains credentials for authentication on the inbound email server |
| `email.sender.name` | Name of email sender that will be display as "from" for email recipients |
| `forwarded.email.recipients` | Email address where email message will be forwarded |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |