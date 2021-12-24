# Creating of message based on FreeMarker template

This example shows how properly to create complex email message with large body that depends on many inputs. 

 ![Image](message_screenshot.png 'Complex message example')

In such cases much more convenient to keep and edit text of the body in separate file. This can be achieved using 
FreeMarker templates that are supported by Email component.

For more details about possibilities of FreeMarker templates see
 [FreeMarker Manual](https://freemarker.apache.org/docs/index.html)

```Java
    public void execute() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Thinking in Java", "Bruce Eckel"));
        books.add(new Book("Le avventure di Cipollino", "Giovanni Francesco Rodari"));
        books.add(new Book("War and Peace", "Lev Tolstoy"));

        EmailSender emailSender = new EmailSender();

        log.info("Create message using Email Sender and send it.");
        new BooksInStockEmail(emailSender).setBooksInfo(books).send();
    }
```

Custom class `BooksInStockEmail` :

```java
public class BooksInStockEmail extends EmailMessage {

    private static final String TYPE_NAME = "books.in.stock.email";

    private static final String TEMPLATE_FILE_PATH = "/email_templates/books_in_stock.ftl";

    private static final String SUBJECT = "Books In Stock";

    private List<Book> books;
    public BooksInStockEmail() {
        super(TYPE_NAME);
    }
    public BooksInStockEmail(EmailSender sender) {
        super(TYPE_NAME, sender);
    }
    public BooksInStockEmail setBooksInfo(List<Book> books) {
        this.books = books;
        return this;
    }

    @Override
    protected void beforeSend() {
        subject(SUBJECT);
        charset("UTF-8");
        html(TEMPLATE_FILE_PATH);
        property("books", books);
    }
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
6. Run `main()` method of `TemplateBasedMessageCreatingModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easyrpa/openframework/tree/main/examples/email/template-based-message-creating
 
## Configuration

All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `outbound.email.server` | Host name and port of email server for outbound emails |
| `outbound.email.protocol` | Protocol which is used by email server for outbound emails |
| `outbound.email.secret` | Vault alias that contains credentials for authentication on the email server for outbound emails |
| `email.sender.name` | Name of email sender that will be display as "from" for email recipients |
| `email.recipients` | Email address where email message will be sent |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |