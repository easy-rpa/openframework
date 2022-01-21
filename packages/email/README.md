# Email

### Table of Contents
* [Description](#description)
* [Usage](#usage)
* [Sending of simple message](#sending-of-simple-message)
* [Creating of complex messages using FreeMarker templates](#creating-of-complex-messages-using-freemarker-templates)
* [Searching and reading of inbound messages](#searching-and-reading-of-inbound-messages)
* [Other Examples](#other-examples)
* [Configuration parameters](#configuration-parameters)
* [Supported protocols](#supported-protocols)

### Description

The Email library provides convenient and easy to use functionality for working with email messages within RPA 
processes. The functionality is adapted for RPA processes. It hides lots of implementation details and configuration 
steps behind and focuses on actual actions that are easy to read and perceive when looking at the code. 
  
### Usage

To start use the library first you need to add corresponding Maven dependency to your project:

![mavenVersion](https://img.shields.io/maven-central/v/eu.ibagroup/easy-rpa-openframework-email)

```java
<dependency>
    <groupId>eu.ibagroup</groupId>
    <artifactId>easy-rpa-openframework-email</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Sending of simple message

The sending of email message using this library looks as follows: 
```java
@Inject
private EmailSender emailSender;

public void execute() {
    ...        
    emailSender.send(new EmailMessage().recipients("user@example.com").subject("Hello message").text("Hello World!"));
    ...
}
```
The first step in this example is a construction of new `EmailMessage`. The `EmailMessage` is a class representing 
specific email message. It has all necessary methods to specify its parameters and content. In some complex cases when 
the content of message is large or it depends on lots of other parameters its recommended to make custom extensions of 
this class as described in the [next part](#creating-of-complex-messages-using-freemarker-templates) of this tutorial. 
 
As soon as the email message is created it can be send. The Email library provides special service for this purpose 
called `EmailSender`. It has only one function 'send' but to make it work it's necessary to specify parameters of 
outbound email server. In case of injection of this service using `@Inject` annotation as in the example above these 
parameters are expected to be defined in configuration parameters of the RPA process. 

The example below demonstrates specifying of parameters necessary for message sending using GMail SMTP server:
```properties
outbound.email.server=smtp.gmail.com
outbound.email.protocol=smtp_over_tls
outbound.email.secret=email.user
``` 

Parameter keys are predefined and Email library expects them. In this example:
 * `outbound.email.server` - defines the host name or IP-address of outbound email server. 
 * `outbound.email.protocol` - the name of supported by Email library protocol used for interaction with outbound 
 email server. The `"smtp_over_tls"` means 'interaction based on SMTP protocol encrypted using TLS protocol'. 
 The default port for this protocol is 587. If it's necessary to use another port it can specified with the host 
 name of `outbound.email.server` parameter (e.g. 'smtp.gmail.com:587'). The full list of supported protocols and their 
 names can be found [here](#supported-protocols).
 * `outbound.email.secret` - the alias name of secret vault entry that contains information necessary for
 authentication on the outbound email server. In most cases this information is a JSON string with following structure:
 ```json
     {         
         "user": "sender@example.com",
         "password": "passphrase"
     }
 ```

Optionally, almost all parameters of `EmailMessage` also can be specified within configuration parameters of the RPA 
process. But their keys are predefined only partially. Parameter keys of `EmailMessage` depends on the value of special 
parameter `typeName` that can be passed into constructor of `EmailMessage`. The default value of this parameter is 
`email`. Below is shown how is possible to simplify the initial example by defining of some email message parameters in 
configuration.    
```properties
outbound.email.server=smtp.gmail.com
outbound.email.protocol=smtp_over_tls
outbound.email.secret=email.user
hello.email.recipients=user1@example.com;user2@example.com
hello.email.subject=Hello message
``` 
```java
@Inject
private EmailSender emailSender;

public void execute() {
    ...        
    emailSender.send(new EmailMessage("hello.email").text("Hello World!"));
    ...
}
```
The value of `typeName` in this example is `"hello.email"`. This parameter helps to distinguish different groups of 
`EmailMessage` constant parameters and easily reuse them in the code.  
 
The full list of possible configuration parameters can be found [here](#configuration-parameters).

If configuration parameters of the RPA process are not available by some reason or it's necessary to work with 
several outbound email servers at the same time the following way can be used.
```java
EmailSender emailSender = new EmailSender()
                              .server("smtp.gmail.com")
                              .protocol(OutboundEmailProtocol.SMTP_OVER_TLS)
                              .secret("{ \"user\": \"sender@example.com\", \"password": \"passphrase\" }");
emailSender.send(new EmailMessage().recipients("user@example.com").subject("Hello message").text("Hello World!"));
```

### Creating of complex messages using FreeMarker templates

Lets consider the case of sending email message with proposition to buy books in bookshop. 
<p align="center">
  <img src="https://i.postimg.cc/cCkNj11j/message-screenshot.png">
</p>

The content of this email is a styled HTML. It has the table with list of proposing books that is not static and formed 
based on data provided as input. The forming of such content in the code is possible but it's not convenient. It makes 
the code messy, poorly perceived and as result brings unnecessary difficulties. Especially in case of changes to 
get different output. As better solution the Email library supports using of FreeMarker template as email content.

[FreeMarker](https://freemarker.apache.org/) engine generates text output based on template and input data. 
Template is written in the FreeMarker Template Language (FTL). It allows to define the actual text output as well as 
how the input data should be presented in this output. 

The input data for books proposition email is a list of books that are necessary to display in the table. Lets take that 
each item in the list is a instance of some class `Book` representing specific book and contains all necessary info 
about it.
```java
@Data
public class Book {

    private final String name;
    private final String author;
}
```
In this case the content of books proposition email can be defined using following FreeMarker template.
> For better convenience it's recommended to move out the FreeMarker template into separate file with extension `.ftl`. 

*books_proposition.ftl*
```html
<html>
    <head>
        <#include "/email_templates/books_proposition.css">
    </head>
    <body>
        <div class="message-body">
            <p>
              Dear customer,
            <p/>
            <p>
              Today our bookshop can propose you to buy following books
            </p>
            <table>
                <tr><th class="first">Name</th><th>Author</th></tr>
                <#list books as book>
                    <tr class="row${book_index % 2}"><td>${book.getName()}</td><td>${book.getAuthor()}</td></tr>
                </#list>
            </table>
        </div>
    </body>
</html>
```
> The `books_proposition.css` contains CSS styles necessary to make this HTML markup looks the same as on image above. 
Its content can be found 
[here](../../examples/email/template-based-message-creating/src/main/resources/email_templates/books_proposition.css) 

This FreeMarker template expects the list `books` as input. It loops over this list and builds corresponding HTML table
row for each item. 

For more details about possibilities of FreeMarker templates see
 [FreeMarker Manual](https://freemarker.apache.org/docs/index.html)
 
When the email content depends on lots of input data all these data should be collected somewhere. It's recommended to 
define custom classes that extends `EmailMessage` class and represents specific email messages. The custom email class 
can be used for collecting and preparation of all inputs necessary to generate it's content. According to this the class 
representing books proposition email looks as follows:
```java
public class BooksPropositionEmail extends EmailMessage {

    private static final String TYPE_NAME = "books.proposition.email";
    private static final String TEMPLATE_FILE_PATH = "/email_templates/books_proposition.ftl";
    private static final String SUBJECT = "Books In Stock";

    private List<Book> books;
    
    // Using of @Inject annotation here allows injecting of this message directly 
    // into place of using it without necessity of separate injecting of EmailSender. 
    // When EmailSender is provided in constructor of EmailMessage, the message can be send using its method send().  
    @Inject
    public BooksPropositionEmail(EmailSender sender) {
        super(TYPE_NAME, sender);
    }

    public BooksPropositionEmail setBooksInfo(List<Book> books) {
        this.books = books;
        return this;
    }

    @Override
    protected void beforeSend() {
        // Specifying the email subject.
        // Here can be any logic based on which the subject can be changed.
        subject(SUBJECT);

        // Path to corresponding FTL file as body.
        html(TEMPLATE_FILE_PATH);

        // Specifying of properties that are used as inputs for FTL file.
        property("books", books);
    }
}
```
The method `beforeSend` is called as first step when called the method `send` of `EmailSender`. It's a place where all 
necessary input data is already provided and ready to use. To make the FreeMarker engine aware about this input data
the method `property` should be called for each input. The first argument is a name of corresponding variable in the 
FreeMarker template. 

Finally, the creating and sending of books proposition email can be done in several lines of code:
```java
@Inject
private BooksPropositionEmail booksPropositionEmail;

public void execute() {
    ...    
    List<Book> books = new ArrayList<>();
    books.add(new Book("Thinking in Java", "Bruce Eckel"));
    books.add(new Book("Le avventure di Cipollino", "Giovanni Francesco Rodari"));
    books.add(new Book("War and Peace", "Lev Tolstoy"));
    
    booksPropositionEmail.setBooksInfo(books).send();
    ...
}
```

> Specifying of parameters for outbound email server is skipped here. 
See [the full code of this example](../../examples/email/template-based-message-creating) for more details.

### Searching and reading of inbound messages 

For working with mailbox folders and email messages in them the Email library provides special service 
called `EmailClient`. It has functions for searching, waiting, reading and manipulation of email messages as well 
as managing of mailbox folders. The searching and reading of email messages using this service looks as follows: 
```java
@Inject
private EmailClient emailClient;

public void execute() {
    ...        
    List<String> LOOKUP_KEYWORDS = Arrays.asList("database", "storage");

    log.info("Fetch messages that contain any of '{}' keywords in subject or body.", LOOKUP_KEYWORDS);
    List<EmailMessage> messages = emailClient.fetchMessages(msg -> {
        boolean subjectContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getSubject()::contains);
        boolean bodyContainsKeywords = LOOKUP_KEYWORDS.stream().anyMatch(msg.getText()::contains);
        return subjectContainsKeywords || bodyContainsKeywords;
    });

    log.info("List fetched messages:");
    messages.forEach(msg -> {
        log.info("'{}' from '{}'", msg.getSubject(), msg.getSender().getPersonal());
    });
    ...
}
```

To make `EmailClient` service work it's necessary to specify parameters of inbound email server. In case of injection 
of this service using `@Inject` annotation as in the example above these parameters are expected to be defined in 
configuration parameters of the RPA process. 

Below the example of specifying parameters for GMail IMAP server:
```properties
inbound.email.server=imap.gmail.com
inbound.email.protocol=imap_over_tls
inbound.email.secret=mailbox
``` 

Parameter keys are predefined and Email library expects them. In this example:
 * `inbound.email.server` - defines the host name or IP-address of inbound email server. 
 * `inbound.email.protocol` - the name of supported by Email library protocol used for interaction with inbound 
 email server. The `"imap_over_tls"` means 'interaction based on IMAP protocol encrypted using TLS protocol'. 
 The default port for this protocol is 993. If it's necessary to use another port it can specified with the host 
 name of `inbound.email.server` parameter (e.g. 'imap.gmail.com:993'). The full list of supported protocols and their 
 names can be found [here](#supported-protocols).
 * `inbound.email.secret` - the alias name of secret vault entry that contains information necessary for
 authentication on the inbound email server. In most cases this information is a JSON string with following structure:
 ```json
     {         
         "user": "user@example.com",
         "password": "passphrase"
     }
 ```

If configuration parameters of the RPA process are not available by some reason or it's necessary to work with 
several inbound email servers at the same time the following way can be used.
```java
EmailClient emailClient = new EmailClient()
                              .server("imap.gmail.com")
                              .protocol(InboundEmailProtocol.IMAP_OVER_TLS)
                              .secret("{ \"user\": \"user@example.com\", \"password": \"passphrase\" }");
List<EmailMessage> messages = emailClient.fetchMessages();
...
```

### Other Examples

Please refer to [Email Examples](../../examples#email) to see more examples of using this library.

### Configuration parameters

Below the full list of possible parameters that the Email library expects in configuration parameters of the 
RPA process.
<table>
    <tr><th>Parameter</th><th>Value</th></tr>
    <tr><td valign="top"><code>outbound.email.server</code></td><td>
        The host name or IP-address of outbound email server that is necessary for the work of <code>EmailSender</code>.
        Optionally, the port number can be specified here too if it is different from default one for used protocol.<br>
        <br>
        Exp: <code>smtp.gmail.com</code> or <code>smtp.gmail.com:587</code> 
    </td></tr>
    <tr><td valign="top"><code>outbound.email.protocol</code></td><td>
        The name of protocol used for interaction with outbound email server. This parameter is necessary for 
        the work of <code>EmailSender</code>. The full list of supported protocols and their names can be 
        found <a href="#supported-protocols">here</a>.<br>
        <br>
        Exp: <code>smtp_over_tls</code>
    </td></tr>
    <tr><td valign="top"><code>outbound.email.secret</code></td><td>
        The alias name of secret vault entry that contains information necessary for authentication on the outbound 
        email server. This parameter is necessary for the work of <code>EmailSender</code>. The value of secret vault
        entry should be a JSON string with following structure:<br>
        <code>{ "user": "sender@example.com", "password": "passphrase" }</code> 
    </td></tr>
    <tr><td valign="top"><code>inbound.email.server</code></td><td>
        The host name or IP-address of inbound email server that is necessary for the work of <code>EmailClient</code>.
        Optionally, the port number can be specified here too if it is different from default one for used protocol.<br>
        <br>
        Exp: <code>imap.gmail.com</code> or <code>imap.gmail.com:993</code>         
    </td></tr>
    <tr><td valign="top"><code>inbound.email.protocol</code></td><td>
        The name of protocol used for interaction with inbound email server. This parameter is necessary for 
        the work of <code>EmailClient</code>. The full list of supported protocols and their names can be 
        found <a href="#supported-protocols">here</a>.<br>
        <br>
        Exp: <code>imap_over_tls</code>        
    </td></tr>
    <tr><td valign="top"><code>inbound.email.secret</code></td><td>
        The alias name of secret vault entry that contains information necessary for authentication on the inbound 
        email server. This parameter is necessary for the work of <code>EmailClient</code>. The value of secret vault
        entry should be a JSON string with following structure:<br>
        <code>{ "user": "user@example.com", "password": "passphrase" }</code>    
    </td></tr>
    <tr><td valign="top"><code>mailbox.default.folder</code></td><td>
        The name of default folder with inbound messages of the mailbox. This parameter is used by 
        <code>EmailClient</code>.<br><br>
        If this folder is not specified the <code>"INBOX"</code> folder is used as default.
    </td></tr>
    <tr><td valign="top"><code>[typeName].sender.name</code></td><td>
        The display name of the actual sender of <code>EmailMessage</code> with corresponding <code>typeName</code>. 
        The actual sender is set by email service and corresponds to email account on behalf of which the message is 
        sent. If the field <code>From:</code> of email message is not defined explicitly the email address string of 
        the actual sender will be displayed here. By defining this parameter it's possibly to replace  the email 
        address string of the actual sender with some custom name.<br>
        <br>
        Exp: <code>John Doe</code> 
    </td></tr>
    <tr><td valign="top"><code>[typeName].from</code></td><td>
        The email address displayed in the field <code>From:</code> of <code>EmailMessage</code> with corresponding 
        <code>typeName</code>. This value can be different from the actual email sender.<br>
        <br>
        Exp: <code>user1@example.com</code> 
    </td></tr>
    <tr><td valign="top"><code>[typeName].recipients</code></td><td>        
        The list of email addresses delimited with <code>;</code> who are recipients of <code>EmailMessage</code> with 
        corresponding <code>typeName</code>. These email addresses displayed in the field <code>To:</code> of the email 
        message.<br>
        <br>
        Exp: <code>user1@example.com</code> or <code>user1@example.com;user2@example.com;user3@example.com</code>         
    </td></tr>
    <tr><td valign="top"><code>[typeName].cc.recipients</code></td><td>
        The list of email addresses delimited with <code>;</code> who are CC recipients of <code>EmailMessage</code> 
        with corresponding <code>typeName</code>. These email addresses displayed in the field <code>CC:</code> of the 
        email message.<br>
        <br>
        Exp: <code>user1@example.com</code> or <code>user1@example.com;user2@example.com;user3@example.com</code>         
    </td></tr>
    <tr><td valign="top"><code>[typeName].bcc.recipients</code></td><td>
        The list of email addresses delimited with <code>;</code> who are BCC recipients of <code>EmailMessage</code> 
        with corresponding <code>typeName</code>. These email addresses displayed in the field <code>BCC:</code> of the 
        email message.<br>
        <br>
        Exp: <code>user1@example.com</code> or <code>user1@example.com;user2@example.com;user3@example.com</code>         
    </td></tr>
    <tr><td valign="top"><code>[typeName].reply.to</code></td><td>
        The list of email addresses delimited with <code>;</code> who are supposed recipients of the replying on 
        <code>EmailMessage</code> with corresponding <code>typeName</code>.<br>
        <br>
        Exp: <code>user1@example.com</code> or <code>user1@example.com;user2@example.com;user3@example.com</code>         
    </td></tr>
    <tr><td valign="top"><code>[typeName].subject</code></td><td>
        The subject of <code>EmailMessage</code> with corresponding <code>typeName</code>.
    </td></tr>        
    <tr><td valign="top"><code>[typeName].body.tpl</code></td><td>
        The content of <code>EmailMessage</code> with corresponding <code>typeName</code>. Here can be actual text 
        or HTML or it can be a path to Freemarker Template File (*.ftl) in the resources of the RPA process module.<br>
        <br>
        Exp: <code>Hello World!</code> or <code>email_templates/summary_email.ftl</code>    
    </td></tr>
    <tr><td valign="top"><code>[typeName].charset</code></td><td>
        The name of charset used for encoding the content of <code>EmailMessage</code> with corresponding 
        <code>typeName</code>.<br>
        <br>
        Exp: <code>UTF-8</code> or <code>iso-8859-1</code> etc. 
    </td></tr>    
</table> 

### Supported protocols

For interaction with outbound email server the Email library supports the following protocols that can be specified 
for `EmailSender` using method `protocol()` or as value of `outbound.email.protocol` parameter.

| Outbound email protocol | Default port | Description                                           |
|-------------------------|:------------:|:------------------------------------------------------|
| `smtp`                  | 25           | Not encrypted SMTP protocol.                          |
| `smtp_over_tls`         | 587          | SMTP protocol encrypted using TLS (STARTTLS enabled). |
| `smtps`                 | 465          | SMTP protocol encrypted using SSL.                    |



Below the list of protocols that can be specified for `EmailClient` using method `protocol()` or as value of 
`inbound.email.protocol` parameter for interaction with inbound email server.

| Inbound email protocol | Default port | Description                        |
|------------------------|:------------:|:-----------------------------------|
| `pop3`                 | 110          | Not encrypted POP3 protocol.       |
| `pop3_over_tls`        | 995          | POP3 protocol encrypted using TLS. |
| `pop3s`                | 995          | POP3 protocol encrypted using SSL. |
| `imap`                 | 143          | Not encrypted IMAP protocol.       |
| `imap_over_tls`        | 993          | IMAP protocol encrypted using TLS. |
| `imaps`                | 993          | IMAP protocol encrypted using SSL. |


