# Google Drive

## Table of Contents
* [Description](#description)
* [Quick start](#quick-start)
* [Configuration](#configuration)
* [How To](#how-to)
* [Example](#example)

## Description

Component which provides functionality related to Google Drive.

## Quick start

To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-google-drive)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-google-drive</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

After that you need build the project via Maven command:

```java
mvn clean install
```
After execution of this command you should see such message:

![Screenshot-1.png](https://i.postimg.cc/s2Dmc3w1/Screenshot-1.png)

## Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

## How To

* **Move file**
```java
package file_moving.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFile;
import eu.ibagroup.easyrpa.openframework.googledrive.folder.GoogleFolderInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Move File")
public class MoveFile extends ApTask {

    private final String fileName = "test";

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

* **Get list of files**
```java
package getting_files.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Get Files")
public class GetFiles extends ApTask {

    @Inject
    private GoogleDrive drive;

    public void execute() {
        log.info("Getting list of all files");
        List<GoogleFileInfo> files = drive.listFiles();

        log.info("List of files:");
        files.forEach(file -> log.info("Name: '{}' id: '{}'",file.getName(),file.getId()));
    }
}
```

* **Get directory info**
```java
package file_dir_info.task;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.googledrive.GoogleDrive;
import eu.ibagroup.easyrpa.openframework.googledrive.file.GoogleFileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApTaskEntry(name = "Get File/Dir Info")
public class GetFileDirInfo extends ApTask {

    private static final String fileName = "newTest";

    @Inject
    private GoogleDrive drive;

    public void execute() {

        log.info("Getting file with the name '{}'", fileName);
        Optional<GoogleFileInfo> file = drive.getFileInfo(fileName);

        file.ifPresent(ob -> {
            log.info("File has name '{}' id '{}'", ob.getName(), ob.getId());
            log.info("File has type '{}' and size '{}' bytes", ob.getFileType(), ob.getSize());
            log.info("File has parents: ");
            ob.getParents().forEach(parent -> log.info("Parent id: {} ", parent));
        });
    }
}
```

## Example

For more code examples please refer to corresponding [article](https://github.com/dzyap/openframework/tree/main/examples#google-drive). 