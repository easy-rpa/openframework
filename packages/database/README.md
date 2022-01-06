# Database

## Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Simple SELECT from a DataBase](#read-mysql-table-content)
* [Select from a table using Java object](#select-from-a-table-using-java-object)
* [Example](#example)

## Description

Component which responsible for communication with external databases within RPA processes.
This implementation provides functionality of ORMLite library with easier way of its initialization and establishing of database connection.


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

## Simple SELECT from a DataBase

In most often cases during interaction with Databases we need to get some data from it. In this example we will describe how to perform a simple SELECT query.

The first step in such case would be configuration of connection to a specific table. There is 1 parameter responsible for this:

**vault.properties**

| Parameter     | Value                                                                                                  |
| ------------- |--------------------------------------------------------------------------------------------------------|
| `testdb` | JSON value encoded in Base64 format which contains all the information for connection to a specific DB |

Decoded value looks like this: { "jdbcUrl":"jdbc:postgresql://localhost:5432/postgres", "user": "postgres", "password": "root" }

As you can see there are 3 parameters in this JSON.

First tells which JDBC driver to use and remote DB address: "jdbcUrl":"jdbc:postgresql://localhost:5432/postgres"

Second specifies a user which will be used for connection: "user": "postgres"

And the third - user's password: "password": "root"

These necessary configuration parameters located inside **src/main/resources** folder.



After that inside your class in order to use DB functionality you need simply inject it:

```java
    @Inject
    private DatabaseService dbService;
```

Also we specify our SQL query:
```java
    private final String SELECT_INVOICES_SQL = "SELECT * FROM invoices WHERE invoice_date < '%s';";
```

And the last step of getting data from the table - call of our SQL query using function `withConnection` and `executeQuery` after that:

```java
    @Override
    public void execute() {
        String oldDate = DateTime.now().minusYears(3).toString("yyyy-MM-dd");

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

!*Note* In this example inside `withConnection` function we used hardcoded value `testdb`. The value tells which alias from the `vault.properties` to take. But in real life it's not recommended. The better way is to pass this value through the configuration parameter:

```java
    @Configuration("invoices-db-alias")
    private String invoicesDbAlias;

    dbService.withConnection(invoicesDbAlias, (c) -> {......
```

Such approach will allow you simply switch between Production and Test environments without a code changes.


## Select from a table using Java object

As in previous example first we inject necessary DB object to our program (don't forget about configuration parameters related to connection):

```java
    @Inject
    private DatabaseService dbService;
```

Next step - we prepare our Java class which describes fields of a table. This class will be used to automatically transform the data we get from the table to Java object:

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

And the final action - execution of function `withConnection` and `selectAll` with class `Invoice` specified inside as a parameter:

```java
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