# Google Drive files manipulating

This process example show how it's possible to perform different actions with Google Drive files using Google Drive package
functionality.

* ### Create file

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();

        String fileName = "creationTest";
        
        log.info("Creating file with the name '{}'", fileName);
        Optional<GoogleFile> file = drive.createFile(fileName);
    }
```

* ### Delete file

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();

        String fileName = "creationTest";

        log.info("Getting file from Google Drive");
        Optional<GoogleFile> file = drive.getFile(fileName);

        drive.deleteFile(file.getId());
    }
```

* ### Upload file

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();

        String fileName = "/myFile.txt";

        log.info("Creation file instance of '{}'", fileName);
        java.io.File file = new java.io.File(fileName);
        file.createNewFile();

        log.info("Uploading file to google drive");
        Optional<GoogleFile> googleFile = drive.createFile(file);
    }
```

* ### Rename file

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();

        String fileName = "myFile";

        log.info("Getting file '{}' from google drive", fileName);
        Optional<GoogleFile> file = drive.getFile(fileName);

        drive.renameFile(file.get(), "RenamedFile");
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
6. Run `main()` method of `FileManipulationsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/google-drive/files-manipulations

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64.<br> |