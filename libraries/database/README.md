# Database

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Simple SELECT from database](#simple-select-from-database)
* [Select records from a table as collection of Java objects](#select-records-from-a-table-as-collection-of-java-objects)
* [Other examples](#other-examples)
* [Supported databases](#supported-databases)

### Description

The Database library provides convenient and easy to use functionality for working with remote databases within RPA
processes. Actually this library wraps functionality of [ORMLite](https://ormlite.com) library and organize the process 
of initialization and establishing of database connection in more clear way with less amount of code. As ORMLite it 
supports most popular types of databases (like MySQL, Postgres etc.). But the functionality does not depends of 
database type which makes the process of switching between different databases very simple and fast.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-database)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-database</artifactId>
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


### Simple SELECT from database

Before executing of any query to database it's necessary to configure connection to it. The Database library supports 
two ways to do it:
1. Using secret vault and configuration services of RPA platform.
2. Via specifying of connection parameters directly in the code of the process using `DatabaseParams`. This way can be 
used when necessary services of RPA platform are not available by some reason or when values of connection parameters 
depend on some other parameters and have to be determined in the code. 

The first way is preferable that's why further we will focus on it. Necessary for establishing connection parameters
should be specified within secret vault entry of RPA platform. Its value is a JSON with following structure:
```json
{
    "jdbcUrl":"jdbc_url",
    "user": "db_user",
    "password": "db_user_password"
}
```

There are 3 parameters have to be defined here:
* `jdbcUrl` - the JDBC URL for connection. E.g. `"jdbc:postgresql://localhost:5432/postgres"`. The information 
regarding different JDBC URL formats for different type of databases can be found 
[here](https://www.baeldung.com/java-jdbc-url-format).
* `user` - the name of database user using to perform authentication during connection.
* `password` -  the password of database user using to perform authentication during connection.

As soon as connection parameters are configured in the secret vault they can be used in the code of the process. Lets 
consider the example below that performs simple SELECT query. 
```java
@Inject
private DatabaseService dbService;

public void execute() {
    dbService.withConnection("testdb", (c) -> {
        ResultSet rs = c.executeQuery("SELECT * FROM invoices");
        while (rs.next()) {
            int id = rs.getInt("invoice_number");
            String customerName = rs.getString("customer_name");
            double amount = rs.getDouble("amount");
            log.info("invoice_number = {}, customer_name = {}, amount = {}", id, customerName, amount);
        }
    });
}
```
Here the `testdb` is an alias of secret vault entry with necessary connection parameters. The `DatabaseService` uses 
this information to get parameters from secret vault and establish connection when the method `withConnection()` is 
called. 

The method `withConnection()` is fully responsible for established connection. It keeps the connection, provides it 
and closes it in the end or in case of errors. The second argument is actions need to be performed using database 
connection. They are defined as lambda expression with argument `c`. The `c` is an instance of `DatabaseConnection` 
class representing established connection. It has convenient and easy to use functions to work with database. When 
the performing of this lambda expression is end the connection is closed.

Instead of direct passing of secret vault alias it's recommended to pass it through the configuration parameter of RPA 
platform. It provides more flexibility in case of necessity to switch from one database to another. E.g. switching 
between test and production environments.      
```java
@Configuration("invoices-db")
private String invoicesDbAlias;

@Inject
private DatabaseService dbService;

public void execute() {
    dbService.withConnection(invoicesDbAlias, (c) -> {
       ...
    });
}
```

In case of using `DatabaseParams` the same example will look as follows:
```java
DatabaseParams dbParams = new DatabaseParams().url("jdbc_url").user("db_user").pass("db_user_password");

new DatabaseService().withConnection(dbParams, (c)->{
    ResultSet rs = c.executeQuery("SELECT * FROM invoices");
    while (rs.next()) {
       int id = rs.getInt("invoice_number");
       String customerName = rs.getString("customer_name");
       double amount = rs.getDouble("amount");
       log.info("invoice_number = {}, customer_name = {}, amount = {}", id, customerName, amount);
    }
});
```   

### Select records from a table as collection of Java objects

The main purpose of ORMLite library is a mapping of database table records to related Java objects. This is a powerful 
and convenient functionality that is strongly recommended to use in case of working with single database table or view.

The ORMLite provides `@DatabaseTable` and `@DatabaseField` annotations that help to tie the Java class and its 
fields with specific database table and its columns. Lets consider the following example of `Invoice` class 
relating with `invoices` table of some database.
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
}
```

The `@DatabaseTable` annotation is applied on class level and specifies the database table to which the class is 
related. 

Respectively the `@DatabaseField` annotation is applied on field level and describes the column of database table to 
which the field is related. It accepts long list of parameters which related to data types or define specific behavior 
for some cases. 

For more information about `@DatabaseTable` and `@DatabaseField` annotations and using of them please refer to 
corresponding [ORMLite documentation](https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Class-Setup).
 
In the process code a simple selection of all records from table `invoices` and mapping of them to Java objects 
of `Invoice` class looks as follows:
```java
@Inject
private DatabaseService dbService;

@Override
public void execute() {

    List<Invoice> invoices = dbService.withConnection("testdb", (c) -> {
        return c.selectAll(Invoice.class);
    });

    log.info("Fetched records:");
    for (Invoice invoice : invoices) {
        log.info("{}", invoice);
    }
}
```

The `DatabaseConnection` (the `c` attribute) has a set of functions that wrap corresponding functions of ORMLite's DAO 
object and helps to avoid the direct using of it. It hides unnecessary specifics of ORMLite library and the 
code becomes more clear. 

### Other examples

Please refer to [Database Examples](../../examples#database) to see more examples of using this library.

### Supported databases

The Database library uses ORMLite and supports everything that is supported by ORMLite. It's only necessary to add 
corresponding database driver as Maven dependency. But for convenience this library has already have some drivers as 
Maven dependencies. It's not necessary to do anything more in case of using databases in the list below.
* MySQL
* PostgreSQL
* MS SQL Server
* Oracle
* DB2
