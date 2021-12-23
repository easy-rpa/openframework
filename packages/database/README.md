# Database

## Table of Contents
* [Description](#description)
* [Supported DBs](#supported-dbs)
* [Usage](#usage)
* [Read MYSQL table content](#read-mysql-table-content)
* [Insert record to SQL Server table](#insert-record-to-sql-server-table)
* [Configuration](#configuration)
* [Example](#example)

## Description

Component which provides functionality to communicate with external databases.

## Supported DBs
* MySQL
* PostgreSQL
* Oracle
* DB2
* MS SQL Server

## Usage
To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/database)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>database</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Read MYSQL table content

In most often cases during interaction with Databases we need to get some data from it. In this example we will describe how to read the data from MYSQL table.

The first step in such case would be configuration of connection to a specific table. There are 2 parameters responsible for this:

**apm_run.properties**

| Parameter     | Value                                  |
| ------------- |----------------------------------------|
| `mssql.url` | MS SQL DB address                      |

**vault.properties**

| Parameter     | Value                                  |
| ------------- |----------------------------------------|
| `mssql.credentials` | MS SQL credentials used to connect     |

These necessary configuration parameters located inside **src/main/resources** folder.

To describe table's fields we have added **MySqlInvoice** class:

```java
@DatabaseTable(tableName = "invoices")
public class MySqlInvoice {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int id;

    @DatabaseField(columnName = "invoice_number", canBeNull = false)
    private int invoiceNumber;

    @DatabaseField(columnName = "invoice_date", dataType = DataType.DATE,
            format = Constants.DB_DATE_PATTERN)
    public Date invoiceDate;

    @DatabaseField(columnName = "customer_name", canBeNull = false)
    private String customerName;

    @DatabaseField(canBeNull = false)
    private double amount;

    public MySqlInvoice() {
    }
}
```

After that inside your class in order to use DB functionality you need simply inject it:

```java
    @Inject
    MySqlService dbService;
```

And the last step of getting all data from table - call of one method 'selectALL':

```java

        List<MySqlInvoice> allInvoices = dbService.withConnection(MySqlInvoice.class, (ex) ->
                ex.selectAll(MySqlInvoice.class)
        );
```


## Insert record to SQL Server table

As in previous example first we inject necessary DB object to our program (don't forget about configuration parameters related to connection):

```java
    @Inject
    SQLServerService dbService;
```

Next step - we prepare records to insert:

```java
List<String> queries = new ArrayList();
        queries.add("INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
                "VALUES ('000001', '2019-7-04', 'At&T', '5000.76');");
        queries.add("INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
                "VALUES ('000002', '2012-02-15', 'Apple', '12320.99');");
```

And the final action - execution of function 'executeInsert':

```java
        List<ResultSet> rs = new ArrayList<>();
        dbService.withTransaction((ex) -> {
            for (String query : queries) {
                rs.add(ex.executeInsert(query));
            }
            return null;
        });
```

## Configuration

**apm_run.properties**

| Parameter     | Value                                  |
| ------------- |----------------------------------------|
| `mssql.url` | MS SQL DB address                      |
| `mssql.credentials` | MS SQL credentials used to connect     |
| `com.microsoft.sqlserver.jdbc.SQLServerDriver` | MS SQL Server Driver                   |
| `postgres.url` | PostgreSQL DB address                  |
| `postgres.credentials` | PostgreSQL credentials used to connect |


## Example

For more code examples please refer to corresponding [article](https://github.com/easyrpa/openframework/tree/main/examples#database). 