# Google Drive

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Creating of file](#creating-of-file)
* [Moving file to a specific location](#creating-of-file)
* [Other Example](#other-examples)

### Description

Component which provides functionality related to Google Drive.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-drive)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-google-drive</artifactId>
    <version>1.0</version>
</dependency>
```

Additionally, to let the library collaborate with RPA platform make sure that Maven dependency to corresponding adapter 
is added also. 

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>1.0</version>
</dependency>
```

### Creating of file

In this example we will create a file in Google Drive, and after we'll move it to a specific location.

First step - we need to configure Google Drive credentials in order to connect:

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

Note: All necessary configuration files can be found in `src/main/resources` directory.

Next step - inside the main program we inject object of 'GoogleDrive':

```java
    @Inject
    private GoogleDrive drive;
```

And simply use a command to create a new file:

```java
Optional<GoogleFile> file = drive.createFile("test");
```

Now new file is available on Google Drive.

### Moving file to a specific location

In this section we will continue previous case and move previously created file to a specific folder.
To achieve this we simply use a command called 'moveFile':

```java
public class MoveFile extends ApTask {

    private final String newFolderName = "newDirectory";

    @Inject
    private GoogleDrive drive;

    public void execute() {

        log.info("Creating file");
        Optional<GoogleFile> file = drive.createFile(fileName);

        Optional<GoogleFolderInfo> folder = drive.getFolder(newFolderName);

        if (file.isPresent() && folder.isPresent()) {
            log.info("File created in root directory ");

            drive.moveFile(file.get(), folder.get());

            log.info("Now file in directory '{}'", newFolderName);
        } else {
            log.info("File wasn't created or directory not found");
        }
    }
}
```

### Other Examples

Please refer to [Excel Examples](../../examples#google-drive) to see more examples of using this library.