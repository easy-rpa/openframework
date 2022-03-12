# Waiting of message based on specified condition

This example periodically looks up email messages with specific keywords in subject or body. This process ends when 
email message with corresponding keywords is present or appears in mailbox.

```Java
@Inject
private EmailClient emailClient;

public void execute() throws ExecutionException, InterruptedException {
    List<String> LOOKUP_KEYWORDS = Arrays.asList("database", "storage");
    
    List<EmailMessage> messages = emailClient.waitMessages(msg -> {
        log.info("Check message '{}'", msg.getSubject());
        msg.markRead();
        boolean subjectContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getSubject()::contains);
        boolean bodyContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getText()::contains);
        return subjectContainsKeywords || bodyContainsKeywords;
    }, Duration.ofMinutes(30), Duration.ofSeconds(5)).get();
    
    log.info("Retrieved messages:");
    messages.forEach(msg -> {
        log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
    });
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
6. Run `main()` method of `MessageWaitingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/email/message-waiting

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
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
</table> 