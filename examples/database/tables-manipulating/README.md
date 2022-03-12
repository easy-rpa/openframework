# Database tables manipulating

This example demonstrates using of Database library functionality for working with database tables.

* #### Create table to store specific entities

Using ORMLite annotations `@DatabaseTable` and `@DatabaseField` it's possible to tie the Java class and its fields 
with specific database table and its columns. If the database table is not exist these annotations can provide enough 
information to create such table from scratch.
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

Here for class `Invoice` is specified relation with database table `invoices`. Annotations `@DatabaseField` provides 
all necessary information about data types of each field/column. Now, the `invoices` table can be easily created as 
follows:   

```Java
@Configuration(value = "invoices.db.params")
private String invoicesDbParams;

@Inject
private DatabaseService dbService;

public void execute() {
    dbService.withConnection(invoicesDbParams, (c) -> {
        c.createTable(Invoice.class);
    });
}
```

> For more information about `@DatabaseTable` and `@DatabaseField` annotations and using of them please refer to 
> corresponding [ORMLite documentation](https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Class-Setup).

* #### Drop entity table

By the same way if we have Java class tied with specific database table this table can be easily dropped using 
tied Java class.

```Java
@Configuration(value = "invoices.db.params")
private String invoicesDbParams;

@Inject
private DatabaseService dbService;

public void execute() {
    dbService.withConnection(invoicesDbParams, (c) -> {
        c.dropTable(Invoice.class);
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
6. Run `main()` method of `TablesManipulatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/database/tables-manipulating

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>    
    <tr><td valign="top"><code>invoices.db.params</code></td><td>
        The alias of secret vault entry with parameters necessary for establishing connection with database. In case of 
        running of this example without EasyRPA Control Server, secret vault entries can be specified in the 
        <code>vault.properties</code> file. The value of secret vault entry in this case should be a JSON string with 
        following structure encoded with Base64:<br>
        <br>
        <code>{"jdbcUrl":"jdbc:postgresql://localhost:5432/postgres", "user": "postgres", "password": "root"}</code>    
    </td></tr>
</table> 