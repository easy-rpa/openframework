# Google Drive folders manipulating

This process example show how it's possible to perform different actions with Google Drive folders using Google Drive package
functionality.


* ### Create folder

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();
        
        String folderName = "creationTestFolder";
        
        log.info("Creating folder with the name '{}'", folderName);
        Optional<GoogleFolderInfo> file = drive.createFolder(folderName);
    }
```

* ### Rename folder

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();

        String folderName = "creationTestFolder";

        Optional<GoogleFolderInfo> file = drive.getFolder(folderName);
        drive.renameFolder(file.get(), "RenamedFolder");
    }
```

* ### Delete folder

```Java
    public void execute() {
        GoogleDrive drive = new GoogleDrive();

        String folderName = "RenamedFolder";

        log.info("Getting Folder from Google Drive");
        Optional<GoogleFolderInfo> file = drive.getFolder(folderName);

        drive.deleteFolder(file.getId());
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
6. Run `main()` method of `FoldersManipulationsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/google-drive/folders-manipulations

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64.<br> |

## Running

Run `main()` method of `LocalRunner` class.