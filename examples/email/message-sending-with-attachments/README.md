# Sending of message with attachments

This example sends email message with attached file.

```Java
@Inject
private EmailSender emailSender;

@Override
public void execute() throws IOException {
    String subject = "Test email with attachment";
    String body = "This message was sent by EasyRPA Bot and has attached file.";
    File testFile = new File("test.xlsx");

    log.info("Send message with attached file.");
    new EmailMessage(emailSender).subject(subject).text(body).attach(testFile).send();
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
6. Run `main()` method of `MessageSendingWithAttachmentsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/email/message-sending-with-attachments

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
    <tr><td valign="top"><code>email.sender.name</code></td><td>
        The name of sender that will be displayed in the field "From:" of email message.
    </td></tr>
    <tr><td valign="top"><code>email.recipients</code></td><td>
        The list of email addresses delimited with <code>;</code> who are recipients of email message. These email 
        addresses displayed in the field <code>To:</code> of the message.<br>
        <br>
        Exp: <code>user1@example.com</code> or <code>user1@example.com;user2@example.com;user3@example.com</code>     
    </td></tr>
</table>
