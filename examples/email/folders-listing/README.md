# Getting a list of existing mailbox folders

This example gets the list of existing mailbox folders.

```Java
@Inject
private EmailClient emailClient;

public void execute() {
    List<String> folders = emailClient.listFolders();

    log.info("Fetched folders:");
    folders.forEach(log::info);
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
6. Run `main()` method of `FoldersListingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/email/folders-listing

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
