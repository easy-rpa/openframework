# Database

## Table of Contents
* [Description](#description)
* [Supported DBs](#supported-dbs)
* [Quick start](#quick-start)
* [Configuration](#configuration)
* [How To](#how-to)
* [Example](#example)

## Description

Component which provides functionality to communicate with external databases.

## Supported DBs
* MySQL
* PostgreSQL
* Oracle
* DB2
* MS SQL Server

## Quick Start
To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/database)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>database</artifactId>
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

**apm_run.properties**

| Parameter     | Value                                  |
| ------------- |----------------------------------------|
| `mssql.url` | MS SQL DB address                      |
| `mssql.credentials` | MS SQL credentials used to connect     |
| `com.microsoft.sqlserver.jdbc.SQLServerDriver` | MS SQL Server Driver                   |
| `postgres.url` | PostgreSQL DB address                  |
| `postgres.credentials` | PostgreSQL credentials used to connect |

## How To
In this section you can find examples of most popular actions you can perform using Email library from the Open Framework.

* **Print MYSQL table content**
```java
package eu.ibagroup.easyrpa.examples.database.mysql_orm_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.mysql_orm_data_manipulation.entity.MySqlInvoice;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Print MySQL table content", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    @Inject
    MySqlService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        log.info("Print table content:");
        List<MySqlInvoice> allInvoices = dbService.withConnection(MySqlInvoice.class, (ex) ->
                ex.selectAll(MySqlInvoice.class)
        );

        allInvoices.forEach(a -> {
            log.info("id: {}; invoice#: {}; invoice_date: {}; customer_name: {}; amount: {}",
                    a.getId(), a.getInvoiceNumber(), a.getInvoiceDate(), a.getCustomerName(), a.getAmount());
        });
    }
}
```

* **Call Postgres Stored Procedure**
```java
package eu.ibagroup.easyrpa.examples.database.postgres_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.postgres_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.MessageFormat;

@Slf4j
@ApTaskEntry(name = "Call Stored Procedure", description = "Call Stored Procedure which returns current date")
public class CallStoredProc extends ApTask {

    @Inject
    PostgresService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        final Date[] currentDate = {null};
        dbService.withConnection((ex) -> {
            ResultSet rs = ex.executeQuery("SELECT rpa.getdate()");
            while (rs.next()) {
                currentDate[0] = rs.getDate("getdate");
                log.info(MessageFormat.format("Stored procedure returned Current Date = {0}", currentDate[0]));
            }
            return currentDate[0];
        });
    }
}

```
* **Insert record to SQL Server table**
```java
package eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Save records in MS-SQL table", description = "Save records in MS-SQL table")
public class InsertRecordsToDB extends ApTask {
    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        List<String> queries = new ArrayList();
        queries.add("INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
                "VALUES ('000001', '2019-7-04', 'At&T', '5000.76');");
        queries.add("INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
                "VALUES ('000002', '2012-02-15', 'Apple', '12320.99');");

        List<ResultSet> rs = new ArrayList<>();
        dbService.withTransaction((ex) -> {
            for (String query : queries) {
                rs.add(ex.executeInsert(query));
            }
            return null;
        });
    }
}

```

## Example

For more code examples please refer to corresponding [article](https://github.com/easyrpa/openframework/tree/main/examples#database). 