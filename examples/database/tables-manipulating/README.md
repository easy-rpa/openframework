# Database tables manipulating

* ### Create table to store specific entities

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Creating of tables for entities '{}' and '{}'", Invoice.class.getName());
        dbService.withConnection("testdb", (c) -> {
            c.createTableIfNotExists(Invoice.class);
        });

        log.info("Tables are created successfully.");
    }
```

* ### Drop entity table

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Dropping of tables for '{}' and '{}'", Invoice.class.getName());
        dbService.withConnection("testdb", (c) -> {
            c.dropTable(Invoice.class);
        });

        log.info("Table is dropped successfully.");
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
6. Run `main()` method of `TablesManipulatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/database/tables-manipulating
