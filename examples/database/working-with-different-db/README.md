# Working with different database types

* ### Work with MySQL

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        String SELECT_ALL_INVOICES_SQL = "SELECT * FROM invoices";
        
        log.info("Execute query to MySQL database");
        dbService.withConnection("mysql.db", (c) -> {
            ResultSet results = c.executeQuery(SELECT_ALL_INVOICES_SQL);
            log.info("Fetched results:");
            while (results.next()) {
                log.info("Invoice Number: {}", results.getString("invoice_number"));
            }
        });
    }
```

* ### Work with PostgreSQL

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        String SELECT_ALL_INVOICES_SQL = "SELECT * FROM invoices";

        log.info("Execute query to PostgreSQL database");
        dbService.withConnection("postgres.db", (c) -> {
            ResultSet results = c.executeQuery(SELECT_ALL_INVOICES_SQL);
            log.info("Fetched results:");
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
