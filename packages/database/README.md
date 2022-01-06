# Database

## Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Simple SELECT from database](#simple-select-from-database)
* [Select records from a table as collection of Java objects](#select-records-from-a-table-as-collection-of-java-objects)
* [Example](#example)

## Description

The database library provides convenient and easy to use functionality for working with remote databases within RPA processes. Actually this library wraps functionality of ORMLite library and organize the process of intialization and establishing of database connection in more clear way with less amount of code. As ORMLite it supports most popular types of databases (like MySQL, Postgres etc.). But the functionality does not depends of database type which makes the process of switch between different DBs very simple and fast.


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

## Simple SELECT from database

In most often cases during interaction with databases we need to get some data from it. In this example we will describe how to perform a simple SELECT query.

The first step in such case would be configuration of connection to a specific database. There is 1 parameter responsible for this:

**Secret Vault**

| Secret Vault entry | Value                                                                                                             |
|--------------------|-------------------------------------------------------------------------------------------------------------------|
| databaseAlias           | the key of secret vault entry that keeps necessary for establishing database connection parameters in JSON format |

Decoded value looks like this:
```java
    {
        "jdbcUrl":"jdbc:postgresql://localhost:5432/postgres",
        "user": "postgres",
        "password": "root"
    }
```

As you can see there are 3 parameters in this JSON:

* JDBC URL used for connection: "jdbcUrl":"jdbc:postgresql://localhost:5432/postgres"

* Database user used to perform authentication during connection: "user": "postgres"

* Database user password used to perform authentication during connection: "password": "root"



Before accessing database we inject an object of `DatabaseService` class. This is recommended way of creating it. But you cal also use a usual constructor.

After that we can simply get data from the table - call of our SQL query using function `withConnection` and `executeQuery`:

```java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        String oldDate = DateTime.now().minusYears(3).toString("yyyy-MM-dd");
        String SELECT_INVOICES_SQL = "SELECT * FROM invoices WHERE invoice_date < '%s';";

        log.info("Output invoices which are older than '{}'.", oldDate);
        dbService.withConnection("testdb", (c) -> {
            ResultSet rs = c.executeQuery(String.format(SELECT_INVOICES_SQL, oldDate));
            while (rs.next()) {
                int id = rs.getInt("invoice_number");
                Date invoiceDate = rs.getDate("invoice_date");
                String customerName = rs.getString("customer_name");
                double amount = rs.getDouble("amount");
                log.info("invoice_number = {}, invoice_date = {}, customer_name = {}, amount = {}", id, invoiceDate, customerName, amount);
            }
        });
    }
```

!*Note* In this example inside `withConnection` function we used hardcoded value `testdb`. The value tells which alias from the `Secret Vault` to take. But in real life it's not recommended. The better way is to pass this value through the configuration parameter:

```java
    @Configuration("invoices-db-alias")
    private String invoicesDbAlias;

    dbService.withConnection(invoicesDbAlias, (c) -> {......
```

Such approach will allow you simply switch between Production and Test environments without a code changes.


## Select records from a table as collection of Java objects

First step in this example - we prepare our Java class which describes fields of a table. This class will be used to automatically transform the data we get from the table to Java object:

```java
@Data
@DatabaseTable(tableName = "invoices")
public class Invoice {

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int id;

    @DatabaseField(columnName = "invoice_number", canBeNull = false)
    private int invoiceNumber;

    @DatabaseField(columnName = "invoice_date", dataType = DataType.DATE, format = DB_DATE_FORMAT)
    public Date invoiceDate;

    @DatabaseField(columnName = "customer_name", canBeNull = false)
    private String customerName;

    @DatabaseField(canBeNull = false)
    private double amount;

    @DatabaseField(canBeNull = false)
    private boolean outdated;
}
```

For more information about such classes and their configuration please refer to corresponding [article](https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Starting-Class) on ORMLite site.

As in previous example first we inject necessary DB object to our program (don't forget about configuration parameters related to connection).
And after that - execution of function `withConnection` and `selectAll` with class `Invoice` specified inside as a parameter:

```java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Reading existing records in the table that is used to store entity '{}'", Invoice.class.getName());
        List<Invoice> invoices = dbService.withConnection("testdb", (c) -> {
            return c.selectAll(Invoice.class);
        });

        log.info("Fetched records:");
        for (Invoice invoice : invoices) {
            log.info("{}", invoice);
        }
    }
```

## Example

For more code examples please refer to corresponding [article](https://github.com/easyrpa/openframework/tree/main/examples#database). 