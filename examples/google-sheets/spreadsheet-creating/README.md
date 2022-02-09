## Creating of new Spreadsheet

This example creates new Google Spreadsheet file using collection of specific Java objects.

```Java

```
> The `insertTable()` inserts a new table where rows contains data of passed Java objects into it. The specification of 
table header and its cells styling are specified using `@GSheetColumn` annotations within Java class of passed objects.
See the full definition of 
[Passenger](src/main/java/eu/ibagroup/easyrpa/examples/google/sheets/spreadsheet_creating/entities/Passenger.java)
class for all details.
>
See the full source of this example for more details or check following instructions to run it.

### Running

> :warning: **To be able to build and run this example it's necessary to have an access
> to some instance of EasyRPA Control Server.**

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
4. If necessary, change version of `easy-rpa-engine-parent` in the same `pom.xml` to corresponding version of EasyRPA
   Control Server:
    ```xml
    <parent>
        <groupId>eu.ibagroup</groupId>
        <artifactId>easy-rpa-engine-parent</artifactId>
        <version>[Replace with version of EasyRPA Control Server]</version>
    </parent>
    ```

5. Build it using `mvn clean install` command. This command should be run within directory of this example.
6. Run `main()` method of `SheetsCreatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/google/sheets/spreadsheet_creating

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>google.services.auth.secret</code></td><td>
        Vault alias that contains credentials for authentication on the google server
        </td></tr>
    <tr><td valign="top"><code>sample.data.file</code></td><td>
        Path to JSON file that contains sample data for this process. It can be path on local file system or within 
        resources of this module.
    </td></tr>
</table>