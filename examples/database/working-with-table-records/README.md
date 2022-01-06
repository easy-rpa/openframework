# Working with database table records

* ### Read table records

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Reading existing records in the table that is used to store entity '{}'", Invoice.class.getName());
        List<Invoice> invoices = dbService.withConnection("testdb", (c) -> {
            return c.selectAll(Invoice.class);
        });
    }
```

* ### Add new table records

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        List<Invoice> invoicesToAdd = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Invoice.DB_DATE_FORMAT);
        invoicesToAdd.add(new Invoice(10001, dateFormat.parse("2021-01-22"), "Sony", 4500));
        invoicesToAdd.add(new Invoice(10002, dateFormat.parse("2014-04-03"), "Lenovo", 5400.87));

        log.info("Adding of new records to the table that is used to store entity '{}'", Invoice.class.getName());
        dbService.withConnection("testdb", (c) -> {
            c.create(invoicesToAdd);
        });
    }
```

* ### Update table records

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        Date oldDate = DateTime.now().minusYears(3).toDate();

        log.info("Check and find outdated invoices in the table");
        dbService.withConnection("testdb", (c) -> {
        List<Invoice> outdatedInvoices = c.queryBuilder(Invoice.class)
            .where().le("invoice_date", oldDate).query();

        log.info("Amount of found invoices: {}", outdatedInvoices.size());

        log.info("Change outdated flag for found invoices");
        for (Invoice outdatedInvoice : outdatedInvoices) {
            outdatedInvoice.setOutdated(true);
        }

        log.info("Update changed invoices in the table");
            c.update(outdatedInvoices);
        });

        log.info("Records in the table updated successfully.");
    }
```

* ### Delete table records

```Java
    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Find invoices marked as outdated in the table");
        dbService.withConnection("testdb", (c) -> {
            List<Invoice> outdatedInvoices = c.queryBuilder(Invoice.class)
                .where().eq("outdated", true).query();

        log.info("Amount of found invoices: {}", outdatedInvoices.size());

        log.info("Delete outdated invoices from the table");
            c.delete(outdatedInvoices);
        });

        log.info("Records deleted from the table successfully.");
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
6. Run `main()` method of `WorkingWithTableRecordsModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/database/working-with-table-records
