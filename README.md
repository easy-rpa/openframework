
<img height="80px" src="https://i.postimg.cc/FKDhP2kT/Easy-RPA-Full-Logo.png">

# EasyRPA Open Framework

### Table of Contents
* [Introduction](#introduction)
* [Usage](#usage)
* [Libraries](#libraries)
* [Examples](#examples)
* [Contributing](#contributing)
* [License](#license)

## Introduction

The Robotic Process Automation (RPA) supposes the doing of things in a way as human does it, via UI elements. But the 
work with different document formats or services such as Excel, PDF, Google Docs, etc. can be done without actual 
manipulation with UI elements. If RPA platform supports describing of robot scenarios based on the one of popular 
program language like Java, the automation of these things can be done using tens of existing functional libraries 
and APIs. Such approach allows significantly speedup the robot work and increase performance. At the same time, if you 
are not familiar with these libraries it takes much time to find them and investigate. Moreover, the found 
functionality will be poorly adapted for usage in the code of robot scenarios. As result the using of functional 
libraries and APIs can become a nightmare. 

**EasyRPA Open Framework** is a collection of open-source Java-libraries for Robotic Process Automation designed to be 
used with [EasyRPA](http://easyrpa.eu) platform. It keeps in one place libraries to work with most popular document 
formats and services. The functionality is clear and easy to use with minimal amount of preparation or configuration 
steps in the code that ``significantly simplifies development of RPA processes.

The project is:
- 100% Open Source
- Optimized for EasyRPA platform
- Accepting external contributions

![License](https://img.shields.io/github/license/easy-rpa/openframework?color=blue)

## Usage

EasyRPA Open Framework consist of several independent libraries. All of them are deployed on Maven Central repository.

In order to use any of EasyRPA Open Framework's library you need simply add it as a dependency in your Maven POM file. 
E.g.:
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-email</artifactId>
    <version>1.0.0</version>
</dependency>
```

Additionally, to let libraries collaborate with RPA platform it's necessary to add as dependency corresponding adapter. 
Since this framework initially was intended and optimized to work with EasyRPA platform currently only adapter for 
EasyRPA platform is implemented and supported.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>2.3.1</version>
</dependency>
```

> There is no limitation to implement similar adapter for any other RPA platform that uses Java program language for 
> describing of robot scenarios but it's out of this project scope.  

## Libraries

The EasyRPA Open Framework includes following libraries:

<table>
    <tr>
        <th align="left" width="150px">Name</th>
        <th align="center">Latest version</th>
        <th align="left">Description</th>
    </tr>
    <tr>
        <td valign="top"><a href="/libraries/database">Database</a></td>
        <td><img alt="Maven Central" src="https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-database"></td>
        <td>
            Functionality to work with remote databases (MySQL, PostgreSQL, Oracle, DB2, MS SQL Server).
        </td>
    </tr>
    <tr>
        <td valign="top"><a href="/libraries/email">Email</a></td>
        <td><img alt="Maven Central" src="https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-email"></td>
        <td>
            Functionality to work with mailboxes and email messages.
        </td>
    </tr>
    <tr>
        <td valign="top"><a href="/libraries/excel">Excel</a></td>
        <td><img alt="Maven Central" src="https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-excel"></td>
        <td>
            Functionality to work with Excel documents.
        </td>
    </tr>
    <tr>
        <td valign="top"><a href="/libraries/google-services">Google Services</a></td>
        <td><img alt="Maven Central" src="https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-services"></td>
        <td>
            Functionality to perform authentication, authorization and instantiation of Google Workspace API client 
            services like Drive, Sheets, Calendar etc.
        </td>
    </tr>   
    <tr>
        <td valign="top"><a href="/libraries/google-drive">Google Drive</a></td>
        <td><img alt="Maven Central" src="https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-drive"></td>        
        <td>
            Functionality to work with Google Drive files and folders. 
        </td>
    </tr>
    <tr>
        <td valign="top"><a href="/libraries/google-sheets">Google Sheets</a></td>
        <td><img alt="Maven Central" src="https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-sheets"></td>        
        <td>
            Functionality to work with Google Sheets. 
        </td>
    </tr>
</table> 

## Examples

Please refer to [Examples page](examples) to see the full list of examples of using EasyRPA Open Framework 
libraries.

## Contributing

Found a bug and it is necessary to make a fast fix? Wants to add a critical feature? Interested in contributing? Head 
over to the [Contribution guide](.github/CONTRIBUTING.md) to see where to get started.

## License
This project is open-source and licensed under the terms of the [Apache License 2.0](https://apache.org/licenses/LICENSE-2.0).
