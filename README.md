<p align="center">
  <img height="100px" src="https://i.postimg.cc/FKDhP2kT/Easy-RPA-Full-Logo.png">
</p>

# EasyRPA Open Framework

## Table of Contents
* [Introduction](#introduction)
* [Quick Start](#quick-start)
* [Example](#example)
* [Libraries](#libraries)
* [Links](#links)
* [License](#license)

## Introduction
Usually to implement a process which automates work with different document formats and services such as Excel, PDF, Google, AWS, etc. it takes time to explore and investigate them and to find the necessary Java libraries. In addition, the functionality found in such libraries is poorly adapted for usage in RPA processes.

Open Framework is a collection of open-source Java libraries for Robotic Process Automation (RPA) designed to be used with EasyRPA platform. 
Open Framework contains functional to work with popular documents and services. It's usage simplifies development of RPA processes based on Java language.

The project is:
- 100% Open Source
- Optimized for EasyRPA platform
- Accepting external contributions

![License](https://img.shields.io/github/license/easyrpa/openframework?color=blue)

## Quick Start

EasyRPA Open Framework consist of several independent libraries. All of them are deployed on Maven Central repository.

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/database)
![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-email)
![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-excel)
![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-google-drive)
![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-google-sheets)

In order to use any of EasyRPA Open Framework's library you need simply add it as a dependency in your Maven POM file. 
E.g.:
```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-email</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

After that you need to build the project via Maven command:
```java
mvn clean install
```

In result  you should see following message:
<p align="center">
  <img src="https://i.postimg.cc/s2Dmc3w1/Screenshot-1.png">
</p>

Note

Java 1.8 or higher is required

## Example

After installation of the library it can be imported and used inside of your RPA process task:

```java
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.email.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.EmailSender;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Send Message")
public class SendMessage extends ApTask {

    private static final String SUBJECT = "Test email";
    private static final String BODY = "This message was sent by EasyRPA Bot";

    @Inject
    private EmailSender emailSender;

    @Override
    public void execute() {
        log.info("Send message");
        new EmailMessage(emailSender).subject(SUBJECT).html(BODY).send();

        log.info("Messages have been sent successfully");
    }
}

```

## Libraries
The EasyRPA Open Framework currently includes the following libraries:

| Name                                    | Description |
|:----------------------------------------|:---------------------------------------------------------------------------|
| [Database](packages/database)           | Interact with databases (MySQL, PostgreSQL, Oracle, DB2, MS SQL Server) |
| [Email](packages/email)                 | E-Mail operations |
| [Excel](packages/excel)                 | Includes functionality related to Excel and CSV files. For optimal performance, all of the following functions can be used through the backend services without communicating with physical application |
| [Google-drive](packages/google-drive)   | Operations with files and folders |
| [Google-sheets](packages/google-sheets) | Manipulate Google Sheets files directly |

## Links

Here is you can find useful links to other resources:

* [StackOverFlow](https://ru.stackoverflow.com/search?q=openframework)

## License
This project is open-source and licensed under the terms of the [Apache License 2.0](https://apache.org/licenses/LICENSE-2.0).