# Getting information about selected file/directory

Example of process that gets information about a file on Google Drive

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();
        String fileName = "newTest";

        log.info("Getting file with the name '{}'", fileName);
        Optional<GoogleFileInfo> file = drive.getFileInfo(fileName);
        
        log.info("File has name '{}' id '{}'", file.getName(), file.getId());
        log.info("File has type '{}' and size '{}' bytes", file.getFileType(), file.getSize());
        log.info("File has parents: ");
        file.getParents().forEach(parent -> log.info("Parent id: {} ", parent));
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
6. Run `main()` method of `FileDirInfoModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/google-drive/file-dir-info

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64.<br> |