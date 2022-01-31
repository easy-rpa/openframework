# Working with different database types

This example demonstrates the work with different database types. 

The functionality of Database library doesn't depend on specific database type and the code of using it look the same 
in all cases. But to be able to connect to specific database type it's necessary to add corresponding database driver 
as Maven dependency. For convenience the Database library has already have some drivers as Maven dependencies. 
These are drivers for:
* MySQL
* PostgreSQL
* MS SQL Server
* Oracle
* DB2
  
Corresponding database driver is loaded based on the value of `jdbcUrl` parameter when the method `withConnection()` or 
`withTransaction()` is called.   

The information regarding different JDBC URL formats for different type of databases can be found 
[here](https://www.baeldung.com/java-jdbc-url-format).

* #### Work with MySQL

Example of connection parameters to connect to MySQL database:
```json
{
    "jdbcUrl":"jdbc:mysql://localhost:33060/dbname",
    "user": "root",
    "password": "root"
}
```

Below an example of using these parameters that are specified as value of secret vault entry with alias `"mysql.db"`:
```Java
@Inject
private DatabaseService dbService;

public void execute() {
    dbService.withConnection("mysql.db", (c) -> {
        ResultSet results = c.executeQuery("SELECT * FROM invoices");

        while (results.next()) {
            log.info("Invoice Number: {}", results.getString("invoice_number"));
        }
    });
}
```

* ### Work with PostgreSQL

Example of connection parameters to connect to MySQL database:
```json
{ 
    "jdbcUrl":"jdbc:postgresql://localhost:5432/postgres", 
    "user": "postgres", 
    "password": "root" 
}
```

Below an example of using these parameters that are specified as value of secret vault entry with alias `"postgres.db"`:
```Java
@Inject
private DatabaseService dbService;

public void execute() {
    dbService.withConnection("postgres.db", (c) -> {
        ResultSet results = c.executeQuery("SELECT * FROM invoices");

        while (results.next()) {
            log.info("Invoice Number: {}", results.getString("invoice_number"));
        }
    });
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
6. Run `main()` method of `WorkingWithDifferentDbModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/database/working-with-different-db
