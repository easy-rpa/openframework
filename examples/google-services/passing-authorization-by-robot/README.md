# Passing of Google authorization without side help 

Example of process that shows how the robot can authorize himself to perform some actions within his Google account.  

```Java
@Driver(value = DriverParams.Type.BROWSER, param =
        {@DriverParameter(key = DriverParams.Browser.SELENIUM_NODE_CAPABILITIES,
                initializer = BrowserDriver.DefaultChromeOptions.class)})
private BrowserDriver browserDriver;

@Configuration(value = "google.services.auth.credentials")
private String googleAccountCredentials;

@Configuration(value = "google.services.auth.2fa.secret.key")
private String googleAccountSecretKey;

@Inject
private VaultService vaultService;

@Inject
private GoogleDrive drive;

@AfterInit
public void init() {
    drive.onAuthorization(url -> {
        log.info("Authorization started");
        try (LoginPage loginPage = new OAuthConsentScreenApplication(browserDriver).open(url)) {
            SecretCredentials credentials = vaultService.getSecret(googleAccountCredentials);
            String oneTimeCode = getOneTimeCode(vaultService.getSecret(googleAccountSecretKey).getPassword().toUpperCase());

            loginPage.confirmLogin(credentials, oneTimeCode).grantAccess();

            log.info("Access granted");
        }
    });
}

public void execute() {
    List<GFileInfo> files = drive.listFiles();
    ...
}

private String getOneTimeCode(String googleAccountSecretKey) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(googleAccountSecretKey);
    String hexKey = Hex.encodeHexString(bytes);
    return TOTP.getOTP(hexKey);
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
6. Run `main()` method of `PassingAuthorizationByRobotModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/google-services/passing-authorization-by-robot

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>google.services.auth.secret</code></td><td>
        The alias of secret vault entry with OAuth 2.0 Client JSON necessary for authentication on the Google 
        server.<br>
        <br>
        For information regarding how to configure OAuth 2.0 Client see 
        <a href="https://developers.google.com/workspace/guides/create-credentials#oauth-client-id">OAuth client ID credentials</a><br>
        <br>         
        In case of running of this example without EasyRPA Control Server, secret vault entries can be specified in the 
        <code>vault.properties</code> file. The value of secret vault entry in this case should be a JSON string with 
        following structure encoded with Base64:<br>
        <pre>
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
         </pre>    
    </td></tr>    
</table>